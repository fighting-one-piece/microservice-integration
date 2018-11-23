package org.platform.modules.recharge.entity;

import java.io.Serializable;

/** 充值方式 */
public class RechargeModeVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 充值方式名称 */
	private String name = null;
	/** 充值方式标识 */
	private String identity = null;
	/** 充值方式描述 */
	private String desc = null;
	/** 充值总金额 */
	private Double totalMoney = null;
	/** 充值方式代币 */
	private transient Integer dCoin = null;

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

	public Double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(Double totalMoney) {
		this.totalMoney = totalMoney;
	}

	public Integer getdCoin() {
		return dCoin;
	}

	public void setdCoin(Integer dCoin) {
		this.dCoin = dCoin;
	}

}
