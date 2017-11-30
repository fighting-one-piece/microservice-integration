package org.cisiondata.modules.address.entity;

import java.util.ArrayList;
import java.util.List;

public class ADMatcherItem {

	private ADMatcherType type = null;
	
	private int count = 0;
	
	private List<String> words = null;

	public ADMatcherType getType() {
		return type;
	}

	public void setType(ADMatcherType type) {
		this.type = type;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<String> getWords() {
		if (null == words) words = new ArrayList<String>();
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}
	
	public void addWord(String word) {
		this.count++;
		getWords().add(word);
	}
	
	public void addWord(ADMatcherType type, String word) {
		this.type = type;
		this.count++;
		getWords().add(word);
	}
	
	public ADMatcherType prevType() {
		return ADMatcherType.prev(type);
	}
	
	public ADMatcherType nextType() {
		return ADMatcherType.next(type);
	}
	
}
