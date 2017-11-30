package org.cisiondata.modules.abstr.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

public class CEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** 标识*/
	private Long id = null;
	/** 创建时间*/
	@Column(name="CREATE_TIME")
	private Date createTime = null;
	/** 是否删除标志 */
	@Column(name = "DELETE_FLAG")
	private Boolean deleteFlag = false;
	/** DATA数据*/
	private transient List<CEntityData> datas = null;
	/** 操作表名*/
	private transient String table = null;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Boolean getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	
	public boolean hasDeleted() {
		return deleteFlag;
	}

	public List<CEntityData> getDatas() {
		if (null == datas) {
			datas = new ArrayList<CEntityData>();
		}
		return datas;
	}

	public void setDatas(List<CEntityData> datas) {
		this.datas = datas;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}
	
	public String identity() {
		return getClass().getSimpleName().toLowerCase() + ":" + id;
	}
	
	public void initBasicAttributes() {
		this.setDeleteFlag(false);
		this.setCreateTime(new Date());
	}
	
}
