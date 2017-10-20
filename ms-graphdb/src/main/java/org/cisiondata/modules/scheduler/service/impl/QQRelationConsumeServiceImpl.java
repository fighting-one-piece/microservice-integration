package org.cisiondata.modules.scheduler.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.cisiondata.modules.qqrelation.service.IQQGraphService;
import org.cisiondata.modules.scheduler.service.IConsumeService;
import org.springframework.stereotype.Service;

@Service("qqRelationConsumeService")
public class QQRelationConsumeServiceImpl implements IConsumeService {
	
	@Resource(name = "qqGraphV2Service")
	private IQQGraphService qqGraphService = null;
	
	@Override
	public void handle(String message) throws RuntimeException {
		qqGraphService.insertQQQunRelation(message);
	}
	
	@Override
	public void handle(List<String> messages) throws RuntimeException {
		qqGraphService.insertQQQunRelations(messages);
		System.out.println(messages.size() + " qqrelation messages consume finish!!!!!!");
	}
	
	
	
}
