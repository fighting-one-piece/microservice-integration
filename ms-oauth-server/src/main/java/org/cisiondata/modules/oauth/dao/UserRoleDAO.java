package org.cisiondata.modules.oauth.dao;

import org.cisiondata.modules.abstr.dao.GenericDAO;
import org.cisiondata.modules.oauth.entity.UserRole;
import org.springframework.stereotype.Repository;

@Repository("userRoleDAO")
public interface UserRoleDAO extends GenericDAO<UserRole, Long> {
	
}
