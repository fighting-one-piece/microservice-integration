package org.cisiondata.modules.test.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("测试相关api")
@RestController
@RequestMapping(value = "/test")
public class TestController {

	@ApiOperation("获取用户信息")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "header", name = "username", dataType = "String", required = true, value = "用户的姓名", defaultValue = "zhaojigang"),
			@ApiImplicitParam(paramType = "query", name = "password", dataType = "String", required = true, value = "用户的密码", defaultValue = "wangna") })
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
			@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@RequestMapping(value = "/getUser", method = RequestMethod.GET)
	public Map<String, Object> readUser(@RequestHeader("username") String username,
			@RequestParam("password") String password) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", username);
		map.put("password", password);
		return map;
	}

	@ApiOperation(value = "根据参数读取信息", notes = "根据参数读取信息")
	@ApiImplicitParam(paramType = "query", name = "param", value = "参数", required = true, dataType = "String")
	@ResponseBody
	@RequestMapping(value = "/infos", method = RequestMethod.GET)
	public Map<String, Object> readInfo(@RequestParam(name = "param") String param) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("param", param);
		map.put("name", "zhangsan");
		map.put("age", 20);
		return map;
	}

	@ApiOperation(value = "根据参数读取信息", notes = "根据参数读取信息")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "path", name = "param", value = "参数", dataType = "String", required = true, defaultValue = "xxx") 
	})
	@ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"),
	@ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
	@ResponseBody
	@RequestMapping(value = "/infos/{param}", method = RequestMethod.GET)
	public Map<String, Object> readInfoByParam(@PathVariable(name = "param") String param) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("param", param);
		map.put("name", "zhangsan");
		map.put("age", 20);
		return map;
	}

}
