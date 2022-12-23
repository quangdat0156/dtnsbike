package com.dtnsbike.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.service.SessionService;

@Service
public class BlockLogin implements HandlerInterceptor {
	@Autowired
	SessionService session;

	@Autowired
	AccountsDAO dao;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		if (request.getUserPrincipal() != null) {
			Accounts accounts = dao.findById(request.getUserPrincipal().getName()).get();
			if (accounts != null) {
				response.sendRedirect("/DTNsBike/index.html");
			}
		}
		return true;
	}
}
