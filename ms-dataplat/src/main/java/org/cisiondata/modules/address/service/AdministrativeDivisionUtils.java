package org.cisiondata.modules.address.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.util.WordConfTools;
import org.cisiondata.modules.address.entity.ADMatcher;
import org.cisiondata.modules.address.entity.ADMatcherItem;
import org.cisiondata.modules.address.entity.ADMatcherType;
import org.cisiondata.modules.address.entity.AdministrativeDivisionMatcher;
import org.cisiondata.utils.file.DefaultLineHandler;
import org.cisiondata.utils.file.FileUtils;

public class AdministrativeDivisionUtils {

	private static Set<String> provinces = null;
	
	private static Map<String, String> provinceMap = new HashMap<String, String>();
	
	private static Set<String> cities = null;
	
	private static Map<String, String> cityMap = new HashMap<String, String>();
	
	private static Map<String, String> cityProvinceMap = new HashMap<String, String>();
	
	private static Set<String> counties = null;
	
	private static Map<String, String> countyMap = new HashMap<String, String>();
	
	private static Map<String, String> countyCityMap = new HashMap<String, String>();
	
	private static Set<String> towns = null;
	
	private static Map<String, String> townMap = new HashMap<String, String>();
	
	private static Set<String> villages = null;
	
	private static Map<String, String> villageMap = new HashMap<String, String>();
	
	private static Map<String, String> map = new HashMap<String, String>();
	
	static {
		WordConfTools.set("dic.path", "classpath:dictionary/administrative_division.dic,"
				+ "classpath:dictionary/administrative_division_1.dic,"
				+ "classpath:dictionary/administrative_division_2.dic,"
				+ "classpath:dictionary/administrative_division_3.dic,"
				+ "classpath:dictionary/administrative_division_4.dic,"
				+ "classpath:dictionary/administrative_division_5.dic");
		
		List<String> pLines = FileUtils.readFromClasspath("dictionary/administrative_division_1.dic", new DefaultLineHandler());
		provinces = new HashSet<String>(pLines);
		for (int i = 0, len = pLines.size(); i < len; i++) {
			provinceMap.put(pLines.get(i), pLines.get(++i));
		}
		List<String> ciLines = FileUtils.readFromClasspath("dictionary/administrative_division_2.dic", new DefaultLineHandler());
		cities = new HashSet<String>(ciLines);
		for (int i = 0, len = ciLines.size(); i < len; i++) {
			cityMap.put(ciLines.get(i), ciLines.get(++i));
		}
		List<String> coLines = FileUtils.readFromClasspath("dictionary/administrative_division_3.dic", new DefaultLineHandler());
		counties = new HashSet<String>(coLines);
		for (int i = 0, len = coLines.size(); i < len; i++) {
			countyMap.put(coLines.get(i), coLines.get(++i));
		}
		List<String> tLines = FileUtils.readFromClasspath("dictionary/administrative_division_4.dic", new DefaultLineHandler());
		towns = new HashSet<String>(tLines);
		for (int i = 0, len = tLines.size(); i < len; i++) {
			townMap.put(tLines.get(i), tLines.get(++i));
		}
		List<String> vLines = FileUtils.readFromClasspath("dictionary/administrative_division_5.dic", new DefaultLineHandler());
		villages = new HashSet<String>(vLines);
		for (int i = 0, len = vLines.size(); i < len; i++) {
			villageMap.put(vLines.get(i), vLines.get(++i));
		}
		List<String> pccLines = FileUtils.readFromClasspath("dictionary/administrative_division_1_3.dic", new DefaultLineHandler());
		for (int i = 0, len = pccLines.size(); i < len; i++) {
			String[] kv = pccLines.get(i).split(",");
			cityProvinceMap.put(kv[1], kv[2]);
			countyCityMap.put(kv[0], kv[1]);
		}
		List<String> lines = FileUtils.readFromClasspath("dictionary/administrative_division.dic", new DefaultLineHandler());
		for (int i = 0, len = lines.size(); i < len; i++) {
			String[] kv = lines.get(i).split(",");
			map.put(kv[0], kv[1]);
			map.put(kv[1], kv[2]);
			map.put(kv[2], kv[3]);
			if (kv.length == 5) {
				map.put(kv[3], kv[4]);
			}
		}
	}
	
	public static Set<String> getProvinces() {
		return provinces;
	}
	
	public static Map<String, String> getProvinceMap() {
		return provinceMap;
	}
	
	public static Set<String> getCities() {
		return cities;
	}
	
	public static Map<String, String> getCityMap() {
		return cityMap;
	}
	
	public static Map<String, String> getCityProvinceMap() {
		return cityProvinceMap;
	}
	
	public static Set<String> getCounties() {
		return counties;
	}
	
	public static Map<String, String> getCountyMap() {
		return countyMap;
	}
	
	public static Map<String, String> getCountyCityMap() {
		return countyCityMap;
	}
	
	public static Set<String> getTowns() {
		return towns;
	}
	
	public static Set<String> getVillages() {
		return villages;
	}
	
	public static List<String> extract3AdministrativeDivision(String address) {
		List<String> result = new ArrayList<String>();
		if (StringUtils.isNotBlank(address)) {
			List<Word> words = WordSegmenter.seg(address, SegmentationAlgorithm.MaximumMatching);
			AdministrativeDivisionMatcher adMatcher = new AdministrativeDivisionMatcher();
			for (int i = 0, len = words.size(); i < len; i++) {
				if (result.size() == 3) break;
				String word = words.get(i).getText();
				if (provinces.contains(word)) {
					adMatcher.provinceMatch();
					if (provinceMap.containsKey(word)) {
						word = provinceMap.get(word);
					}
					result.add(word);
				} else if (cities.contains(word)) {
					adMatcher.cityMatch();
					if (cityMap.containsKey(word)) {
						word = cityMap.get(word);
					}
					if (adMatcher.getProvinceMatch() == 0) {
						result.add(cityProvinceMap.get(word));
					}
					result.add(word);
				} else if (counties.contains(word)) {
					adMatcher.countyMatch();
					if (countyMap.containsKey(word)) {
						word = countyMap.get(word);
					}
					if (adMatcher.getCityMatch() == 0) {
						String city = countyCityMap.get(word);
						if (StringUtils.isNotBlank(city)) {
							if (adMatcher.getProvinceMatch() == 0) {
								result.add(cityProvinceMap.get(city));
							}
							result.add(city);
						}
					}
					result.add(word);
				}
			}
		}
		return result;
	}
	
	public static List<String> extract5AdministrativeDivision(String address) {
		List<String> result = new ArrayList<String>();
		if (StringUtils.isNotBlank(address)) {
			List<Word> words = WordSegmenter.seg(address, SegmentationAlgorithm.MaximumMatching);
			AdministrativeDivisionMatcher adMatcher = new AdministrativeDivisionMatcher();
			for (int i = 0, len = words.size(); i < len; i++) {
				if (result.size() == 5) break;
				String word = words.get(i).getText();
				if (provinces.contains(word)) {
					adMatcher.provinceMatch();
					if (provinceMap.containsKey(word)) word = provinceMap.get(word);
					adMatcher.getProvinceMatchWords().add(word);
				} else if (cities.contains(word)) {
					adMatcher.cityMatch();
					if (cityMap.containsKey(word)) word = cityMap.get(word);
					adMatcher.getCityMatchWords().add(word);
				} else if (counties.contains(word)) {
					adMatcher.countyMatch();
					if (countyMap.containsKey(word)) word = countyMap.get(word);
					adMatcher.getCountyMatchWords().add(word);
				} else if (towns.contains(word)) {
					adMatcher.townMatch();
					if (townMap.containsKey(word)) word = townMap.get(word);
					adMatcher.getTownMatchWords().add(word);
				} else if (villages.contains(word)) {
					adMatcher.villageMatch();
					if (villageMap.containsKey(word)) word = villageMap.get(word);
					adMatcher.getVillageMatchWords().add(word);
				} 
			}
			if (adMatcher.getVillageMatch() > 0 && adMatcher.getTownMatch() == 0) {
				getParentAdministrativeDivision(adMatcher.getVillageMatchWords().get(0), result, 5);
			} else if (adMatcher.getTownMatch() > 0 && adMatcher.getCountyMatch() == 0) {
				getParentAdministrativeDivision(adMatcher.getTownMatchWords().get(0), result, 4);
			} else if (adMatcher.getCountyMatch() > 0) {
				getParentAdministrativeDivision(adMatcher.getCountyMatchWords().get(0), result, 3);
			} else if (adMatcher.getCityMatch() > 0) {
				getParentAdministrativeDivision(adMatcher.getCityMatchWords().get(0), result, 2);
			} else if (adMatcher.getProvinceMatch() > 0) {
				getParentAdministrativeDivision(adMatcher.getProvinceMatchWords().get(0), result, 1);
			}
		}
		Collections.reverse(result);
		return result;
	}
	
	private static void getParentAdministrativeDivision(String region, List<String> parents, int level) {
		if (level == 0) return;
		parents.add(region);
		if (map.containsKey(region)) {
			getParentAdministrativeDivision(map.get(region), parents, --level);
		}
	}
	
	public static List<String> extractAdministrativeDivision(String address) {
		List<String> result = new ArrayList<String>();
		if (StringUtils.isNotBlank(address)) {
			List<Word> words = WordSegmenter.seg(address, SegmentationAlgorithm.ReverseMaximumMatching);
			ADMatcher adMatcher = new ADMatcher();
			int currentMatcherValue = 0;
			for (int i = 0, len = words.size(); i < len; i++) {
				String word = words.get(i).getText();
				if (word.length() == 1) break;
				if (containsDigit(word)) {
					adMatcher.removeLastItem();
					break;
				}
				if (provinces.contains(word)) {
					if (currentMatcherValue > ADMatcherType.PROVINCE.value()) continue;
					if (provinceMap.containsKey(word)) word = provinceMap.get(word);
					adMatcher.add(ADMatcherType.PROVINCE, word);
					currentMatcherValue = ADMatcherType.PROVINCE.value();
				} else if (cities.contains(word)) {
					if (currentMatcherValue > ADMatcherType.CITY.value()) continue;
					if (cityMap.containsKey(word)) word = cityMap.get(word);
					adMatcher.add(ADMatcherType.CITY, word);
					currentMatcherValue = ADMatcherType.CITY.value();
				} else if (counties.contains(word)) {
					if (currentMatcherValue > ADMatcherType.COUNTY.value()) continue;
					if (countyMap.containsKey(word)) word = countyMap.get(word);
					adMatcher.add(ADMatcherType.COUNTY, word);
					currentMatcherValue = ADMatcherType.COUNTY.value();
				} else if (towns.contains(word)) {
					if (currentMatcherValue > ADMatcherType.TOWN.value()) continue;
					if (townMap.containsKey(word)) word = townMap.get(word);
					adMatcher.add(ADMatcherType.TOWN, word);
					currentMatcherValue = ADMatcherType.TOWN.value();
				} else if (villages.contains(word)) {
					if (currentMatcherValue > ADMatcherType.VILLAGE.value()) continue;
					if (villageMap.containsKey(word)) word = villageMap.get(word);
					adMatcher.add(ADMatcherType.VILLAGE, word);
					currentMatcherValue = ADMatcherType.VILLAGE.value();
				} 
			}
			ADMatcherItem lastItem = adMatcher.lastItem();
			if (null == lastItem) return result;
			String lastRegion = lastItem.getWords().get(0);
			result.add(lastRegion);
			ADMatcherItem prevItem = adMatcher.prevItem(lastItem);
			while (null != prevItem) {
				if (prevItem.getCount() == 0) {
					if (map.containsKey(lastRegion)) {
						lastRegion = map.get(lastRegion);
					}
				} else {
					lastRegion = prevItem.getWords().get(0);
				}
				result.add(lastRegion);
				prevItem = adMatcher.prevItem(prevItem);
			}
			System.out.println(WordSegmenter.seg(address, SegmentationAlgorithm.MaximumMatching));
			System.out.println(words);
		}
		Collections.reverse(result);
		return result;
	}
	
	private static boolean containsDigit(String word) {
		Pattern pattern = Pattern.compile(".*\\d+.*");
		Matcher matcher = pattern.matcher(word);
		return !matcher.matches() ? false : true;
	}
	
	public static void main(String[] args) {
//		System.out.println(extractAdministrativeDivision("峨眉山"));
//		System.out.println(extractAdministrativeDivision("松浦街"));
//		System.out.println(extractAdministrativeDivision("白石村"));
//		System.out.println(extractAdministrativeDivision("澄迈"));
//		System.out.println(extractAdministrativeDivision("曙坪战斗"));
//		System.out.println(extractAdministrativeDivision("一村"));
//		System.out.println(extractAdministrativeDivision("六一"));
//		System.out.println(extractAdministrativeDivision("武城县"));
//		System.out.println(extractAdministrativeDivision("武城县老车站南张庄新区2－1－402"));
//	    //[四川省, 乐山市, 市中区, 龙山路, 老车站社区]
//		System.out.println(extractAdministrativeDivision("滨城区黄河6路与渤海7路胜滨小区东区53号楼1单元1号"));
//		System.out.println(extractAdministrativeDivision("湖北省鄂州市华容区"));
//		System.out.println(extractAdministrativeDivision("湖北省鄂州市华容区仁义"));
//		System.out.println(extractAdministrativeDivision("湖北省鄂州市华容区仁义路顺丰快递2楼速8台球"));
		System.out.println(extractAdministrativeDivision("洪武路88号顺丰速运"));
	}
	
}
