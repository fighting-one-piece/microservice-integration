package org.platform.modules.user.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.platform.modules.abstr.entity.PKAutoEntity;

@Entity
@Table(name="T_RESOURCE")
public class Resource extends PKAutoEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	public interface TYPE {
		/** MENU */
		public static final Integer MENU = 1;
	}
	
	/** 根ID*/
	public static final Long ROOT = 0L;
	
	/** 名称 */
	@Column(name="NAME", length = 20, nullable = false)
	private String name = null;
	/** 标识 */
	@Column(name="IDENTITY", length = 20, nullable = false)
	private String identity = null;
	/** URL */
	@Column(name="URL", length = 50, nullable = false)
	private String url = null;
	/** 类型 */
	@Column(name="TYPE")
	private Integer type = null;
	/** 图标 */
	@Column(name="ICON", length = 20)
	private String icon = null;
	/** 优先权 */
	@Column(name="PRIORITY")
	private Integer priority = null;
	/** 删除标志 */
	@Column(name = "DELETE_FLAG")
	private Boolean deleteFlag = false;
	/** 父资源ID */
	@Column(name = "PARENT_ID")
	private Long parentId = null;
	/** 父资源 */
	@ManyToOne(cascade = CascadeType.REFRESH, optional = true)
	@JoinColumn(name = "PARENT_ID")
	private Resource parent = null;
	/** 子资源 */
	@OneToMany(mappedBy="parent", fetch=FetchType.LAZY)
	private Set<Resource> children = null;
	/** 是否有子节点 */
	private boolean hasChildren;

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Boolean getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Resource getParent() {
		return parent;
	}

	public void setParent(Resource parent) {
		this.parent = parent;
	}

	public Set<Resource> getChildren() {
		if (children == null) {
			children = new HashSet<Resource>();
		}
		return children;
	}

	public void setChildren(Set<Resource> children) {
		this.children = children;
	}

	public boolean isHasChildren() {
		return hasChildren;
	}
	
	public boolean hasChildren() {
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
	
	public Long getParentId() {
		return null == parent ? null : parent.getId();
	}
	
	public boolean isTop() {
		return null != this.getParent() && Resource.ROOT.equals(this.getParent().getId());
	}

}
