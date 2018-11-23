package org.platform.modules.address.entity;

import java.util.HashSet;
import java.util.Set;

/** 地址通用名*/
public class AddressElementGenericName {
	
	/** 道路通用名*/
	private static Set<String> roadGenericNames = new HashSet<String>();
	/** 门派通用名*/
	private static Set<String> houseGenericNames = new HashSet<String>();
	/** 住宅小区通用名*/
	private static Set<String> residentialCommunityGenericNames = new HashSet<String>();
	/** 楼牌通用名*/
	private static Set<String> buildingGenericNames = new HashSet<String>();
	/** 兴趣点通用名*/
	private static Set<String> pointOfInterestGenericNames = new HashSet<String>();

	static {
		initRoadGenericNames();
		initHouseGenericNames();
		initResidentialCommunityGenericNames();
		initBuildingGenericNames();
		initPointOfInterestGenericNames();
	}
	
	private static void initRoadGenericNames() {
		roadGenericNames.add("路");
		roadGenericNames.add("大道");
		roadGenericNames.add("道");
		roadGenericNames.add("大街");
		roadGenericNames.add("街");
		roadGenericNames.add("巷");
		roadGenericNames.add("胡同");
		roadGenericNames.add("条");
		roadGenericNames.add("里");
	}
	
	private static void initHouseGenericNames() {
		houseGenericNames.add("号");
	}
	
	private static void initResidentialCommunityGenericNames() {
		residentialCommunityGenericNames.add("里");
		residentialCommunityGenericNames.add("区");
		residentialCommunityGenericNames.add("小区");
		residentialCommunityGenericNames.add("园");
		residentialCommunityGenericNames.add("村");
		residentialCommunityGenericNames.add("坊");
		residentialCommunityGenericNames.add("庄");
		residentialCommunityGenericNames.add("居");
		residentialCommunityGenericNames.add("寓");
		residentialCommunityGenericNames.add("公寓");
		residentialCommunityGenericNames.add("苑");
		residentialCommunityGenericNames.add("墅");
		residentialCommunityGenericNames.add("别墅");
		residentialCommunityGenericNames.add("弄");
	}

	private static void initBuildingGenericNames() {
		buildingGenericNames.add("号");
		buildingGenericNames.add("号楼");
		buildingGenericNames.add("楼");
		buildingGenericNames.add("宿舍");
		buildingGenericNames.add("斋");
		buildingGenericNames.add("馆");
		buildingGenericNames.add("堂");
		buildingGenericNames.add("栋");
	}

	private static void initPointOfInterestGenericNames() {
		pointOfInterestGenericNames.add("大楼");
		pointOfInterestGenericNames.add("大厦");
		pointOfInterestGenericNames.add("广场");
		pointOfInterestGenericNames.add("饭店");
		pointOfInterestGenericNames.add("馆");
		pointOfInterestGenericNames.add("酒店");
		pointOfInterestGenericNames.add("宾馆");
		pointOfInterestGenericNames.add("市场");
		pointOfInterestGenericNames.add("花园");
		pointOfInterestGenericNames.add("招待所");
	}
	
	
}
