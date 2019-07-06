package org.platform.modules.user.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.platform.modules.abstr.entity.PKAutoEntity;

@Entity
@Table(name="T_GROUP_ROLE")
public class GroupRole extends PKAutoEntity<Long> {

	private static final long serialVersionUID = 1L;

	/** 组ID */
	@Column(name="GROUP_ID")
	private Long groupId = null;
	/** 角色ID */
	@Column(name="ROLE_ID")
	private Long roleId = null;
	/** 组 */
	@ManyToOne(cascade=CascadeType.REFRESH, optional=true)
	@JoinColumn(name="GROUP_ID")
	private Group group = null;
	/** 角色 */
	@ManyToOne(cascade=CascadeType.REFRESH, optional=true)
	@JoinColumn(name="ROLE_ID")
	private Role role = null;
	/** 优先权 */
	@Column(name="PRIORITY")
	private Integer priority = null;

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Integer getPriority() {
		return this.priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

}
