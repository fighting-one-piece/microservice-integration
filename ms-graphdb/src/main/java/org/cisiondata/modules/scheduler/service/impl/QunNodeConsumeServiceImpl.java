package org.cisiondata.modules.scheduler.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.cisiondata.modules.qqrelation.service.IQQGraphService;
import org.cisiondata.modules.scheduler.service.IConsumeService;
import org.springframework.stereotype.Service;

@Service("qunNodeConsumeService")
public class QunNodeConsumeServiceImpl implements IConsumeService {
	
	@Resource(name = "qqGraphService")
	private IQQGraphService qqGraphService = null;
	
	@Override
	public void handle(String message) throws RuntimeException {
		qqGraphService.insertQQQunNode(message);
	}
	
	@Override
	public void handle(List<String> messages) throws RuntimeException {
		qqGraphService.insertQQQunNodes(messages);
		System.out.println(messages.size() + " qun messages consume finish!!!!!!");
	}
	
	
	
}
