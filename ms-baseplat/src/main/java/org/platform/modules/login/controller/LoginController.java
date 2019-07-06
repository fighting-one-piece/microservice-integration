package org.platform.modules.login.controller;

import javax.annotation.Resource;

import org.platform.modules.abstr.annotation.ApiV1RestController;
import org.platform.modules.abstr.web.ResultCode;
import org.platform.modules.abstr.web.WebResult;
import org.platform.modules.login.service.IUserLoginService;
import org.platform.utils.exception.BusinessException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@ApiV1RestController
public class LoginController {

	@Resource(name = "userLoginService")
	private IUserLoginService userLoginService = null;
	
	/**
	 * 登陆
	 * @param account
	 * @param verificationCode
	 * @return
	 */
	@RequestMapping(value = { "/login" }, method = RequestMethod.POST, headers = "Accept=application/json")
	public WebResult login(String account, String password, String uuid, String verificationCode) {
		WebResult webResult = new WebResult();
		try {
			webResult.setCode(ResultCode.SUCCESS.getCode());
			webResult.setData(userLoginService.login(account, password, uuid, verificationCode));
		} catch (BusinessException be) {
			webResult.setCode(be.getCode());
			webResult.setFailure(be.getMessage());
		} catch (Exception e) {
			webResult.setCode(ResultCode.SYSTEM_IS_BUSY.getCode());
			webResult.setFailure(ResultCode.SYSTEM_IS_BUSY.getDesc());
		}
		return webResult;
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public WebResult logout() {
		WebResult webResult = new WebResult();
		try {
			userLoginService.logout();
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (BusinessException be) {
			webResult.setCode(be.getCode());
			webResult.setFailure(be.getMessage());
		} catch (Exception e) {
			webResult.setCode(ResultCode.SYSTEM_IS_BUSY.getCode());
			webResult.setFailure(ResultCode.SYSTEM_IS_BUSY.getDesc());
		}
		return webResult;
	}
	
}
