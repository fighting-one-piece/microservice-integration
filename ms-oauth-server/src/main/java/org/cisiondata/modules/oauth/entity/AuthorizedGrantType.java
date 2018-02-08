package org.cisiondata.modules.oauth.entity;

public enum AuthorizedGrantType {
	
	AUTHORIZATION_CODE("authorization_code"),
	CLIENT_CREDENTIALS("client_credentials"),
	PASSWORD("password"),
	REFRESH_TOKEN("refresh_token");
	
	private String value = null;
	
	private AuthorizedGrantType(String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}

}
