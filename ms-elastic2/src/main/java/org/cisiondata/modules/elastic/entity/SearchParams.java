package org.cisiondata.modules.elastic.entity;

public class SearchParams {
	
	private String indices = null;
	
	private String types = null;
	
	private String fields = null;
	
	private String keywords = null;
	
	private int highLight = 0;
	
	private Integer currentPageNum = null;
	
	private Integer rowNumPerPage = 10;
	
	private String scrollId = null;

	public SearchParams(String indices, String types, String keywords, int highLight) {
		super();
		this.indices = indices;
		this.types = types;
		this.keywords = keywords;
		this.highLight = highLight;
	}
	
	public SearchParams(String indices, String types, String fields, String keywords, int highLight) {
		super();
		this.indices = indices;
		this.types = types;
		this.fields = fields;
		this.keywords = keywords;
		this.highLight = highLight;
	}
	
	public SearchParams(String indices, String types, String keywords, int highLight,
			Integer currentPageNum, Integer rowNumPerPage) {
		super();
		this.indices = indices;
		this.types = types;
		this.keywords = keywords;
		this.highLight = highLight;
		this.currentPageNum = currentPageNum;
		this.rowNumPerPage = rowNumPerPage;
	}

	public SearchParams(String indices, String types, String fields, String keywords, int highLight,
			Integer currentPageNum, Integer rowNumPerPage) {
		super();
		this.indices = indices;
		this.types = types;
		this.fields = fields;
		this.keywords = keywords;
		this.highLight = highLight;
		this.currentPageNum = currentPageNum;
		this.rowNumPerPage = rowNumPerPage;
	}
	
	public SearchParams(String indices, String types, String fields, String keywords, int highLight, String scrollId) {
		super();
		this.indices = indices;
		this.types = types;
		this.fields = fields;
		this.keywords = keywords;
		this.highLight = highLight;
		this.scrollId = scrollId;
	}
	
	public String[] indices() {
		return indices.indexOf(",") == -1 ? new String[]{indices} : indices.split(",");
	}
	
	public String[] types() {
		return types.indexOf(",") == -1 ? new String[]{types} : types.split(",");
	}
	
	public String[] fields() {
		return fields.indexOf(",") == -1 ? new String[]{fields} : fields.split(",");
	}
	
	public String[] keywords() {
		return keywords.trim().indexOf(" ") == -1 ? new String[]{keywords} : keywords.split(" ");
	}
	
	public boolean hasFields() {
		return null == fields || fields.length() == 0 ? false : true;
	}
	
	public boolean isHighLight() {
		return highLight == 0 ? false : true;
	}
	
	public boolean isPagination() {
		return null == currentPageNum ? false : true;
	}
	
	public boolean isScroll() {
		return null == scrollId || "".equalsIgnoreCase(scrollId) ? false : true;
	}

	public int getCurrentPageNum() {
		return null == currentPageNum ? 0 : currentPageNum;
	}

	public int getRowNumPerPage() {
		return null == rowNumPerPage ? 10 : rowNumPerPage;
	}
	
	public String getScrollId() {
		return scrollId;
	}

}
