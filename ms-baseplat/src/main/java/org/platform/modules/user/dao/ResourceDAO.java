package org.platform.modules.user.dao;

import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.user.entity.Resource;
import org.springframework.stereotype.Repository;

@Repository("resourceDAO")
public interface ResourceDAO extends GenericDAO<Resource, Long> {

}
