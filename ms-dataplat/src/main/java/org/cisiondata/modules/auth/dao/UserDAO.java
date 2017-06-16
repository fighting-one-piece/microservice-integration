package org.cisiondata.modules.auth.dao;

import java.util.List;

import org.cisiondata.modules.abstr.entity.Query;
import org.cisiondata.modules.auth.entity.User;
import org.springframework.stereotype.Repository;

@Repository("userDAO")
public interface UserDAO {

	/**
	 * 
	 * @param query
	 * @return
	 */
	public List<User> readDataListByCondition(Query query);
	
}
