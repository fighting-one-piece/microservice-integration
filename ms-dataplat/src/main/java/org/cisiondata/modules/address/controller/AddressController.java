package org.cisiondata.modules.address.controller;

import javax.annotation.Resource;

import org.cisiondata.modules.abstr.web.ResultCode;
import org.cisiondata.modules.abstr.web.WebResult;
import org.cisiondata.modules.address.service.IAddressService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddressController {
	
	@Resource(name = "addressService")
	private IAddressService addressService = null;
	
	@RequestMapping(value = "/ads", method = RequestMethod.GET)
	public WebResult readAdministrativeDivisions(String address) {
		WebResult webResult = new WebResult();
		try {
			webResult.setData(addressService.readAdministrativeDivision(address));
			webResult.setCode(ResultCode.SUCCESS.getCode());
		} catch (Exception e) {
			webResult.setCode(ResultCode.FAILURE.getCode());
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
	@RequestMapping(value = "/3/ads", method = RequestMethod.GET)
	public WebResult read3AdministrativeDivisions(String address) {
		WebResult webResult = new WebResult();
		try {
			webResult.setData(addressService.read3AdministrativeDivision(address));
			webResult.setCode(ResultCode.SUCCESS.getCode());
		} catch (Exception e) {
			webResult.setCode(ResultCode.FAILURE.getCode());
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}

	@RequestMapping(value = "/5/ads", method = RequestMethod.GET)
	public WebResult read5AdministrativeDivisions(String address) {
		WebResult webResult = new WebResult();
		try {
			webResult.setData(addressService.read5AdministrativeDivision(address));
			webResult.setCode(ResultCode.SUCCESS.getCode());
		} catch (Exception e) {
			webResult.setCode(ResultCode.FAILURE.getCode());
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
}
