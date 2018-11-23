package org.platform.modules.listen;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import com.google.common.base.Function;

public class FunctionVisitor extends SimpleFileVisitor<Path> {
	
	private Function<Path, FileVisitResult> pathFunction;

    public FunctionVisitor(Function<Path, FileVisitResult> pathFunction) {
        this.pathFunction = pathFunction;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        return pathFunction.apply(file);
    }

}
