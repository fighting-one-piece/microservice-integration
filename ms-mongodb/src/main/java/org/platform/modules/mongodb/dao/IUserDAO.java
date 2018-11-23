package org.platform.modules.mongodb.dao;

import org.platform.modules.mongodb.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IUserDAO extends MongoRepository<User, Long> {

}
