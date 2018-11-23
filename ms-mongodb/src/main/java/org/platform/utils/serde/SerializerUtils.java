package org.platform.utils.serde;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public final class SerializerUtils {

	private static final Logger LOG = LoggerFactory.getLogger(SerializerUtils.class);

	/**
	 * 序列化
	 * @param object
	 * @return
	 * @throws java.io.IOException
	 */
	public static byte[] write(Object object) {
		if (null == object) {
			return null;
		}
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if (oos != null) oos.close();
				if (baos != null) baos.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return baos.toByteArray();
	}

	/**
	 * 反序列化
	 * @param bytes
	 * @return
	 * @throws java.io.IOException
	 */
	public static Object read(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		Object object = null;
		try {
			 bais = new ByteArrayInputStream(bytes);
			 ois = new ObjectInputStream(bais);
			object = ois.readObject();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		} finally {
			try {
				if (bais != null) bais.close();
				if (ois != null) ois.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return object;
	}

}
