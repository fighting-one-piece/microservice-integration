package org.cisiondata.modules.oauth.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.cisiondata.modules.abstr.entity.PKAutoEntity;

@Entity
@Table(name = "T_CLIENT")
public class Client extends PKAutoEntity<Long> {

	private static final long serialVersionUID = 1L;

	/** 客户ID */
	@Column(name = "CLIENT_ID")
	private String clientId = null;
	/** 客户密码 */
	@Column(name = "CLIENT_SECRET")
	private String clientSecret = null;
	/** 资源ID */
	@Column(name = "RESOURCE_IDS")
	private String resourceIds = null;
	/** 域 */
	@Column(name = "SCOPE")
	private String scope = null;
	/** 授权 */
	@Column(name = "AUTHORITIES")
	private String authorities = null;
	/** 授权类型 */
	@Column(name = "AUTHORIZED_GRANT_TYPES")
	private String authorizedGrantTypes = null;
	/** Token有效时间 */
	@Column(name = "ACCESS_TOKEN_VALIDITY")
	private Integer accessTokenValidity = null;
	/** Token刷新时间 */
	@Column(name = "REFRESH_TOKEN_VALIDITY")
	private Integer refreshTokenValidity = null;
	/** AUTOAPPROVE */
	@Column(name = "AUTOAPPROVE")
	private String autoapprove = null;
	/** WEB_SERVER_REDIRECT_URI */
	@Column(name = "WEB_SERVER_REDIRECT_URI")
	private String webServerRedirectUri = null;
	/** WEB_SERVER_REDIRECT_URI */
	@Column(name = "ADDITIONAL_INFORMATION")
	private String additionalInformation = null;
	/** 创建时间 */
	@Column(name = "CREATE_TIME")
	private Date createTime = null;
	/** 过期时间 */
	@Column(name = "EXPIRE_TIME")
	private Date expireTime = null;
	/** 是否删除标志 */
	@Column(name = "DELETE_FLAG")
	private Boolean deleteFlag = false;
	
	public Client() {}
	
	public Client(String clientId, String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(String resourceIds) {
		this.resourceIds = resourceIds;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getAuthorities() {
		return authorities;
	}

	public void setAuthorities(String authorities) {
		this.authorities = authorities;
	}

	public String getAuthorizedGrantTypes() {
		return authorizedGrantTypes;
	}

	public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
		this.authorizedGrantTypes = authorizedGrantTypes;
	}

	public Integer getAccessTokenValidity() {
		return accessTokenValidity;
	}

	public void setAccessTokenValidity(Integer accessTokenValidity) {
		this.accessTokenValidity = accessTokenValidity;
	}

	public Integer getRefreshTokenValidity() {
		return refreshTokenValidity;
	}

	public void setRefreshTokenValidity(Integer refreshTokenValidity) {
		this.refreshTokenValidity = refreshTokenValidity;
	}

	public String getAutoapprove() {
		return autoapprove;
	}

	public void setAutoapprove(String autoapprove) {
		this.autoapprove = autoapprove;
	}

	public String getWebServerRedirectUri() {
		return webServerRedirectUri;
	}

	public void setWebServerRedirectUri(String webServerRedirectUri) {
		this.webServerRedirectUri = webServerRedirectUri;
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
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

}
