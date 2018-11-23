package org.platform.modules.oauth.dao;

import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.oauth.entity.UserRole;
import org.springframework.stereotype.Repository;

@Repository("userRoleDAO")
public interface UserRoleDAO extends GenericDAO<UserRole, Long> {
	
}
