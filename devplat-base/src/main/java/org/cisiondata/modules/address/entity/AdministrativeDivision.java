package org.cisiondata.modules.address.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.cisiondata.modules.abstr.entity.PKAutoEntity;

/** 行政区划表*/
@Entity
@Table(name="T_ADMINISTRATIVE_DIVISION")
public class AdministrativeDivision extends PKAutoEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	public static final String ROOT = "000000000000";
	
	/** 区域*/
	@Column(name="REGION")
	private String region = null;
	/** 编码*/
	@Column(name="CODE")
	private String code = null;
	/** 上级区域编码*/
	@Column(name = "PARENT_CODE")
	private String parentCode = null;
	
	public String getRegion() {
		return region;
	}
	
	public void setRegion(String region) {
		this.region = region;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	
	@Override
	public String toString() {
		return this.region + ":" + this.code + ":" + this.parentCode;
	}
	
}
