package org.platform.modules.oauth.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.platform.modules.oauth.entity.Client;
import org.platform.modules.oauth.service.IClientService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

@Service("clientDetailsExtService")
public class ClientDetailsExtServiceImpl implements ClientDetailsService {

	private static final String DELIMITER = ",";
	
	@Resource(name = "clientService")
	private IClientService clientService = null;
	
	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		Client client = clientService.readClientByClientId(clientId);
		BaseClientDetails clientDetails = new BaseClientDetails();
		clientDetails.setClientId(clientId);
		clientDetails.setClientSecret(client.getClientSecret());;
		clientDetails.setResourceIds(stringToCollection(client.getResourceIds()));
		clientDetails.setScope(stringToCollection(client.getScope()));
		clientDetails.setAuthorizedGrantTypes(stringToCollection(client.getAuthorizedGrantTypes()));
		String authoritiesTxt = client.getAuthorities();
		if (null != authoritiesTxt && !"".equals(authoritiesTxt)) {
			List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
			String[] authorities = authoritiesTxt.split(DELIMITER);
			for (int i = 0, len = authorities.length; i < len; i++) {
				grantedAuthorities.add(new SimpleGrantedAuthority(authorities[i]));
			}
			clientDetails.setAuthorities(grantedAuthorities);
		}
		clientDetails.setAccessTokenValiditySeconds(client.getAccessTokenValidity());
		clientDetails.setRefreshTokenValiditySeconds(client.getRefreshTokenValidity());
		clientDetails.setAutoApproveScopes(stringToCollection(client.getAutoapprove()));
		return clientDetails;
	}
	
	private Collection<String> stringToCollection(String string) {
		if (null == string || "".equals(string)) return new ArrayList<String>();
		return Arrays.asList(string.split(DELIMITER));
	}

}
