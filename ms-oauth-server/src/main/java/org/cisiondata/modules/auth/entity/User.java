package org.cisiondata.modules.auth.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.cisiondata.modules.abstr.entity.PKAutoEntity;

@Entity
@Table(name = "T_USER")
public class User extends PKAutoEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	
	/** 用户名 */
	@Column(name = "USERNAME")
    private String username = null;
    /** 密码 */
	@Column(name = "PASSWORD")
    private String password = null;
    /** 盐值 */
	@Column(name = "SALT")
	private String salt = null;
	/** 创建时间 */
	@Column(name = "CREATE_TIME")
	private Date createTime = null;
	/** 过期时间 */
	@Column(name = "EXPIRE_TIME")
	private Date expireTime = null;
	/** 是否删除标志 */
	@Column(name = "DELETE_FLAG")
	private Boolean deleteFlag = false;
    private List<Role> roles = new ArrayList<Role>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
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

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinTable(name="T_USER_ROLE",
            joinColumns={ @JoinColumn(name="uid", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="rid", referencedColumnName="id")})
    public List<Role> getRoles() {
		if (null == this.roles) {
			roles = new ArrayList<Role>();
		}
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
