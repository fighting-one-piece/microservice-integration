package org.cisiondata.modules.mongodb.dao.impl;

import java.util.List;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository("userDAO")
public class UserDAOImpl {

	@Autowired
	private MongoTemplate mongoTemplate = null;
	
	public List<User> readDataList() {
		return mongoTemplate.findAll(User.class);
	}
	
}
