package org.cisiondata.modules.oauth.dao;

import org.cisiondata.modules.abstr.dao.GenericDAO;
import org.cisiondata.modules.oauth.entity.Role;
import org.springframework.stereotype.Repository;

@Repository("roleDAO")
public interface RoleDAO extends GenericDAO<Role, Long> {
	
}
