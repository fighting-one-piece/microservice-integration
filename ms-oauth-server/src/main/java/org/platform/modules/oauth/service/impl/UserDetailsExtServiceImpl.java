package org.platform.modules.oauth.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.platform.modules.oauth.entity.Role;
import org.platform.modules.oauth.entity.User;
import org.platform.modules.oauth.service.IRoleService;
import org.platform.modules.oauth.service.IUserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsExtService")
public class UserDetailsExtServiceImpl implements UserDetailsService {
	
	@Resource(name = "userService")
	private IUserService userService = null;

	@Resource(name = "roleService")
	private IRoleService roleService = null;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userService.readUserByUsername(username);
        if(null == user){
        	throw new UsernameNotFoundException("用户名："+ username + "不存在！");
        }
        Collection<SimpleGrantedAuthority> collection = new HashSet<SimpleGrantedAuthority>();
        List<Role> roles = roleService.readRolesByUserId(user.getId());
        for (int i = 0, len = roles.size(); i < len; i++) {
        	collection.add(new SimpleGrantedAuthority("ROLE_" + roles.get(i).getIdentity()));
        }
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), collection);
    }

}
