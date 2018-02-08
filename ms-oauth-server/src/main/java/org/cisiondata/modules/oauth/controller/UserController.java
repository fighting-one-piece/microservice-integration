package org.cisiondata.modules.oauth.controller;

import java.security.Principal;

import javax.annotation.Resource;

import org.cisiondata.modules.abstr.entity.Query;
import org.cisiondata.modules.abstr.web.ResultCode;
import org.cisiondata.modules.abstr.web.WebResult;
import org.cisiondata.modules.oauth.entity.User;
import org.cisiondata.modules.oauth.service.IUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/oauth/users")
public class UserController {

	@Resource(name = "userService")
	private IUserService userService = null;
	
	@RequestMapping(value = "/current", method = RequestMethod.GET)
	public Principal user(Principal user) {
		return user;
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST, headers = "Accept=application/json")
	public WebResult insertUser(@RequestBody User user) {
		WebResult webResult = new WebResult();
		try {
			userService.insert(user);
			webResult.setCode(ResultCode.SUCCESS.getCode());
			webResult.setData(ResultCode.SUCCESS.getDesc());
		} catch (Exception e) {
			webResult.setCode(ResultCode.FAILURE.getCode());
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public WebResult readUserById(@PathVariable("id") long id) {
		WebResult webResult = new WebResult();
		try {
			webResult.setCode(ResultCode.SUCCESS.getCode());
			webResult.setData(userService.readDataByPK(id, false));
		} catch (Exception e) {
			webResult.setCode(ResultCode.FAILURE.getCode());
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
	@PreAuthorize("hasRole('admin')")
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public WebResult readAllUsers() {
		WebResult webResult = new WebResult();
		try {
			webResult.setCode(ResultCode.SUCCESS.getCode());
			webResult.setData(userService.readDataListByCondition(new Query(), false));
		} catch (Exception e) {
			webResult.setCode(ResultCode.FAILURE.getCode());
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
}
