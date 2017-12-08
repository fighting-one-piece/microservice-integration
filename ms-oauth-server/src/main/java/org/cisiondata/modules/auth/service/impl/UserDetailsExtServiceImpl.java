package org.cisiondata.modules.auth.service.impl;

import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Resource;

import org.cisiondata.modules.auth.entity.User;
import org.cisiondata.modules.auth.service.IRoleService;
import org.cisiondata.modules.auth.service.IUserService;
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
        /**
        Iterator<String> iterator =  userRoleService.findRoles(user.getId()).iterator();
        while (iterator.hasNext()){
            collection.add(new SimpleGrantedAuthority(iterator.next()));
        }
		*/
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), collection);
    }

}
