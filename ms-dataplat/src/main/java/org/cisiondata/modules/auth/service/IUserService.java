package org.cisiondata.modules.auth.service;

import java.util.List;

import org.cisiondata.modules.abstr.entity.Query;
import org.cisiondata.modules.auth.entity.User;
import org.cisiondata.modules.bootstrap.config.ds.DataSource;
import org.cisiondata.modules.bootstrap.config.ds.TargetDataSource;

public interface IUserService {

	/**
	 * 
	 * @param query
	 * @return
	 */
	@TargetDataSource(DataSource.SLAVE)
	public List<User> readDataListByCondition(Query query);
	
}
