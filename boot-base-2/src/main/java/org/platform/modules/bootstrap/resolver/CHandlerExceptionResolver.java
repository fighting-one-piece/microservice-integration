package org.platform.modules.bootstrap.resolver;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.platform.modules.abstr.entity.ResultCode;
import org.platform.modules.abstr.entity.Result;
import org.platform.utils.exception.BusinessException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;

//@Component
public class CHandlerExceptionResolver implements HandlerExceptionResolver {
	
	private FastJsonJsonView fastJsonJsonView = new FastJsonJsonView();

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, 
			Object handler, Exception exception) {
		Result result = new Result();
		if (exception instanceof BusinessException) {
			BusinessException be = (BusinessException) exception;
			result.setCode(be.getCode());
			result.setFailure(be.getMessage());
		} else {
			result.setCode(ResultCode.FAILURE.getCode());
			result.setFailure(exception.getMessage());
		}
		Map<String, Object> exceptionParamMap = new HashMap<String, Object>();  
        exceptionParamMap.put("code", result.getCode());  
        exceptionParamMap.put("data", result.getFailure());  
		fastJsonJsonView.setAttributesMap(exceptionParamMap);
		ModelAndView modelAndView = new ModelAndView(); 
		modelAndView.setView(fastJsonJsonView);
		return modelAndView;
	}

}
