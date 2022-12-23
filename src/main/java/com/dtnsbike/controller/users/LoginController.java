package com.dtnsbike.controller.users;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.dao.service.AccountsService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.model.LoginForm;
import com.dtnsbike.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
public class LoginController {

	@Autowired
	HttpServletRequest req;

	@Autowired
	HttpServletResponse resp;

	@Autowired
	AccountsDAO accountdao;

	@Autowired
	AccountsService accountsService;

	@Autowired
	SessionService session;
    @Autowired
    AccountsDAO accountsDAO;
	String path = "users/common/";
//	Login Page
	@GetMapping("/login.html")
	public String getLogin(Model m) {
	    
		LoginForm login = new LoginForm();
		m.addAttribute("loginForm", login);
		
		return path + "accounts/login";
	}

//	@PostMapping("/login.html")
	public String postLogin(Model m, @Valid @ModelAttribute("loginForm") LoginForm login, Errors errors) {
		if (!errors.hasErrors()) {
			String user = login.getUsername();
			String pass = login.getPass();

			Accounts acc = new Accounts();
			acc = accountsService.findById(user);

			if (acc instanceof Accounts) {
				if (pass.equals(acc.getPassword())) {
					if (acc.getActive()) {
						session.set("account", acc);
						m.addAttribute("message", "456");
						return "redirect:/index.html";
					} else {
						m.addAttribute("message", "789");
					}
				} else {
					m.addAttribute("message", "1011");
				}
			} else {
				m.addAttribute("message", "1011");
			}
		} else {
			m.addAttribute("message", "123");
		}
		return path + "accounts/login";
	}
	@ModelAttribute("accountProfile")
	public Accounts getListSizes() throws JsonMappingException, JsonProcessingException, IOException {
		if (req.getUserPrincipal() != null) {
			return accountsDAO.findById(req.getUserPrincipal().getName()).get();
		} else {
			return null;
		}
	}
	@ModelAttribute("check_admin_page")
	public Boolean check() {
		if (req.getUserPrincipal() != null) {
			Accounts acc = (Accounts) session.get("account");
			return !accountsDAO.check2(acc.getUsername()).isEmpty() || !accountsDAO.check3(acc.getUsername()).isEmpty();
		} else {
			return false;
		}

	}
}
