package com.dtnsbike.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.service.RestApiService;
import com.dtnsbike.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DeleteSessionLogin implements HandlerInterceptor {
	@Autowired
	SessionService session;

	@Autowired
	AccountsDAO accountDAO;

	@Autowired
	RestApiService api;

	@Autowired
	HttpServletRequest request;

	@Autowired
	ObjectMapper mapper;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		if (request.getRemoteUser() == null) {
			session.remove("acount");
		} else {
			session.set("account", accountDAO.findById(request.getUserPrincipal().getName()).get());
		}
		return true;
	}
}
