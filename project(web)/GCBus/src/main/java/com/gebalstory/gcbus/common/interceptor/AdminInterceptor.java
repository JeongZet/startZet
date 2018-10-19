package com.gebalstory.gcbus.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AdminInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if(request.getParameter("adminPw").equals("0911")) {
			
			return true;
		}
		response.sendRedirect(request.getContextPath()+"/regForm.do");
		return false;
	}
}
