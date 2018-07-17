package org.cisiondata.utils.endecrypt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

	/** gzip进行压缩 */
	public static byte[] gzip(String input) {
		if (null == input || input.length() == 0) return null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = null;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(input.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (gzip != null) gzip.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return out.toByteArray();
	}
	
	/** gzip进行解压缩 */
	public static String gunzip(byte[] bytes) {
		if (null == bytes) return null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = null;
		GZIPInputStream ginzip = null;
		String decompressed = null;
		try {
			in = new ByteArrayInputStream(bytes);
			ginzip = new GZIPInputStream(in);
			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = ginzip.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ginzip != null) ginzip.close();
				if (in != null) in.close();
				if (out != null) out.close();
			} catch (IOException e) {
			}
		}
		return decompressed;
	}

	/** zip进行压缩 */
	public static byte[] zip(String input) {
		if (null == input) return null;
		ByteArrayOutputStream out = null;
		ZipOutputStream zout = null;
		byte[] bytes = null;
		try {
			out = new ByteArrayOutputStream();
			zout = new ZipOutputStream(out);
			zout.putNextEntry(new ZipEntry("0"));
			zout.write(input.getBytes());
			zout.closeEntry();
			bytes = out.toByteArray();
		} catch (IOException e) {
		} finally {
			try {
				if (zout != null) zout.close();
				if (out != null) out.close();
			} catch (IOException e) {
			}
		}
		return bytes;
	}

	/** zip进行解压缩 */
	public static String unzip(byte[] bytes) {
		if (null == bytes) return null;
		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		ZipInputStream zin = null;
		String decompressed = null;
		try {
			out = new ByteArrayOutputStream();
			in = new ByteArrayInputStream(bytes);
			zin = new ZipInputStream(in);
			zin.getNextEntry();
			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = zin.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString();
		} catch (IOException e) {
			decompressed = null;
		} finally {
			try {
				if (zin != null) zin.close();
				if (in != null) in.close();
				if (out != null) out.close();
			} catch (IOException e) {
			}
		}
		return decompressed;
	}

}
