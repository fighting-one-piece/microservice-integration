package org.platform.modules.user.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.platform.modules.abstr.entity.PKAutoEntity;

@Entity
@Table(name="T_ROLE")
public class Role extends PKAutoEntity<Long> {

	private static final long serialVersionUID = 1L;

	/** 名称 */
	@Column(name="NAME", length = 50, nullable = false, unique = true)
	private String name = null;
	/** 标识 */
	@Column(name="IDENTITY", length = 50)
	private String identity = null;
	/** 描述 */
	@Column(name="DESC", length = 100)
	private String desc = null;
	/** 删除标志 */
	@Column(name = "DELETE_FLAG", nullable = false)
	private Boolean deleteFlag = false;
	/**用户角色集合 */
	@OneToMany(mappedBy="role", fetch=FetchType.LAZY)
	private Set<UserRole> userRoles = null;
	/**组角色集合 */
	@OneToMany(mappedBy="role", fetch=FetchType.LAZY)
	private Set<GroupRole> groupRoles= null;
	/**角色菜单集合 */
	@OneToMany(mappedBy="role", cascade = CascadeType.ALL,fetch=FetchType.LAZY)
	private Set<RoleResource> roleResources = null;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getDesc() {
		return this.desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Boolean getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public Set<UserRole> getUserRoles() {
		if (null == userRoles) {
			userRoles = new HashSet<UserRole>();
		}
		return this.userRoles;
	}

	public void setUserRoles(Set<UserRole> userRoles) {
		this.userRoles = userRoles;
	}
	
	public Set<GroupRole> getGroupRoles() {
		if (null == groupRoles) {
			groupRoles = new HashSet<GroupRole>();
		}
		return groupRoles;
	}

	public void setGroupRoles(Set<GroupRole> groupRoles) {
		this.groupRoles = groupRoles;
	}

	public Set<RoleResource> getRoleResources() {
		if (null == roleResources) {
			roleResources = new HashSet<RoleResource>();
		}
		return this.roleResources;
	}

	public void setRoleResources(Set<RoleResource> roleResources) {
		this.roleResources = roleResources;
	}

}
