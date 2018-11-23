package org.platform.modules.listen;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;

public class AsyncRecursiveDirectoryStream implements DirectoryStream<Path> {

	private LinkedBlockingQueue<Path> pathsBlockingQueue = new LinkedBlockingQueue<Path>();
	private boolean closed = false;
	private FutureTask<Void> pathTask;
	private Path startPath;
	@SuppressWarnings("rawtypes")
	private Filter filter;

	public AsyncRecursiveDirectoryStream(Path startPath, String pattern) throws IOException {

		this.startPath = Objects.requireNonNull(startPath);
	}

	@Override
	public Iterator<Path> iterator() {
		confirmNotClosed();
		findFiles(startPath, filter);
		return new Iterator<Path>() {
			Path path;

			@Override
			public boolean hasNext() {
				try {
					path = pathsBlockingQueue.poll();
					while (!pathTask.isDone() && path == null) {
						path = pathsBlockingQueue.poll(5, TimeUnit.MILLISECONDS);
					}
					return (path != null);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				return false;
			}

			@Override
			public Path next() {
				return path;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Removal not supported");
			}
		};
	}

	@SuppressWarnings("rawtypes")
	private void findFiles(final Path startPath, final Filter filter) {
		pathTask = new FutureTask<Void>(new Callable<Void>() {
			@SuppressWarnings("unchecked")
			@Override
			public Void call() throws Exception {
				Files.walkFileTree(startPath, new FunctionVisitor(getFunction(filter)));
				return null;
			}
		});
		start(pathTask);
	}

	private Function<Path, FileVisitResult> getFunction(final Filter<Path> filter) {
		return new Function<Path, FileVisitResult>() {
			@Override
			public FileVisitResult apply(Path input) {
				try {
					if (filter.accept(input.getFileName())) {
						pathsBlockingQueue.offer(input);
					}
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage());
				}
				return (pathTask.isCancelled()) ? FileVisitResult.TERMINATE : FileVisitResult.CONTINUE;
			}
		};
	}

	@Override
	public void close() throws IOException {
		if (pathTask != null) {
			pathTask.cancel(true);
		}
		pathsBlockingQueue.clear();
		pathsBlockingQueue = null;
		pathTask = null;
		filter = null;
		closed = true;
	}

	private void start(FutureTask<Void> futureTask) {
		new Thread(futureTask).start();
	}

	private void confirmNotClosed() {
		if (closed) {
			throw new IllegalStateException("DirectoryStream has already been closed");
		}
	}

}
