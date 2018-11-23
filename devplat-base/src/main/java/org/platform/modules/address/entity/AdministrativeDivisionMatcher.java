package org.platform.modules.address.entity;

import java.util.ArrayList;
import java.util.List;

public class AdministrativeDivisionMatcher {

	/** 省匹配 */
	private int provinceMatch = 0;
	/** 省匹配词语 */
	private List<String> provinceMatchWords = null;
	/** 市匹配 */
	private int cityMatch = 0;
	/** 市匹配词语 */
	private List<String> cityMatchWords = null;
	/** 县匹配 */
	private int countyMatch = 0;
	/** 县匹配词语 */
	private List<String> countyMatchWords = null;
	/** 镇匹配 */
	private int townMatch = 0;
	/** 镇匹配词语 */
	private List<String> townMatchWords = null;
	/** 村匹配 */
	private int villageMatch = 0;
	/** 村匹配词语 */
	private List<String> villageMatchWords = null;
	
	public void provinceMatch() {
		this.provinceMatch++;
	}
	
	public int getProvinceMatch() {
		return provinceMatch;
	}
	
	public void setProvinceMatch(int provinceMatch) {
		this.provinceMatch = provinceMatch;
	}
	
	public List<String> getProvinceMatchWords() {
		if (null == provinceMatchWords) provinceMatchWords = new ArrayList<String>();
		return provinceMatchWords;
	}

	public void setProvinceMatchWords(List<String> provinceMatchWords) {
		this.provinceMatchWords = provinceMatchWords;
	}

	public void cityMatch() {
		this.cityMatch++;
	}
	
	public int getCityMatch() {
		return cityMatch;
	}
	
	public void setCityMatch(int cityMatch) {
		this.cityMatch = cityMatch;
	}

	public List<String> getCityMatchWords() {
		if (null == cityMatchWords) cityMatchWords = new ArrayList<String>();
		return cityMatchWords;
	}
	
	public void setCityMatchWords(List<String> cityMatchWords) {
		this.cityMatchWords = cityMatchWords;
	}
	
	public void countyMatch() {
		this.countyMatch++;
	}

	public int getCountyMatch() {
		return countyMatch;
	}

	public void setCountyMatch(int countyMatch) {
		this.countyMatch = countyMatch;
	}
	
	public List<String> getCountyMatchWords() {
		if (null == countyMatchWords) countyMatchWords = new ArrayList<String>();
		return countyMatchWords;
	}
	
	public void setCountyMatchWords(List<String> countyMatchWords) {
		this.countyMatchWords = countyMatchWords;
	}

	public void townMatch() {
		this.townMatch++;
	}
	
	public int getTownMatch() {
		return townMatch;
	}

	public void setTownMatch(int townMatch) {
		this.townMatch = townMatch;
	}
	
	public List<String> getTownMatchWords() {
		if (null == townMatchWords) townMatchWords = new ArrayList<String>();
		return townMatchWords;
	}
	
	public void setTownMatchWords(List<String> townMatchWords) {
		this.townMatchWords = townMatchWords;
	}
	
	public void villageMatch() {
		this.villageMatch++;
	}

	public int getVillageMatch() {
		return villageMatch;
	}

	public void setVillageMatch(int villageMatch) {
		this.villageMatch = villageMatch;
	}
	
	public List<String> getVillageMatchWords() {
		if (null == villageMatchWords) villageMatchWords = new ArrayList<String>();
		return villageMatchWords;
	}
	
	public void setVillageMatchWords(List<String> villageMatchWords) {
		this.villageMatchWords = villageMatchWords;
	}
	
}
