package org.platform.modules.abstr.service.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;

public class SerDeConverter {

	private static Converter<Object, byte[]> serializer = new SerializingConverter();
	private static Converter<byte[], Object> deserializer = new DeserializingConverter();
	
	public static byte[] serialize(Object object) {
		try {
			return serializer.convert(object);
		} catch (Exception ex) {
			throw new RuntimeException("Cannot serialize", ex);
		}
	}

	public static Object deserialize(byte[] bytes) {
		try {
			return deserializer.convert(bytes);
		} catch (Exception ex) {
			throw new RuntimeException("Cannot deserialize", ex);
		}
	}

}
