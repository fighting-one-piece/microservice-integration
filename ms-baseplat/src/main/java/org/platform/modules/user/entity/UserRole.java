package org.platform.modules.user.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.platform.modules.abstr.entity.PKAutoEntity;

@Entity
@Table(name="T_USER_ROLE")
public class UserRole extends PKAutoEntity<Long> {

	private static final long serialVersionUID = 1L;

	/** 用户ID */
	@Column(name="USER_ID")
	private Long userId = null;
	/** 角色ID */
	@Column(name="ROLE_ID")
	private Long roleId = null;
	/** 用户 */
	@ManyToOne(cascade=CascadeType.REFRESH, optional=true)
	@JoinColumn(name="USER_ID")
	private User user = null;
	/**角色 */
	@ManyToOne(cascade=CascadeType.REFRESH, optional=true)
	@JoinColumn(name="ROLE_ID")
	private Role role = null;
	/**优先权*/
	@Column(name="PRIORITY")
	private Integer priority = null;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
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
