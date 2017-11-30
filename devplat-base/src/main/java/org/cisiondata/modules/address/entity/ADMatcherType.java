package org.cisiondata.modules.address.entity;

public enum ADMatcherType {

	PROVINCE(1),CITY(2),COUNTY(3),TOWN(4),VILLAGE(5);
	
	private int value = 0;
	
	private ADMatcherType (int value) {
		this.value = value;
	}
	
	public int value() {
		return this.value;
	}
	
	public boolean equals(ADMatcherType type) {
		return this.value == type.value() ? true : false;
	}
	
	public static ADMatcherType prev(ADMatcherType type) {
		if (type.equals(ADMatcherType.VILLAGE)) {
			return ADMatcherType.TOWN;
		} else if (type.equals(ADMatcherType.TOWN)) {
			return ADMatcherType.COUNTY;
		} else if (type.equals(ADMatcherType.COUNTY)) {
			return ADMatcherType.CITY;
		} else if (type.equals(ADMatcherType.CITY)) {
			return ADMatcherType.PROVINCE;
		} else {
			return null;
		}
	}
	
	public static ADMatcherType next(ADMatcherType type) {
		if (type.equals(ADMatcherType.PROVINCE)) {
			return ADMatcherType.CITY;
		} else if (type.equals(ADMatcherType.CITY)) {
			return ADMatcherType.COUNTY;
		} else if (type.equals(ADMatcherType.COUNTY)) {
			return ADMatcherType.TOWN;
		} else if (type.equals(ADMatcherType.TOWN)) {
			return ADMatcherType.VILLAGE;
		} else {
			return null;
		}
	}
	
}
