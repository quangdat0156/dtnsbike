package com.dtnsbike.controller.users;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.dao.service.BlogsService;
import com.dtnsbike.dao.service.FavoritesService;
import com.dtnsbike.dao.service.TypesService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Blogs;
import com.dtnsbike.entity.Types;
import com.dtnsbike.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
public class BLogsController {

	String path = "users/common/";
	@Autowired
	TypesService typ_service;
	@Autowired
	BlogsService blog_service;
	@Autowired
	FavoritesService fav_service;
	@Autowired
	SessionService session;
	@Autowired
	AccountsDAO accountsDAO;
	@Autowired
	HttpServletRequest req;

//	Trang Tin tức 
	@RequestMapping("/blogs.html")
	public String blogPage(Model m) {
		m.addAttribute("hhh", true);

		m.addAttribute("blogs", blog_service.findTop(PageRequest.of(0, 4)));
		m.addAttribute("blogss", blog_service.findTop2(PageRequest.of(0, 1)));
		m.addAttribute("blogsss", blog_service.findTop2(PageRequest.of(0, 2)));
		m.addAttribute("blogs_all", blog_service.findAll());

		return path + "blogs/blogs";
	}

//	Trang tin tức chi tiết :))
	@RequestMapping("/blog_details.html")
	public String blogDetailPage(Model m, @RequestParam("id") Integer id) {
		m.addAttribute("hhh", true);

		m.addAttribute("blog", blog_service.findById(id).get());
		m.addAttribute("blogs", blog_service.findAll());
		m.addAttribute("blogss", blog_service.findTop2(PageRequest.of(0, 4)));

		Accounts acc = session.get("account");
		if (acc != null) {
			Blogs blog = blog_service.findById(id).get();
			blog.setViews(blog.getViews() + 1);
			blog_service.update(blog);
		}

		return path + "blogs/blog_details";
	}

	@ModelAttribute("types")
	List<Types> findAllType() {
		return typ_service.findAllInProduct();
	}

	@ModelAttribute("favcount")
	Integer favcount() {
		Accounts ac = (Accounts) session.get("account");
		return fav_service.findAllUser(ac).size();
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
