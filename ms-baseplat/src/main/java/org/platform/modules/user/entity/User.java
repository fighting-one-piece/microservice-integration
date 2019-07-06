package org.platform.modules.user.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.platform.modules.abstr.entity.PKAutoEntity;

@Entity
@Table(name="T_USER")
public class User extends PKAutoEntity<Long> {

	private static final long serialVersionUID = 1L;

	/** 账号 */
	@Column(name = "ACCOUNT", length = 50, nullable = false, unique = true)
	private String account = null;
	/** 密码 */
	@Column(name = "PASSWORD", length = 50, nullable = false)
	private String password = null;
	/** 盐值 */
	@Column(name = "SALT", length = 50, nullable = false)
	private String salt = null;
	/** 昵称 */
	@Column(name = "NICK_NAME", length = 20)
	private String nickName = null;
	/** 真名 */
	@Column(name = "REAL_NAME", length = 20)
	private String realName = null;
	/** 身份证号码 */
	@Column(name = "ID_CARD", length = 18)
	private String idCard = null;
	/** 手机号码 */
	@Column(name = "MOBILE_PHONE", length = 11)
	private String mobilePhone = null;
	/** 邮箱 */
	@Column(name = "EMAIL", length = 20)
	private String email = null;
	/** 创建时间 */
	@Column(name = "CREATE_TIME")
	private Date createTime = null;
	/** 过期时间 */
	@Column(name = "EXPIRE_TIME")
	private Date expireTime = null;
	/** 是否删除标志 */
	@Column(name = "DELETE_FLAG", nullable = false)
	private Boolean deleteFlag = false;
	/** 用户属性列表 */
	private List<UserAttribute> attributes = null;
	/** 用户组 */
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	private Set<UserGroup> userGroups = null;
	/** 用户角色 */
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	private Set<UserRole> userRoles = null;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public Boolean getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public boolean hasDeleted() {
		return deleteFlag;
	}
	
	public boolean hasExpired() {
		return expireTime.before(Calendar.getInstance().getTime());
	}

	public boolean isValid() {
		return hasDeleted() || hasExpired() ? false : true;
	}

	public List<UserAttribute> getAttributes() {
		if (null == attributes) attributes = new ArrayList<UserAttribute>();
		return attributes;
	}

	public void setAttributes(List<UserAttribute> attributes) {
		this.attributes = attributes;
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

	public Set<UserRole> getUserRoles() {
		if (null == userRoles) {
			userRoles = new HashSet<UserRole>();
		}
		return this.userRoles;
	}

	public void setUserRoles(Set<UserRole> userRoles) {
		this.userRoles = userRoles;
	}
	
	public interface ATTRIBUTE {
		
		/** ACCESS TOKEN */
		public static final String ACCESS_TOKEN = "accessToken";
		/** SESSIONID */
		public static final String SESSION_ID = "sessionId";
		
	}
	
}
