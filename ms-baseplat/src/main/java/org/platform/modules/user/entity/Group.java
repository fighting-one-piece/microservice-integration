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
@Table(name = "T_GROUP")
public class Group extends PKAutoEntity<Long> {

	private static final long serialVersionUID = 1L;

	/** 名称 */
	@Column(name = "NAME")
	private String name = null;
	/** 标识 */
	@Column(name="IDENTITY")
	private String identity = null;
	/** 描述 */
	@Column(name = "DESCRIPTION")
	private String desc = null;
	/** 删除标志 */
	@Column(name = "DELETE_FLAG")
	private Boolean deleteFlag = false;
	/**用户组集合 */
	@OneToMany(mappedBy="group", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	private Set<UserGroup> userGroups = null;
	/**组角色集合 */
	@OneToMany(mappedBy="group", fetch=FetchType.LAZY)
	private Set<GroupRole> groupRoles = null;

	public String getName() {
		return name;
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
		return desc;
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

	public Set<GroupRole> getGroupRoles() {
		if (null == groupRoles) {
			groupRoles = new HashSet<GroupRole>();
		}
		return groupRoles;
	}

	public void setGroupRoles(Set<GroupRole> groupRoles) {
		this.groupRoles = groupRoles;
	}

	public Set<UserGroup> getUserGroups() {
		if (null == userGroups) {
			userGroups = new HashSet<UserGroup>();
		}
		return userGroups;
	}

	public void setUserGroups(Set<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}
	
	

}
