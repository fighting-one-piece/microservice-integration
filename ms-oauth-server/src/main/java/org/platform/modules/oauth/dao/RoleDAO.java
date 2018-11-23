package org.platform.modules.oauth.dao;

import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.oauth.entity.Role;
import org.springframework.stereotype.Repository;

@Repository("roleDAO")
public interface RoleDAO extends GenericDAO<Role, Long> {
	
}
