package org.cisiondata.modules.abstr.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.cisiondata.utils.exception.BusinessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller("exceptionController")
public class ExceptionController {

	@ExceptionHandler(BusinessException.class)
	public String businessException(BusinessException be, HttpServletRequest request) {
		request.setAttribute("error", be.getMessage());
		return "errors/business";
	}
	
}
