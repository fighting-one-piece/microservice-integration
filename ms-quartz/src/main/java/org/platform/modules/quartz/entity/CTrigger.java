package org.platform.modules.quartz.entity;

import java.io.Serializable;
import java.util.Date;

public class CTrigger implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** Trigger组名*/
	private String group = null;
	/** Trigger名称*/
	private String name = null;
	/** Trigger描述*/
	private String description = null;
	/** Cron表达式*/
	private String cronExpression = null;
	/** 开始时间*/
	private Date startTime = null;
	/** 结束时间*/
	private Date endTime = null;
	/** 最终Fire时间*/
	private Date finalFireTime = null;
	/** 状态*/
	private String status = null;
	
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

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getFinalFireTime() {
		return finalFireTime;
	}

	public void setFinalFireTime(Date finalFireTime) {
		this.finalFireTime = finalFireTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
