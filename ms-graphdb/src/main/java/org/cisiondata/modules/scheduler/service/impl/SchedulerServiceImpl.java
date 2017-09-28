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
	
	@Resource(name = "qqNodeConsumeService")
	private IConsumeService qqNodeConsumeService = null;
	
	@Resource(name = "qunNodeConsumeService")
	private IConsumeService qunNodeConsumeService = null;
					  
	@Resource(name = "qqRelationConsumeService")
	private IConsumeService qqRelationNodeConsumeService = null;
	
	@Override
	public void startupScheduler(String topic) throws BusinessException {
		ConsumerScheduler scheduler = null;
		if ("qqnode".equalsIgnoreCase(topic)) {
			scheduler = new ConsumerScheduler("qqnode", 6, 1000, qqNodeConsumeService);
		} else if ("qunnode".equalsIgnoreCase(topic)) {
			scheduler = new ConsumerScheduler("qunnode", 6, 1000, qunNodeConsumeService);
		} else if ("qqrelation".equalsIgnoreCase(topic)) {
			scheduler = new ConsumerScheduler("qqrelation", 6, 1000, qqRelationNodeConsumeService);
		}
		scheduler.startup();
		schedulers.put(topic, scheduler);
	}

	@Override
	public void shutdownScheduler(String topic) throws BusinessException {
		ConsumerScheduler scheduler = schedulers.get(topic);
		if (null == scheduler) throw new BusinessException("Scheduler不存在"); 
		scheduler.shutdown();
	}
	
}
