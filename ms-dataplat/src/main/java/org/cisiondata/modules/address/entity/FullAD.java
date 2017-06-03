package org.cisiondata.modules.address.entity;

import javax.persistence.Column;

import org.cisiondata.modules.abstr.entity.PKAutoEntity;

public class FullAD extends PKAutoEntity<Long> {

	private static final long serialVersionUID = 1L;

	/** 省、自治区、直辖市、特别行政区 */
	@Column(name = "PROVINCE")
	private String province = null;
	/** 市、自治州、区 */
	@Column(name = "CITY")
	private String city = null;
	/** 县 */
	@Column(name = "COUNTY")
	private String county = null;
	/** 街道办事处、镇、乡 */
	@Column(name = "VILLAGES_TOWNS")
	private String villagesTowns = null;
	/** 居民委员会、村民委员会 */
	@Column(name = "RESIDENTS_COMMITTEE")
	private String residentsCommittee = null;
	/** 编码 */
	@Column(name = "CODE")
	private String code = null;

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getVillagesTowns() {
		return villagesTowns;
	}

	public void setVillagesTowns(String villagesTowns) {
		this.villagesTowns = villagesTowns;
	}

	public String getResidentsCommittee() {
		return residentsCommittee;
	}

	public void setResidentsCommittee(String residentsCommittee) {
		this.residentsCommittee = residentsCommittee;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
