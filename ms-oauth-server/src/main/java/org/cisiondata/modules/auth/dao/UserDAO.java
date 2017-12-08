package org.cisiondata.modules.auth.dao;

import org.cisiondata.modules.abstr.dao.GenericDAO;
import org.cisiondata.modules.auth.entity.User;
import org.springframework.stereotype.Repository;

@Repository("userDAO")
public interface UserDAO extends GenericDAO<User, Long>  {

}
