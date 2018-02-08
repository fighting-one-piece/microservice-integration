package org.cisiondata.modules.oauth.dao;

import org.cisiondata.modules.abstr.dao.GenericDAO;
import org.cisiondata.modules.oauth.entity.Client;
import org.springframework.stereotype.Repository;

@Repository("clientDAO")
public interface ClientDAO extends GenericDAO<Client, Long> {

}
