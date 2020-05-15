package org.platform.modules.recharge.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.platform.modules.abstr.entity.PKAutoEntity;
import org.platform.utils.json.GsonUtils;
import org.platform.utils.reflect.ReflectUtils;

/** 充值方式 */
@Table(name = "T_RECHARGE_MODE")
public class RechargeMode extends PKAutoEntity<Long> {

	private static final long serialVersionUID = 1L;

	/** 充值方式名称 */
	@Column(name = "NAME")
	private String name = null;
	/** 充值方式标识 */
	@Column(name = "IDENTITY")
	private String identity = null;
	/** 充值方式描述 */
	@Column(name = "DESC")
	private String desc = null;
	/** 充值总金额 */
	@Column(name = "TOTAL_MONEY")
	private Double totalMoney = null;
	/** 充值方式代币 */
	private transient Integer dCoin = null;
	/** 充值方式赠送代币 */
	private transient Integer dCoinPresent = null;
	/** 充值折扣 */
	private transient Double discount = null;
	/** 充值规则 */
	@Column(name = "RULE")
	private String rule = null;
	/** 删除标识 */
	@Column(name = "DELETE_FLAG")
	private boolean deleteFlag = false;

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

	public Integer getdCoinPresent() {
		return dCoinPresent;
	}

	public void setdCoinPresent(Integer dCoinPresent) {
		this.dCoinPresent = dCoinPresent;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public boolean isDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	
	public void fillRechargeModeFromRule() {
		if (StringUtils.isBlank(this.rule)) return;
		Map<String, Object> params = GsonUtils.fromJsonToMap(rule);
		Field[] fields = RechargeMode.class.getDeclaredFields();
		try {
			for (int i = 0, len = fields.length; i < len; i++) {
				Field field = fields[i];
        		if (!Modifier.isTransient(field.getModifiers())) continue;
				Object value = params.get(field.getName());
				if (null == value) continue;
				field.setAccessible(true);
				value = ReflectUtils.convertValueByFieldType(field.getType(), value);
				field.set(this, value);
				field.setAccessible(false);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
