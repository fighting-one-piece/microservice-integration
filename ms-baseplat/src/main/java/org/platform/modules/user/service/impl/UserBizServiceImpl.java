package org.platform.modules.user.service.impl;

import java.text.SimpleDateFormat;

import org.platform.modules.user.service.IUserBizService;
import org.platform.utils.date.DateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service("userBizService")
public class UserBizServiceImpl extends UserServiceImpl implements IUserBizService, InitializingBean {

	private Logger LOG = LoggerFactory.getLogger(UserBizServiceImpl.class);

	private static SimpleDateFormat SDF = DateFormatter.TIME.get();

	@Override
	public void afterPropertiesSet() throws Exception {
	}

}
