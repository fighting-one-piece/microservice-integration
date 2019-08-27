package org.platform.modules.user.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.platform.modules.abstr.annotation.ApiV1RestController;
import org.platform.modules.abstr.web.ResultCode;
import org.platform.modules.abstr.web.WebResult;
import org.platform.modules.user.entity.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value="用户模块", tags={"用户操作接口"})
@ApiV1RestController
public class UserController {
	
	static Map<Long, User> users = Collections.synchronizedMap(new HashMap<Long, User>());

	@ApiOperation(value = "新增用户", notes = "新增用户")
	@RequestMapping(value = "/user/insert", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "header", name = "accessToken", value = "访问令牌", required = true, dataType = "String", defaultValue = "xxxxxx"),
		@ApiImplicitParam(paramType= "form", name = "id", value = "用户ID", required = true, dataType = "Long"),
		@ApiImplicitParam(paramType= "form", name = "name", value = "用户姓名", required = true, dataType = "String"),
		@ApiImplicitParam(paramType= "form", name = "age", value = "用户年龄", required = true, dataTypeClass = Integer.class)
	})
	public WebResult insertUser(@RequestParam(name = "id") Long id, @RequestParam(name = "name") String name, 
			@RequestParam(name = "age") Integer age) {
		WebResult webResult = new WebResult();
		try {
			users.put(id, new User(id, name, age));
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (Exception exception) {
			webResult.setResultCode(ResultCode.FAILURE);
		}
		return webResult;
	}

	@ApiOperation(value = "更新用户", notes = "根据ID更新指定对象")
	@RequestMapping(value = "/user/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "header", name = "accessToken", value = "访问令牌", required = true, dataType = "String", defaultValue = "xxxxxx"),
		@ApiImplicitParam(paramType="form", name = "id", value = "用户ID", required = true, dataType = "Long"),
		@ApiImplicitParam(paramType="form", name = "name", value = "用户姓名", required = true, dataType = "String"),
		@ApiImplicitParam(paramType="form", name = "age", value = "用户年龄", required = true, dataTypeClass=Integer.class)
	})
	public WebResult updateUser(@RequestParam(name = "id") Long id, @RequestParam(name = "name") String name, 
			@RequestParam(name = "age") Integer age) {
		WebResult webResult = new WebResult();
		try {
			if (!users.containsKey(id)) throw new RuntimeException("ID不存在");
			User user = users.get(id);
			user.setName(name);
			user.setAge(age);
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (RuntimeException rexception) {
			webResult.setCode(ResultCode.FAILURE.getCode());
			webResult.setFailure(rexception.getMessage());
		} catch (Exception exception) {
			webResult.setResultCode(ResultCode.FAILURE);
		}
		return webResult;
	}

	@ApiOperation(value = "删除用户", notes = "根据ID删除指定对象")
	@RequestMapping(value = "/user/delete", method = RequestMethod.POST)
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "header", name = "accessToken", value = "访问令牌", required = true, dataType = "String", defaultValue = "xxxxxx"),
		@ApiImplicitParam(paramType = "query", name = "id", value = "用户ID", required = true, dataType = "Long"),
	})
	public WebResult deleteUser(@RequestParam(name = "id") Long id) {
		WebResult webResult = new WebResult();
		try {
			users.remove(id);
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (Exception exception) {
			webResult.setResultCode(ResultCode.FAILURE);
		}
		return webResult;
	}
	
	@ApiOperation(value = "用户信息", notes = "根据ID读取用户信息")
	@RequestMapping(value = "/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "header", name = "accessToken", value = "访问令牌", required = true, dataType = "String", defaultValue = "xxxxxx"),
		@ApiImplicitParam(paramType = "query", name = "id", value = "用户ID", required = true, dataType = "Long"),
	})
	public WebResult readUser(Long id) {
		WebResult webResult = new WebResult();
		try {
			webResult.setCode(ResultCode.SUCCESS.getCode());
			webResult.setData(users.get(id));
		} catch (Exception exception) {
			webResult.setResultCode(ResultCode.FAILURE);
		}
		return webResult;
	}
	
	@ApiOperation(value = "用户分页列表", notes = "根据条件读取用户分页列表")
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "header", name = "accessToken", value = "访问令牌", dataType = "String", required = true, defaultValue = "xxxxxx"),
		@ApiImplicitParam(paramType = "query", name = "currentPageNum", value = "当前页数", dataTypeClass=Integer.class, required = true, defaultValue = "1"),
		@ApiImplicitParam(paramType = "query", name = "rowNumberPerPage", value = "每数行数", dataTypeClass=Integer.class, required = true, defaultValue = "1")
	})
	/**
	@ApiResponses({ 
		@ApiResponse(code = 400, message = "请求参数无效"),
		@ApiResponse(code = 403, message = "ForbiddenAccess!资源不可用"),
		@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") 
	})
	*/
	public WebResult readUsers() {
		WebResult webResult = new WebResult();
		try {
			webResult.setCode(ResultCode.SUCCESS.getCode());
			webResult.setData(new ArrayList<User>(users.values()));
		} catch (Exception exception) {
			webResult.setResultCode(ResultCode.FAILURE);
		}
		return webResult;
	}
	
}
