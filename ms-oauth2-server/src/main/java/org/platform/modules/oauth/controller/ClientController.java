package org.platform.modules.oauth.controller;

import javax.annotation.Resource;

import org.platform.modules.abstr.web.ResultCode;
import org.platform.modules.abstr.web.WebResult;
import org.platform.modules.oauth.entity.Client;
import org.platform.modules.oauth.service.IClientService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/oauth/clients")
public class ClientController {
	
	@Resource(name = "clientService")
	private IClientService clientService = null;
	
	//@PreAuthorize("hasRole('CLIENT-ADMIN')")
	@RequestMapping(value = "", method = RequestMethod.POST, headers = "Accept=application/json")
	public WebResult insertClient(@RequestBody Client client) {
		WebResult webResult = new WebResult();
		try {
			clientService.insert(client);
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (Exception e) {
			webResult.setCode(ResultCode.FAILURE.getCode());
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
	//@PreAuthorize("hasRole('CLIENT-ADMIN')")
	@RequestMapping(value = "/{id}", method = RequestMethod.POST, headers = "Accept=application/json")
	public WebResult updateClient(@PathVariable Long id, @RequestBody Client client) {
		WebResult webResult = new WebResult();
		try {
			client.setId(id);
			clientService.update(client);
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (Exception e) {
			webResult.setCode(ResultCode.FAILURE.getCode());
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
	//@PreAuthorize("hasRole('CLIENT-ADMIN')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public WebResult readClient(@PathVariable Long id) {
		WebResult webResult = new WebResult();
		try {
			webResult.setCode(ResultCode.SUCCESS.getCode());
			webResult.setData(clientService.readDataByPK(id, false));
		} catch (Exception e) {
			webResult.setCode(ResultCode.FAILURE.getCode());
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
}
