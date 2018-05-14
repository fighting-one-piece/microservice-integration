package org.cisiondata.modules.abstr.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/** 查询条件*/
public class Query implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 排序-升序*/
	public static final int ORDER_ASC = 1;
	/** 排序-降序*/
	public static final int ORDER_DESC = 2;
	/** 当前页数*/
	public static final String CURRENT_PAGE_NUM = "current_page_num";
	/** 每页行数*/
	public static final String ROW_NUM_PER_PAGE = "row_num_per_page";
	/** 总行数*/
	public static final String TOTAL_ROW_NUM = "total_row_num";
	/** 是否分页*/
	public static final String IS_PAGINATION = "isPagination";
	/** 起始位置*/
	public static final String OFFSET = "offset";
	/** 限制数量*/
	public static final String LIMIT = "limit";
	/** 表名*/
	public static final String TABLE = "table";
	/** Entity表名*/
	public static final String E_TABLE = "entityTable";
	/** Data表名*/
	public static final String D_TABLE = "dataTable";
	/** 是否分页*/
	private boolean isPagination = false;
	/** 当前页数*/
	private int currentPageNum = 0;
	/** 每页行数*/
	private int rowNumPerPage = Integer.MAX_VALUE;
	/** 排序属性*/
	private String orderProperty = null;
	/** 排序类型*/
	private int orderType = ORDER_ASC;
	/** 查询条件Map*/
	private Map<String, Object> condition = new HashMap<String, Object>();
	/** 基本属性查询条件Map*/
	private Map<String, Object> basicAttributes = new HashMap<String, Object>();
	/** 数据属性查询条件Map*/
	private Map<String, Object> dataAttributes = new HashMap<String, Object>();
	
	public Query() {
		condition.put(IS_PAGINATION, isPagination);
		condition.put(OFFSET, 0);
		condition.put(LIMIT, rowNumPerPage);
	}

	public void addCondition(String conditionKey, Object conditionValue) {
		condition.put(conditionKey, conditionValue);
	}

	public void addOrderCondition(String orderProperty, int orderType) {
		this.orderProperty = orderProperty;
		this.orderType = orderType;
	}

	public Object obtainConditionValue(String conditionKey) {
		for (Map.Entry<String, Object> entry : condition.entrySet()) {
			if (conditionKey.equals(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	public String getOrderCondition() {
		return orderProperty;
	}
	
	public String getDataOrderAttribute() {
		return null;
	}

	public int getOrderType() {
		return orderType;
	}
	
	public boolean isPagination() {
		return isPagination;
	}

	public void setPagination(boolean isPagination) {
		this.isPagination = isPagination;
		condition.put(IS_PAGINATION, this.isPagination);
	}

	public int getCurrentPageNum() {
		return this.currentPageNum <= 1 ? 1 : currentPageNum;
	}

	public void setCurrentPageNum(int currentPageNum) {
		this.currentPageNum = (currentPageNum <= 1 ? 1 : currentPageNum);
	}

	public int getRowNumPerPage() {
		return rowNumPerPage;
	}

	public void setRowNumPerPage(int rowNumPerPage) {
		this.rowNumPerPage = rowNumPerPage;
		condition.put(OFFSET, (this.currentPageNum - 1) * this.rowNumPerPage);
		condition.put(LIMIT, this.rowNumPerPage);
	}

	public Map<String, Object> getCondition() {
		if (null == this.condition) this.condition = new HashMap<String, Object>();
		return this.condition;
	}

	public void setCondition(Map<String, Object> condition) {
		this.condition = condition;
	}
	
	public Map<String, Object> getBasicAttributes() {
		return basicAttributes;
	}

	public void setBasicAttributes(Map<String, Object> basicAttributes) {
		this.basicAttributes = basicAttributes;
	}

	public Map<String, Object> getDataAttributes() {
		return dataAttributes;
	}

	public void setDataAttributes(Map<String, Object> dataAttributes) {
		this.dataAttributes = dataAttributes;
	}

}
