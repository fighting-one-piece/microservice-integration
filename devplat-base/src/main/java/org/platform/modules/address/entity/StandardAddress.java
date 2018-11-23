package org.platform.modules.address.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.platform.modules.abstr.entity.PKAutoEntity;

/** 标准地址表 */
@Entity
@Table(name="T_STANDARD_ADDRESS")
public class StandardAddress extends PKAutoEntity<Long> {

	private static final long serialVersionUID = 1L;

	/** 行政区划代码 */
	@Column(name="ADMINISTRATIVE_DIVISION_CODE")
	private String administrativeDivisionCode = null;
	/** 道路名*/
	@Column(name="ROAD_NAME")
	private String roadName = null;
	/** 门牌号*/
	@Column(name="HOUSE_NUMBER")
	private String houseNumber = null;
	/** 小区名*/
	@Column(name="RESIDENTIAL_COMMUNITY_NAME")
	private String residentialCommunityName = null;
	/** 楼牌号*/
	@Column(name="BUILDING_NUMBER")
	private String buildingNumber = null;
	/** 兴趣点*/
	@Column(name="POINT_OF_INTEREST")
	private String pointOfInterest = null;
	/** 空间坐标*/
	@Column(name="SPATIAL_COORDINATES")
	private String spatialCoordinates = null;
	
}
