package org.platform.modules.user.dao;

import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.user.entity.User;
import org.springframework.stereotype.Repository;

@Repository("userDAO")
public interface UserDAO extends GenericDAO<User, Long> {

}
