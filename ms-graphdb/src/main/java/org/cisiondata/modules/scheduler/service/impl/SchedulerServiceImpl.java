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
		if ("qq".equalsIgnoreCase(topic)) {
			scheduler = new ConsumerScheduler("qq", 6, 1, qqNodeConsumeService);
		} else if ("qun".equalsIgnoreCase(topic)) {
			scheduler = new ConsumerScheduler("qun", 6, 1, qunNodeConsumeService);
		} else if ("qqqun".equalsIgnoreCase(topic)) {
			scheduler = new ConsumerScheduler("qqqun", 6, 1, qqRelationNodeConsumeService);
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
