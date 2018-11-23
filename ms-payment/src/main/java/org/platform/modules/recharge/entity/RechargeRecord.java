package org.platform.modules.recharge.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import org.platform.modules.abstr.entity.PKAutoEntity;
import org.platform.utils.json.GsonUtils;

/** 充值记录 */
@Table(name = "T_RECHARGE_RECORD")
public class RechargeRecord extends PKAutoEntity<Long> {

	private static final long serialVersionUID = 1L;

	/** 用户ID */
	@Column(name = "USER_ID")
	private Long userId = null;
	/** 充值总金额 */
	@Column(name = "TOTAL_MONEY")
	private Double totalMoney = null;
	/** 充值方式 */
	@Column(name = "RECHARGE_MODE")
	private String rechargeMode = null;
	/** 充值渠道 */
	@Column(name = "CHANNEL")
	private String channel = null;
	/** 商户订单号 */
	@Column(name = "OUT_TRADE_NO")
	private String outTradeNo = null;
	/** 充值订单号 */
	@Column(name = "TRADE_NO")
	private String tradeNo = null;
	/** 充值签名 */
	@Column(name = "SIGN")
	private String sign = null;
	/** 充值记录备注 */
	@Column(name = "RECHARGE_NOTE")
	private String rechargeNote = null;
	/** 支付结果记录备注 */
	@Column(name = "RESULT_NOTE")
	private String resultNote = null;
	/** 交易状态 */
	@Column(name = "TRADE_STATUS")
	private String tradeStatus = null;
	/** 订单生成时间 */
	@Column(name = "INSERT_TIME")
	private Date insertTime = null;
	/** 删除标识 */
	@Column(name = "DELETE_FLAG")
	private boolean deleteFlag = false;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(Double totalMoney) {
		this.totalMoney = totalMoney;
	}

	public String getRechargeMode() {
		return rechargeMode;
	}

	public void setRechargeMode(String rechargeMode) {
		this.rechargeMode = rechargeMode;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getRechargeNote() {
		return rechargeNote;
	}

	public void setRechargeNote(String rechargeNote) {
		this.rechargeNote = rechargeNote;
	}

	public String getResultNote() {
		return resultNote;
	}

	public void setResultNote(String resultNote) {
		this.resultNote = resultNote;
	}

	public String getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public boolean isDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	
	public RechargeMode obtainRechargeMode() {
		RechargeMode cRechargeMode = GsonUtils.builder().fromJson(this.rechargeMode, RechargeMode.class);
		cRechargeMode.fillRechargeModeFromRule();
		return cRechargeMode;
	}

}
