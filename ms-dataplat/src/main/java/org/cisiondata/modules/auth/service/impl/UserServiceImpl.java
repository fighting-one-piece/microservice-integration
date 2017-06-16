package org.cisiondata.modules.auth.service.impl;

import java.util.List;

import org.cisiondata.modules.abstr.entity.Query;
import org.cisiondata.modules.auth.dao.UserDAO;
import org.cisiondata.modules.auth.entity.User;
import org.cisiondata.modules.auth.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements IUserService {
	
	@Autowired
	private UserDAO userDAO = null;

	@Override
	public List<User> readDataListByCondition(Query query) {
		return userDAO.readDataListByCondition(query);
	}

}
