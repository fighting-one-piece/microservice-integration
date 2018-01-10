package org.cisiondata.modules.auth.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.cisiondata.modules.abstr.dao.GenericDAO;
import org.cisiondata.modules.abstr.entity.Query;
import org.cisiondata.modules.abstr.service.impl.GenericServiceImpl;
import org.cisiondata.modules.auth.dao.UserDAO;
import org.cisiondata.modules.auth.entity.User;
import org.cisiondata.modules.auth.service.IUserService;
import org.cisiondata.utils.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl extends GenericServiceImpl<User, Long> implements IUserService {

	@Resource(name = "userDAO")
	private UserDAO userDAO = null;
	
	@Override
	public GenericDAO<User, Long> obtainDAOInstance() {
		return userDAO;
	}
	
	@Override
	public User readUserByUsername(String username) throws BusinessException {
		if (StringUtils.isBlank(username)) throw new BusinessException("username is null");
		Query query = new Query();
		query.addCondition("username", username);
		return userDAO.readDataByCondition(query);
	}
	
	@Override
	public User readUserByUsernameAndPassword(String username, String password) throws BusinessException {
		if (StringUtils.isBlank(username)) throw new BusinessException("username is null");
		if (StringUtils.isBlank(password)) throw new BusinessException("password is null");
		Query query = new Query();
		query.addCondition("username", username);
		query.addCondition("password", password);
		return userDAO.readDataByCondition(query);
	}

}
