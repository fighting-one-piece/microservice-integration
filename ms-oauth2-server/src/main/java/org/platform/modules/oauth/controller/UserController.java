package org.platform.modules.oauth.controller;

import java.security.Principal;

import javax.annotation.Resource;

import org.platform.modules.abstr.entity.Query;
import org.platform.modules.abstr.entity.ResultCode;
import org.platform.modules.abstr.entity.Result;
import org.platform.modules.oauth.entity.User;
import org.platform.modules.oauth.service.IUserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users")
public class UserController {

	@Resource(name = "userService")
	private IUserService userService = null;
	
	@RequestMapping(value = "/current", method = RequestMethod.GET)
	public Principal user(Principal principal) {
		return principal;
	}
	
	//@PreAuthorize("hasRole('USER-ADMIN')")
	@RequestMapping(value = "", method = RequestMethod.POST, headers = "Accept=application/json")
	public Result insertUser(@RequestBody User user) {
		Result webResult = new Result();
		try {
			userService.insert(user);
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (Exception e) {
			webResult.setCode(ResultCode.FAILURE.getCode());
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
	//@PreAuthorize("hasRole('USER-ADMIN')")
	@RequestMapping(value = "/{id}", method = RequestMethod.POST, headers = "Accept=application/json")
	public Result insertUser(@PathVariable("id") Long id, @RequestBody User user) {
		Result webResult = new Result();
		try {
			user.setId(id);
			userService.update(user);
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (Exception e) {
			webResult.setCode(ResultCode.FAILURE.getCode());
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
	//@PreAuthorize("hasRole('USER-ADMIN')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Result readUserById(@PathVariable("id") Long id) {
		Result webResult = new Result();
		try {
			webResult.setCode(ResultCode.SUCCESS.getCode());
			webResult.setData(userService.readDataByPK(id, false));
		} catch (Exception e) {
			webResult.setCode(ResultCode.FAILURE.getCode());
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
	//@PreAuthorize("hasRole('USER-ADMIN')")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Result readAllUsers() {
		Result webResult = new Result();
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
