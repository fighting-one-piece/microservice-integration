package org.cisiondata.modules.datatransfer.controller;

import javax.annotation.Resource;

import org.cisiondata.modules.abstr.web.ResultCode;
import org.cisiondata.modules.abstr.web.WebResult;
import org.cisiondata.modules.datatransfer.service.impl.TransferListenServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/transfer")
public class TransferController {
	
	@Resource(name = "transferListenService")
	private TransferListenServiceImpl transferListenService = null;
	
	@RequestMapping(value = "/user/dir", method = RequestMethod.GET)
	public String readUserDir() {
		System.out.println(System.getProperty("user.dir"));
		return System.getProperty("user.dir");
	}
	
	@RequestMapping(value = "/listener/startup", method = RequestMethod.GET)
	public WebResult startupListener() {
		WebResult webResult = new WebResult();
		try {
			webResult.setData(transferListenService.startupListen());
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (Exception e) {
			webResult.setResultCode(ResultCode.FAILURE);
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
	@RequestMapping(value = "/listener/shutdown", method = RequestMethod.GET)
	public WebResult stopListen() {
		WebResult webResult = new WebResult();
		try {
			webResult.setData(transferListenService.shutdownListen());
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (Exception e) {
			webResult.setResultCode(ResultCode.FAILURE);
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}

}
