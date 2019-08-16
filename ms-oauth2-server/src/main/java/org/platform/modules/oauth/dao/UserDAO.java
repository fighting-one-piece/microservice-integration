package org.platform.modules.oauth.dao;

import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.oauth.entity.User;
import org.springframework.stereotype.Repository;

@Repository("userDAO")
public interface UserDAO extends GenericDAO<User, Long>  {

}
