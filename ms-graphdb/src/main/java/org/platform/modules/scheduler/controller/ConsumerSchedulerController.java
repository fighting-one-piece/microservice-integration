package org.platform.modules.scheduler.controller;

import javax.annotation.Resource;

import org.platform.modules.abstr.web.ResultCode;
import org.platform.modules.abstr.web.WebResult;
import org.platform.modules.scheduler.service.ISchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/scheduler/consumers")
public class ConsumerSchedulerController {
	
	private Logger LOG = LoggerFactory.getLogger(ConsumerSchedulerController.class);
	
	@Resource(name = "schedulerService")
	private ISchedulerService schedulerService = null;

	@ResponseBody
	@RequestMapping(value = "/{topic}/startup", method = RequestMethod.GET)
	public WebResult startupScheduler(@PathVariable("topic") String topic) {
		WebResult webResult = new WebResult();
		try {
			schedulerService.startupScheduler(topic);
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			webResult.setResultCode(ResultCode.FAILURE);
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
	@ResponseBody
	@RequestMapping(value = "/{topic}/shutdown", method = RequestMethod.GET)
	public WebResult shutdownScheduler(@PathVariable("topic") String topic) {
		WebResult webResult = new WebResult();
		try {
			schedulerService.shutdownScheduler(topic);
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			webResult.setResultCode(ResultCode.FAILURE);
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
}
