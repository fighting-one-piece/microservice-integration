package org.platform.modules.quartz.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CJob implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 任务组名*/
	private String group = null;
	/** 任务名称*/
	private String name = null;
	/** 任务描述*/
	private String description = null;
	/** 任务实现类*/
	private String jobClass = null;
	/** 是否持久化*/
	private boolean durable = false;
	/** 是否请求恢复*/
	private boolean requestsRecovery = false;
	
	/** Trigger列表*/
	private List<CTrigger> triggers = null;
	
	public String getGroup() {
		return group;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getJobClass() {
		return jobClass;
	}

	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}

	public boolean isDurable() {
		return durable;
	}

	public void setDurable(boolean durable) {
		this.durable = durable;
	}

	public boolean isRequestsRecovery() {
		return requestsRecovery;
	}

	public void setRequestsRecovery(boolean requestsRecovery) {
		this.requestsRecovery = requestsRecovery;
	}

	public List<CTrigger> getTriggers() {
		if (null == triggers) {
			triggers = new ArrayList<CTrigger>();
		}
		return triggers;
	}

	public void setTriggers(List<CTrigger> triggers) {
		this.triggers = triggers;
	}
	
}
