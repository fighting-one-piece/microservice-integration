package org.platform.modules.recharge.entity;

import java.io.Serializable;

/** 充值记录 */
public class RechargeRecordVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 充值总金额 */
	private Double totalMoney = null;
	/** 充值方式 */
	private RechargeMode rechargeMode = null;
	/** 充值渠道 */
	private String channel = null;
	/** 订单生成时间 */
	private Long insertTime = null;

	public Double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(Double totalMoney) {
		this.totalMoney = totalMoney;
	}

	public RechargeMode getRechargeMode() {
		return rechargeMode;
	}

	public void setRechargeMode(RechargeMode rechargeMode) {
		this.rechargeMode = rechargeMode;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Long getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Long insertTime) {
		this.insertTime = insertTime;
	}

}
