package org.platform.modules.address.controller;

import javax.annotation.Resource;

import org.platform.modules.abstr.entity.Result;
import org.platform.modules.address.service.IAddressService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddressController {
	
	@Resource(name = "addressService")
	private IAddressService addressService = null;
	
	@RequestMapping(value = "/ads", method = RequestMethod.GET)
	public Result readAdministrativeDivisions(String address) {
		return Result.buildSuccess(addressService.readAdministrativeDivision(address));
	}
	
	@RequestMapping(value = "/3/ads", method = RequestMethod.GET)
	public Result read3AdministrativeDivisions(String address) {
		return Result.buildSuccess(addressService.read3AdministrativeDivision(address));
	}

	@RequestMapping(value = "/5/ads", method = RequestMethod.GET)
	public Result read5AdministrativeDivisions(String address) {
		return Result.buildSuccess(addressService.read5AdministrativeDivision(address));
	}
	
}
