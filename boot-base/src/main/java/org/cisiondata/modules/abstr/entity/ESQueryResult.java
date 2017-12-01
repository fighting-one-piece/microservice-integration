package org.cisiondata.modules.abstr.entity;

public class ESQueryResult<Entity> extends QueryResult<Entity> {
	
	private static final long serialVersionUID = 1L;
	
	/** ES Scroll Id*/
	private String scrollId = null;
	
	public String getScrollId() {
		return scrollId;
	}

	public void setScrollId(String scrollId) {
		this.scrollId = scrollId;
	}

}
