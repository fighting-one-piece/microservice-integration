package org.cisiondata.modules.scheduler.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cisiondata.modules.scheduler.ConsumerScheduler;
import org.cisiondata.modules.scheduler.service.IConsumeService;
import org.cisiondata.modules.scheduler.service.ISchedulerService;
import org.cisiondata.utils.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("schedulerService")
public class SchedulerServiceImpl implements ISchedulerService {
	
	private Logger LOG = LoggerFactory.getLogger(SchedulerServiceImpl.class);
	
	private Map<String, ConsumerScheduler> schedulers = new HashMap<String, ConsumerScheduler>();
	
	@Autowired
	private List<IConsumeService> consumeServiceList = null;
	
	@Override
	public void startupScheduler(String topic) throws BusinessException {
		for (int i = 0, len = consumeServiceList.size(); i < len; i++) {
			System.out.println("consumer service" + consumeServiceList.get(i).getClass());
		}
		ConsumerScheduler scheduler = new ConsumerScheduler(topic, 12, 1000, consumeServiceList);
		scheduler.startup();
		schedulers.put(topic, scheduler);
		LOG.info("{} scheduler startup success!!!", topic);
	}

	@Override
	public void shutdownScheduler(String topic) throws BusinessException {
		ConsumerScheduler scheduler = schedulers.get(topic);
		if (null == scheduler) throw new BusinessException("Scheduler不存在"); 
		scheduler.shutdown();
		LOG.info("{} scheduler shutdown success!!!", topic);
	}
	
}
