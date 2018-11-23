package org.platform.modules.address.entity;

import java.util.ArrayList;
import java.util.List;

public class ADMatcher {
	
	private List<ADMatcherItem> items = null;

	public List<ADMatcherItem> getItems() {
		if (null == items) items = new ArrayList<ADMatcherItem>();
		return items;
	}

	public void setItems(List<ADMatcherItem> items) {
		this.items = items;
	}
	
	public void add(ADMatcherType type, String word) {
		boolean exists = false;
		for (int i = 0, len = getItems().size(); i < len; i++) {
			ADMatcherItem item = items.get(i);
			if (item.getType().equals(type)) {
				item.getWords().add(word);
				exists = true;
				break;
			}
		}
		if (!exists) {
			ADMatcherItem item = new ADMatcherItem();
			item.addWord(type, word);
			getItems().add(item);
		}
	}
	
	public ADMatcherItem prevItem(ADMatcherItem item) {
		ADMatcherType prevType = item.prevType();
		if (null == prevType) return null;
		for (int i = 0, len = getItems().size(); i < len; i++) {
			ADMatcherItem currentItem = items.get(i);
			if (prevType.equals(currentItem.getType())) return currentItem;
		}
		ADMatcherItem newItem = new ADMatcherItem();
		newItem.setType(prevType);
		return newItem;
	}
	
	public ADMatcherItem nextItem(ADMatcherItem item) {
		ADMatcherType nextType = item.nextType();
		if (null == nextType) return null;
		for (int i = 0, len = getItems().size(); i < len; i++) {
			ADMatcherItem currentItem = items.get(i);
			if (nextType.equals(currentItem.getType())) return currentItem;
		}
		ADMatcherItem newItem = new ADMatcherItem();
		newItem.setType(nextType);
		return newItem;
	}
	
	public ADMatcherItem lastItem() {
		int lastItemMatcherTypeValue = 0;
		ADMatcherItem lastItem = null;
		for (int i = 0, len = getItems().size(); i < len; i++) {
			ADMatcherItem item = items.get(i);
			int currentItemMatcherTypeValue = item.getType().value();
			if (currentItemMatcherTypeValue > lastItemMatcherTypeValue) {
				lastItemMatcherTypeValue = currentItemMatcherTypeValue;
				lastItem = item;
			}
		}
		return lastItem;
	}
	
	public void removeLastItem() {
		getItems().remove(getItems().size() - 1);
	}
	
}

