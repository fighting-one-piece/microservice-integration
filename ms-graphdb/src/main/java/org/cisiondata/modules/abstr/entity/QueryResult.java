package org.cisiondata.modules.abstr.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QueryResult<Entity> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** 数据集合*/
	private List<Entity> resultList = null;
	/** 总行数*/
	private long totalRowNum = 0;
	/** ES Scroll Id*/
	private String scrollId = null;

	public QueryResult() {
	}

	public QueryResult(long totalRowNum, List<Entity> resultList) {
		this.totalRowNum = totalRowNum;
		this.resultList = resultList;
	}

	public QueryResult(int currentPageNum, int rowNumPerPage, List<Entity> resultList) {
		this.totalRowNum = resultList.size();
		this.resultList = resultList;

		int firstRowNum = getFirstRowNum(currentPageNum, rowNumPerPage);
		int lastRowNum = firstRowNum + rowNumPerPage;

		List<Entity> list = new ArrayList<Entity>();
		for (int i = firstRowNum, rowNum = resultList.size(); i < rowNum && i < lastRowNum; i++) {
			list.add(resultList.get(i));
		}
		this.resultList.clear();
		this.resultList = list;
	}

	public List<Entity> getResultList() {
		if (resultList == null) {
			resultList = new ArrayList<Entity>();
		}
		return resultList;
	}

	public void setResultList(List<Entity> resultList) {
		this.resultList = resultList;
	}

	public long getTotalRowNum() {
		return totalRowNum;
	}

	public void setTotalRowNum(long totalRowNum) {
		this.totalRowNum = totalRowNum;
	}

	public int getTotalPageNum(int rowNumPerPage) {
		if (rowNumPerPage == 0) {
			return 0;
		}
		return (resultList.size() - 1) / rowNumPerPage + 1;
	}

	public int getFirstRowNum(int currentPageNum, int rowNumPerPage) {
		if (currentPageNum <= 1) {
			currentPageNum = 1;
		}
		if (currentPageNum > getTotalPageNum(rowNumPerPage)) {
			currentPageNum = getTotalPageNum(rowNumPerPage);
		}
		return (currentPageNum -1) * rowNumPerPage;
	}
	
	public String getScrollId() {
		return scrollId;
	}

	public void setScrollId(String scrollId) {
		this.scrollId = scrollId;
	}

}
