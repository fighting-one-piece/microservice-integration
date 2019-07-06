package org.platform.modules.user.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.platform.modules.abstr.entity.PKAutoEntity;

@Entity
@Table(name="T_ROLE_RESOURCE")
public class RoleResource extends PKAutoEntity<Long> {

	private static final long serialVersionUID = 1L;

	/** 角色ID */
	@Column(name="ROLE_ID")
	private Long roleId = null;
	/** 资源ID */
	@Column(name="RESOURCE_ID")
	private Long resourceId = null;
	/** 角色 */
	@ManyToOne(cascade=CascadeType.REFRESH, optional=true)
	@JoinColumn(name="ROLE_ID")
	private Role role = null;
	/** 资源 */
	@ManyToOne(cascade=CascadeType.REFRESH, optional=true)
	@JoinColumn(name="RESOURCE_ID")
	private Resource resource = null;
	/** 优先权 */
	@Column(name="PRIORITY")
	private Integer priority = null;
	
	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Integer getPriority() {
		return this.priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

}
