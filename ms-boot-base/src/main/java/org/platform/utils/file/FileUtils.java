package org.platform.utils.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(FileUtils.class);
	
	public static <T> List<T> readFromAbsolute(String src, LineHandler lineHandler) throws FileNotFoundException {
		return read(new FileInputStream(new File(src)), lineHandler);
	}
	
	public static <T> List<T> readFromClasspath(String src, LineHandler lineHandler) {
		return read(FileUtils.class.getClassLoader().getResourceAsStream(src), lineHandler);
	}
	
	public static <T> List<T> read(InputStream in, LineHandler lineHandler) {
		List<T> result = new ArrayList<T>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = br.readLine()) != null) {
				result.add(lineHandler.handle(line));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != in) in.close();
				if (null != br) br.close();
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return result;
	}
	
	public static void write(String dest, String... lines) {
		write(dest, Arrays.asList(lines));
	}
	
	public static void write(String dest, List<String> lines) {
		OutputStream out = null;
		BufferedWriter bw = null;
		try {
			out = new FileOutputStream(new File(dest));
			bw = new BufferedWriter(new OutputStreamWriter(out));
			for (int i = 0, len = lines.size(); i < len; i++) {
				bw.write(lines.get(i));
				bw.newLine();
			}
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != out) out.close();
				if (null != bw) bw.close();
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	public static void filter(String src, String dest, LineHandler lineHandler) {
		InputStream in = null;
		BufferedReader br = null;
		OutputStream out = null;
		BufferedWriter bw = null;
		try {
			in = new FileInputStream(new File(src));
			br = new BufferedReader(new InputStreamReader(in));
			out = new FileOutputStream(new File(dest));
			bw = new BufferedWriter(new OutputStreamWriter(out));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (!lineHandler.filter(line)) {
					bw.write(line);
					bw.newLine();
				}
			}
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != in) in.close();
				if (null != br) br.close();
				if (null != out) out.close();
				if (null != bw) bw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
