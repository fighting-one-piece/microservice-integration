package org.platform.modules.user.dao;

import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.user.entity.Role;
import org.springframework.stereotype.Repository;

@Repository("roleDAO")
public interface RoleDAO extends GenericDAO<Role, Long> {

}
