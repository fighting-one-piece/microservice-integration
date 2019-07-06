package org.platform.modules.user.service.impl;

import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.abstr.service.impl.GenericServiceImpl;
import org.platform.modules.user.dao.ResourceDAO;
import org.platform.modules.user.entity.Resource;
import org.platform.modules.user.service.IResourceService;
import org.springframework.stereotype.Service;

@Service("resourceService")
public class ResourceServiceImpl extends GenericServiceImpl<Resource, Long> implements IResourceService {

	@javax.annotation.Resource(name = "resourceDAO")
	private ResourceDAO resourceDAO = null;

	@Override
	public GenericDAO<Resource, Long> obtainDAOInstance() {
		return resourceDAO;
	}

}
