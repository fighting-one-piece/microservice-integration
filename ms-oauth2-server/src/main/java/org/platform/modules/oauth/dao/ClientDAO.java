package org.platform.modules.oauth.dao;

import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.oauth.entity.Client;
import org.springframework.stereotype.Repository;

@Repository("clientDAO")
public interface ClientDAO extends GenericDAO<Client, Long> {

}
