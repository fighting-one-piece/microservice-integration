package org.platform.modules.user.dao;

import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.user.entity.RoleResource;
import org.springframework.stereotype.Repository;

@Repository("roleResourceDAO")
public interface RoleResourceDAO extends GenericDAO<RoleResource, Long> {

}
