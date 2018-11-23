package org.platform.modules.elastic.entity;

public class Condition {

	protected String entityName = null;

	public Condition() {
		setEntityName();
	}
	
	public void setEntityName() {
		this.entityName = getClass().getSimpleName();
	}
	
	
	
}
