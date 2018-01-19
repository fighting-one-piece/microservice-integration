package org.cisiondata.modules.oauth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.cisiondata.modules.abstr.entity.PKAutoEntity;

@Entity
@Table(name = "T_AUTHORITY")
public class Authority extends PKAutoEntity<Long> {

	private static final long serialVersionUID = 1L;

	/** 授权名称 */
	@Column(name = "NAME")
	private String name = null;
	/** 授权标识 */
	@Column(name = "IDENTITY")
	private String identity = null;
	/** 授权描述 */
	@Column(name = "DESCRIPTION")
	private String desc = null;
	/** 是否删除标志 */
	@Column(name = "DELETE_FLAG")
	private Boolean deleteFlag = Boolean.FALSE;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Boolean getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

}
