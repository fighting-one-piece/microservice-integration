package org.cisiondata.modules.elastic.entity;

public class TermCondition extends Condition {

	private String name = null;

	private Object value = null;
	
	public TermCondition() {
	}
	
	public TermCondition(String name, Object value) {
        this.name = name;
        this.value = value;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
