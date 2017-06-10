package org.cisiondata.modules.mongodb.dao;

import org.cisiondata.modules.mongodb.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IUserDAO extends MongoRepository<User, Long> {

}
