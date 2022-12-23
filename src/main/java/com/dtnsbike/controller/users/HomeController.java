package com.dtnsbike.controller.users;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.dao.service.BlogsService;
import com.dtnsbike.dao.service.BrandsService;
import com.dtnsbike.dao.service.CategoryService;
import com.dtnsbike.dao.service.FavoritesService;
import com.dtnsbike.dao.service.ProductsService;
import com.dtnsbike.dao.service.TypesService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Brands;
import com.dtnsbike.entity.Types;
import com.dtnsbike.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
public class HomeController {

	@Autowired
	AccountsDAO accountsDAO;

	@Autowired
	ProductsService pro_service;
	@Autowired
	BrandsService bra_service;
	@Autowired
	TypesService typ_service;
	@Autowired
	FavoritesService fav_service;
	@Autowired
	BlogsService blog_service;
	@Autowired
	CategoryService cate_service;

	@Autowired
	HttpServletRequest req;

	@Autowired
	SessionService session;

	String path = "users/common/";

//	Index Home Page aka Trang chủ
	@RequestMapping("/index.html")
	public String index(Model m) {
		Accounts ac = (Accounts) session.get("account");
		m.addAttribute("hhh", false);
		m.addAttribute("blogs", blog_service.findTop(PageRequest.of(0, 3)));
		m.addAttribute("checkk", typ_service);
		m.addAttribute("cate", cate_service.findAll2());
		m.addAttribute("products", pro_service.findTop(PageRequest.of(0, 4)));
		m.addAttribute("sales", pro_service.findSale(PageRequest.of(0, 4)));

		session.set("uri", req.getRequestURI());
		session.remove("keywords");

		if (ac != null) {
			m.addAttribute("check", fav_service);
		} else {
			m.addAttribute("check", null);
		}

		return "users/index";
	}

//	Trang dịch vụ
	@RequestMapping("/services.html")
	public String servicesPage() {
		return path + "home/services";
	}

//	Trang giới thiệu
	@RequestMapping("/about.html")
	public String aboutPage() {
		return path + "home/about";
	}

//	Trang liên hệ góp ý 
	@GetMapping("/contact.html")
	public String contactPage() {
		return path + "home/contact";
	}

	@PostMapping("/contact.html")
	public String contactPagePost(Model m) {

//		String name = req.getParameter("name");
		String email = req.getParameter("email");
		String subject = req.getParameter("subject");
		String message = req.getParameter("message");

		Properties pros = new Properties();
		pros.setProperty("mail.smtp.auth", "true");
		pros.setProperty("mail.smtp.starttls.enable", "true");
		pros.setProperty("mail.smtp.host", "smtp.gmail.com");
		pros.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com");
		pros.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
		pros.setProperty("mail.smtp.port", "587");
		// Kết nối

		Session session = Session.getInstance(pros, new Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				String username = "dtnsoftware2022@gmail.com";
				String password = "yxcorwgpixpkrkyd";
				return new javax.mail.PasswordAuthentication(username, password);
			}
		});

		MimeMessage msg = new MimeMessage(session);
		// set message headers
		try {
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");
			msg.setFrom(new InternetAddress(email));
			msg.setReplyTo(msg.getFrom());
			msg.setSubject(subject, "utf-8");
			msg.setText(message, "utf-8");
			msg.setRecipients(Message.RecipientType.TO,
					new InternetAddress[] { new InternetAddress("dtnsoftware2022@gmail.com") });
			Transport.send(msg);
			m.addAttribute("mess", "Đã gửi");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path + "home/contact";
	}

	@ModelAttribute("types")
	List<Types> findAllType() {
		return typ_service.findAllInProduct();
	}

	@ModelAttribute("brands")
	List<Brands> findAllBrand() {
		return bra_service.findAll();
	}

	@ModelAttribute("favcount")
	Integer favcount() {
		Accounts ac = session.get("account");
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
