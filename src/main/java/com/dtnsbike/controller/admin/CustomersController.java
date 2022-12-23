package com.dtnsbike.controller.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.entity.Accounts;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
@RequestMapping("/admin")
public class CustomersController {
	
	@Autowired
	AccountsDAO accountDao;
	
	@Autowired
	HttpServletRequest request;
	
	String path = "admin/common/customers/";
	
//	Danh sách khách hàng
	@RequestMapping("/customer-list.html")
	public String getList() {
		return path+"customer-list.html";
	}
//	Form cập nhật khách hàng
	@RequestMapping("/customer-update.html")
	public String getFormUpdate() {
		return path+"customer-update.html";
	}
	@ModelAttribute("accountProfile")
	public Accounts getListSizes() throws JsonMappingException, JsonProcessingException, IOException {
		return accountDao.findById(request.getUserPrincipal().getName()).get();
	}
}
