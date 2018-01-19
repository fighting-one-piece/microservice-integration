package org.cisiondata.modules.oauth.dao;

import org.cisiondata.modules.abstr.dao.GenericDAO;
import org.cisiondata.modules.oauth.entity.User;
import org.springframework.stereotype.Repository;

@Repository("userDAO")
public interface UserDAO extends GenericDAO<User, Long>  {

}
