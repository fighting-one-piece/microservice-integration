package org.cisiondata.modules.elastic.entity;

import java.util.ArrayList;
import java.util.List;

public class BoolCondition extends Condition {

	private List<Condition> mustClauses = new ArrayList<Condition>();

	private List<Condition> mustNotClauses = new ArrayList<Condition>();

	private List<Condition> filterClauses = new ArrayList<Condition>();

	private List<Condition> shouldClauses = new ArrayList<Condition>();
	
    public BoolCondition must(Condition condition) {
        mustClauses.add(condition);
        return this;
    }

    public BoolCondition filter(Condition condition) {
        filterClauses.add(condition);
        return this;
    }

    public BoolCondition mustNot(Condition condition) {
        mustNotClauses.add(condition);
        return this;
    }

    public BoolCondition should(Condition condition) {
        shouldClauses.add(condition);
        return this;
    }
    
    public boolean hasClauses() {
        return !(mustClauses.isEmpty() && shouldClauses.isEmpty() && mustNotClauses.isEmpty() && filterClauses.isEmpty());
    }

	public List<Condition> getMustClauses() {
		return mustClauses;
	}

	public List<Condition> getMustNotClauses() {
		return mustNotClauses;
	}

	public List<Condition> getFilterClauses() {
		return filterClauses;
	}

	public List<Condition> getShouldClauses() {
		return shouldClauses;
	}

}
