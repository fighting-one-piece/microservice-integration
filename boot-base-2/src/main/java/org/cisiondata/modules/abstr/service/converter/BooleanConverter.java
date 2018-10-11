package org.cisiondata.modules.abstr.service.converter;

public class BooleanConverter {

	public static boolean convert(int value) {
		return value == 1 ? true : false;
	}
	
	public static int convert(boolean value) {
		return value ? 1 : 0;
	}
	
}
