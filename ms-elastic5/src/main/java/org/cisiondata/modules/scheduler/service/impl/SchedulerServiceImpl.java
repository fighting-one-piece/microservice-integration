package org.cisiondata.modules.scheduler.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.cisiondata.modules.scheduler.ConsumerScheduler;
import org.cisiondata.modules.scheduler.service.IConsumeService;
import org.cisiondata.modules.scheduler.service.ISchedulerService;
import org.cisiondata.utils.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service("schedulerService")
public class SchedulerServiceImpl implements ISchedulerService {
	
	private Map<String, ConsumerScheduler> schedulers = new HashMap<String, ConsumerScheduler>();
	
	@Resource(name = "elastic5ConsumeService")
	private IConsumeService elastic5ConsumeService = null;
	
	@Override
	public void startupScheduler(String topic) throws BusinessException {
		ConsumerScheduler scheduler = null;
		if ("elastic5".equalsIgnoreCase(topic)) {
			scheduler = new ConsumerScheduler("elastic5", 12, 1000, elastic5ConsumeService);
		} 
		scheduler.startup();
//		new Thread(scheduler).start();
		schedulers.put(topic, scheduler);
	}

	@Override
	public void shutdownScheduler(String topic) throws BusinessException {
		ConsumerScheduler scheduler = schedulers.get(topic);
		if (null == scheduler) throw new BusinessException("Scheduler不存在"); 
		scheduler.shutdown();
	}
	
}
