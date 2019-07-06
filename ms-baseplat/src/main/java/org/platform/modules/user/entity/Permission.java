package org.platform.modules.user.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.platform.modules.abstr.entity.PKAutoEntity;

@Entity
@Table(name="T_PERMISSION")
public class Permission extends PKAutoEntity<Long> {

	private static final long serialVersionUID = 1L;

	/** 用户类型*/
	public static final Integer PRINCIPAL_TYPE_USER = 0;
	/** 组类型*/
	public static final Integer PRINCIPAL_TYPE_GROUP = 1;
	/** 角色类型*/
	public static final Integer PRINCIPAL_TYPE_ROLE = 2;
	/** 授权不允许*/
	public static final Integer AUTH_NO = 0;
	/** 授权允许*/
	public static final Integer AUTH_YES = 1;
	/** 授权不确定*/
	public static final Integer AUTH_NEUTRAL = 2;
	/** 授权新增*/
	public static final Integer AUTH_CREATE = 1;
	/** 授权读取*/
	public static final Integer AUTH_READ = 2;
	/** 授权修改*/
	public static final Integer AUTH_UPDATE = 3;
	/** 授权删除*/
	public static final Integer AUTH_DELETE = 4;
	/** 不继承*/
	public static final Integer EXTENDS_NO = 0;
	/** 继承*/
	public static final Integer EXTENDS_YES = 1;
	/** */
	public static final String CREATE = "create";
	public static final String READ = "read";
	public static final String UPDATE = "update";
	public static final String DELETE = "delete";

	/** 主体类型  0标识用户 1标识组 2标识角色 */
	@Column(name="PRINCIPAL_TYPE")
	private Integer principalType = null;
	/** 主体标识 */
	@Column(name="PRINCIPAL_ID")
	private Long principalId = null;
	/** 资源ID */
	@Column(name="RESOURCE_ID")
	private Long resourceId = null;
	/** 授权状态 用后四位标识CRUD操作 */
	@Column(name="AUTH_STATUS")
	private Integer authStatus = null;
	/** 继承状态  0标识不继承 1标识继承 */
	@Column(name="EXTEND_STATUS")
	private Integer extendStatus = null;

	public Integer getPrincipalType() {
		return principalType;
	}

	public void setPrincipalType(Integer principalType) {
		this.principalType = principalType;
	}

	public Long getPrincipalId() {
		return principalId;
	}

	public void setPrincipalId(Long principalId) {
		this.principalId = principalId;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public Integer getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(Integer authStatus) {
		this.authStatus = authStatus;
	}

	public Integer getExtendStatus() {
		return extendStatus;
	}

	public void setExtendStatus(Integer extendStatus) {
		this.extendStatus = extendStatus;
	}

	/**
	 * 授权
	 * @param permission
	 * @param yes
	 */
	public void setPermisssion(Integer permission, boolean yes) {
		int temp = 1;
		//根据CRUD判断
		temp = temp << permission;
		if (yes) {
			authStatus |= temp;//授予权限
		} else {
			authStatus &= ~temp;//保持原来权限
		}
	}

	/**
	 * 获取权限
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(int permission){
		//如果继承，则返回未定的授权
//		if(extendStatus == EXTENDS_YES){
//			return AUTH_NEUTRAL;
//		}
		int temp = 1 << permission;
		temp &= authStatus;
		return temp != 0 ? true : false;
	}
	
	public List<String> obtainOperateIdentities() {
		List<String> permissions = new ArrayList<String>();
		if (hasPermission(AUTH_CREATE)) permissions.add(CREATE);
		if (hasPermission(AUTH_READ)) permissions.add(READ);
		if (hasPermission(AUTH_UPDATE)) permissions.add(UPDATE);
		if (hasPermission(AUTH_DELETE)) permissions.add(DELETE);
		return permissions;
	}
	
}
