package com.dtnsbike.controller.admin;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.dao.service.AccountsService;
import com.dtnsbike.entity.*;
import com.dtnsbike.model.*;
import com.dtnsbike.service.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;

@Controller
@RequestMapping("/admin")
public class Accounts_Admin_Controller {

	private String path = "admin/common/accounts/";

	@Autowired
	AccountsService accountsService;

	@Autowired
	GetContentJsonService contentJson;

	@Autowired
	RestApiService api;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	ConvertPageService pageService;

	@Autowired
	SessionService session;

	@Autowired
	HttpServletRequest request;

	@Autowired
	ConvertDateService dateService;

	@Autowired
	FileManagerService fileService;

	@Autowired
	AccountsDAO accountDao;

//	Danh sách tài khoản
	@RequestMapping("/account-list.html")
	public String accountList(Model m, HttpServletRequest request, @RequestParam("page") Optional<String> pages)
			throws JsonParseException, JsonMappingException, IOException {

		String sizes = request.getParameter("size");

		if (sizes == null) {
			sizes = "5";
		}
		if (!NumberUtils.isParsable(sizes)) {
			sizes = "5";
		}

		List<Integer> arr = new ArrayList<Integer>(4);
		arr.add(5);
		arr.add(10);
		arr.add(15);
		arr.add(20);
		Integer size = Integer.valueOf(sizes);
		Integer page = 1;
		if (pages.isPresent()) {
			if (NumberUtils.isParsable(String.valueOf(pages.get()))) {
				page = Integer.valueOf(pages.get());
				if (page < 1) {
					page = 1;
				}
			}
		}
		if (size < 5 || size > 20 || !arr.contains(size)) {
			size = 5;
		}
		TypeReference<List<Accounts>> typeAcc = new TypeReference<List<Accounts>>() {
		};
		List<Accounts> listAcc = mapper.readValue(api.get("/DTNsBike/rest/accounts").toString(), typeAcc);
		if (pageService.checkTotalPages(listAcc, page, size) == false) {
			page = 1;
		}
		Pageable pageable = PageRequest.of((page - 1), size);
		@SuppressWarnings("unchecked")
		List<Accounts> accounts = (List<Accounts>) pageService.toPage(listAcc, pageable).getContent();
		m.addAttribute("listAcc", accounts);
		mapper = new ObjectMapper();
		TypeReference<List<Authorities>> typeAuth = new TypeReference<List<Authorities>>() {
		};
		List<Authorities> listAuth = mapper.readValue(api.get("/DTNsBike/rest/auths").toString(), typeAuth);
		List<Authorities> result = listAuth.stream().filter(x -> x.getActive() == true).collect(Collectors.toList());
		m.addAttribute("listAuth", result);
		List<String> listAuthUser = new ArrayList<>();
		for (int i = 0; i < result.size(); i++) {
			listAuthUser.add(result.get(i).getUsername().getUsername());
		}
		Integer totalPage = listAcc.size() / size;
		if (listAcc.size() % size > 0) {
			totalPage = totalPage + 1;
		}
		m.addAttribute("listAuthUser", listAuthUser);
		m.addAttribute("listPage", pageService.listPage(listAcc, page, size));
		m.addAttribute("page", page);
		m.addAttribute("totalPage", totalPage.intValue());
		m.addAttribute("pageableItems", pageable);
		m.addAttribute("totalItems", listAcc.size());
		m.addAttribute("size", size);
		return path + "account-list.html";
	}

	@RequestMapping("/account/account-update.html")
	public String updateAcc(Model m, @ModelAttribute("accountModel") AccountAdminModel accountModel)
			throws JsonMappingException, JsonProcessingException, IOException {
		if (Boolean.valueOf(request.getParameter("load")) == true) {
			return "redirect:/admin/account/form/add";
		}
		session.set("editURI", "/admin/account/account-update.html");
		m.addAttribute("imgs", session.get("imgs"));
		if (session.get("editUserID") != null) {
			session.set("editURI", "/admin/account/account-update.html");
			m.addAttribute("editUserID", session.get("editUserID"));
			if (session.get("accountModel") != null) {
				accountModel = session.get("accountModel");
				m.addAttribute("accountModel", accountModel);
			}
		} else {
			if (session.get("accountModels") != null) {
				accountModel = session.get("accountModels");
				m.addAttribute("accountModel", accountModel);
			} else {
				accountModel = new AccountAdminModel();
			}
			m.addAttribute("imgs", session.get("imgs"));
		}
		TypeReference<List<Roles>> typeRole = new TypeReference<List<Roles>>() {
		};
		List<Roles> listRoles = mapper.readValue(api.get("/DTNsBike/rest/roles").toString(), typeRole);
		m.addAttribute("listRoles", listRoles);
		session.set("uriInsert", true);
		return path + "account-update.html";
	}

	@ModelAttribute("accountProfile")
	public Accounts getListSizes() throws JsonMappingException, JsonProcessingException, IOException {
		return accountDao.findById(request.getUserPrincipal().getName()).get();
	}
	@ModelAttribute("check_role")
	Boolean checkRole() {
		Accounts acc = accountDao.findById(request.getUserPrincipal().getName()).get();
		return accountDao.check(acc.getUsername()) != null;
	}

}
