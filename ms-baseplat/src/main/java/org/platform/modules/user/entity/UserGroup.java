package org.platform.modules.user.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.platform.modules.abstr.entity.PKAutoEntity;

@Entity
@Table(name="T_USER_GROUP")
public class UserGroup extends PKAutoEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	/** 用户ID */
	@Column(name="USER_ID")
	private Long userId = null;
	/** 组ID */
	@JoinColumn(name="GROUP_ID")
	private Long groupId = null;
	/** 用户 */
	@ManyToOne(cascade=CascadeType.REFRESH, optional=true)
	@JoinColumn(name="USER_ID")
	private User user = null;
	/** 组 */
	@ManyToOne(cascade=CascadeType.REFRESH, optional=true)
	@JoinColumn(name="GROUP_ID")
	private Group group = null;
	/** 优先权*/
	@Column(name="PRIORITY")
	private Integer priority = null;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Integer getPriority() {
		return this.priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

}
