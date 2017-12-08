package org.cisiondata.modules.auth.dao;

import org.cisiondata.modules.abstr.dao.GenericDAO;
import org.cisiondata.modules.auth.entity.Role;
import org.springframework.stereotype.Repository;

@Repository("roleDAO")
public interface RoleDAO extends GenericDAO<Role, Long> {
	
}
