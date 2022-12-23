package com.dtnsbike.controller.users;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.dao.interfaces.AuthoritiesDAO;
import com.dtnsbike.dao.interfaces.RolesDAO;
import com.dtnsbike.dao.service.TypesService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Authorities;
import com.dtnsbike.entity.Types;
import com.dtnsbike.model.RegisterForm;
import com.dtnsbike.service.ConvertDateService;
import com.dtnsbike.service.RandomPassService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
public class AccountsController {

	String path = "users/common/";
	@Autowired
	TypesService typ_service;

	@Autowired
	AccountsDAO accountdao;

	@Autowired
	HttpServletRequest req;

	@Autowired
	HttpServletResponse resp;

	@Autowired
	AuthoritiesDAO authoritiesDAO;

	@Autowired
	RolesDAO rolesDAO;

	@Autowired
	RandomPassService randomPassService;

	@Autowired
	ConvertDateService dateService;
	
	@Autowired
	AccountsDAO accountsDAO;

//	Trang đăng ký 
	@GetMapping("/register.html")
	public String registerPage(@ModelAttribute("registerForm") RegisterForm register) {
		return path + "accounts/register";
	}

	String usernamebig;
	String passwordbig;
	String emailbig;
	String phonebig;
	String firstname;
	String middlename;
	String lastname;
	
	@PostMapping("/register.html")
	public String registerPagePost(Model m, @Valid @ModelAttribute("registerForm") RegisterForm register,
			Errors errors) {
		Optional<Accounts> account = accountdao.findById(register.getUsername());
		String account_phone = accountdao.findPhone(register.getPhone());
		String account_email = accountdao.findEmail(register.getEmail());
		String sdt = register.getPhone();
		String hoten = register.getFullname();

		int loi = 0;
		if (account.isPresent() && !register.getUsername().isEmpty()) {
			m.addAttribute("mess_user", "Tên đăng nhập đã được sử dụng");
			loi++;
		}
		
		Pattern pattern =Pattern.compile("[^A-Za-z0-9]");
		Matcher matcher=pattern.matcher(register.getUsername());
		if (matcher.find()) {
			m.addAttribute("mess_user","Tên đăng nhập không chứa dấu cách hoặc ký tự đặc biệt");
			loi++;
		}
		
		if (account_email != null && !register.getEmail().isEmpty()) {
			m.addAttribute("mess_email", "Email đã được sử dụng");
			loi++;
		}
		if (account_phone != null && !sdt.isEmpty()) {
			m.addAttribute("mess_phone", "Số điện thoại đã được sử dụng");
			loi++;
		}

		String reg = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$";
		if (!sdt.matches(reg) && !sdt.isEmpty()) {
			m.addAttribute("erros_phone", "Số điện thoại không hợp lệ");
			loi++;
		}
		if (!register.getPassword().equals(register.getRepassword())) {
			m.addAttribute("erros_pass", "Mật khẩu không trùng khớp");
			loi++;
		}

		// xu ly ho ten
		hoten = hoten.trim();
		String tenlot = "";
		String ten = "";
		String ho = "";
		if (hoten.lastIndexOf(" ") < 0) {
			ho = null;
		} else {
			ho = hoten.substring(0, hoten.indexOf(" "));
		}

		if (ho == null) {
			m.addAttribute("erros_ten", "Vui lòng nhập đầy đủ họ tên");
			loi++;
		} else {
			tenlot = hoten.substring(hoten.indexOf(" "), hoten.lastIndexOf(" "));
			ten = hoten.substring(hoten.lastIndexOf(" "));
		}

		if (ten == null || tenlot == null) {
			m.addAttribute("erros_ten", "Vui lòng nhập đầy đủ họ tên");
			loi++;
		}
		
		if (register.getPassword()!=null) {
			if (register.getPassword().length() < 8) {
			m.addAttribute("erros_pass", "Mật khẩu ít nhất 8 kí tự!!!");
			loi++;
			}
		}else {
			m.addAttribute("erros_pass", null);
		}

		if (errors.hasErrors()) {
			loi++;
		}
		m.addAttribute("sucess", null);

		if (loi == 0) {
			int code = (int) Math.floor(((Math.random() * 899999) + 100000));
			otp = String.valueOf(code);
			
			// Gửi mật khẩu qua mail
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
			try {
				Multipart multipart = new MimeMultipart();
				MimeBodyPart bodytext = new MimeBodyPart();
				bodytext.setContent(getHTMLT(otp, "Mã OTP của bạn là !"), "text/html; charset=utf-8");
				multipart.addBodyPart(bodytext);

				MimeMessage mess = new MimeMessage(session);
				mess.setFrom(new InternetAddress("phuchtpc01818@fpt.edu.vn"));
				mess.setRecipients(Message.RecipientType.TO, req.getParameter("email"));
				mess.setSubject("PASSWORD RETRIEVAL", "utf-8");
				mess.setReplyTo(mess.getFrom());
				mess.setContent(multipart);
				Transport.send(mess);
				m.addAttribute("mess", "Mã OTP đã được gửi đến email của bạn");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			usernamebig=register.getUsername();
			passwordbig=register.getPassword();
			emailbig=register.getEmail();
			phonebig=register.getPhone();
			firstname=ten;
			middlename=tenlot;
			lastname=ho;
			
			return "redirect:/register_OTP.html";
		}
		return path + "accounts/register";
	}
	
//	Trang đổi mật khẩu 
	@RequestMapping("/change_pass.html")
	public String changePassPage() {
		return path + "accounts/change_pass";
	}

//	Trang quên mật khẩu
	@GetMapping("/forgot_pass.html")
	public String forgotPassPage() {
		return path + "accounts/forgot_pass";
	}

	String otp;
	String mail;

	@PostMapping("/forgot_pass.html")
	public String forgotPassPagePost(Model m, @RequestParam("email") String email) {
		if (accountdao.findEmail(email) != null) {
//            String pass= accountdao.findByEmail(email).get(0).getPassword();

			int code = (int) Math.floor(((Math.random() * 899999) + 100000));
			otp = String.valueOf(code);
			mail = email;

			// Gửi mật khẩu qua mail
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
			try {
				Multipart multipart = new MimeMultipart();
				MimeBodyPart bodytext = new MimeBodyPart();
				bodytext.setContent(getHTMLT(otp, "Mã OTP của bạn là !"), "text/html; charset=utf-8");
				multipart.addBodyPart(bodytext);

				MimeMessage mess = new MimeMessage(session);
				mess.setFrom(new InternetAddress("phuchtpc01818@fpt.edu.vn"));
				mess.setRecipients(Message.RecipientType.TO, req.getParameter("email"));
				mess.setSubject("PASSWORD RETRIEVAL", "utf-8");
				mess.setReplyTo(mess.getFrom());
				mess.setContent(multipart);
				Transport.send(mess);
				m.addAttribute("mess", "Mã OTP đã được gửi đến email của bạn");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "redirect:/otp.html";

		} else {
			m.addAttribute("mess1", "Vui lòng nhập đúng email của bạn !");
		}
		return path + "accounts/forgot_pass";
	}

	public String getHTMLT(String code, String message) {
		String html = "<!DOCTYPE html\r\n"
				+ "    PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n" + "\r\n" + "<head>\r\n"
				+ "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\r\n"
				+ "    <title>DTNsBike</title>\r\n"
				+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\r\n" + "</head>\r\n"
				+ "\r\n" + "<body style=\"margin: 0; padding: 0;\">\r\n"
				+ "    <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"800\" style=\"border: 1px solid #cccccc;\">\r\n"
				+ "        <tr>\r\n" + "            <td align=\"center\"\r\n"
				+ "                style=\"padding: 10px 0 30px 0;background: url('https://lh3.googleusercontent.com/kwFhfKq_9afZ8_tGqSNozfp_DrYyzLikHy9xtC4kiiqylGWv7n_5UJA7yQ3W18xTmw=h500');background-size: cover;\">\r\n"
				+ "                <h3 style=\"color: #ffffff; font-family: Arial, sans-serif; font-size:50px;\">\r\n"
				+ "                    DTNsBike\r\n" + "                </h3>\r\n" + "            </td>\r\n"
				+ "        </tr>\r\n" + "        <tr>\r\n"
				+ "            <td bgcolor=\"#ffffff\" style=\"padding: 40px 30px 40px 30px;\">\r\n"
				+ "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\r\n"
				+ "                    <tr align=\"center\">\r\n"
				+ "                        <td style=\"color: #153643; font-family: Arial, sans-serif; font-size:18px;\">\r\n"
				+ "                            <b> " + message + "</b>\r\n" + "                        </td>\r\n"
				+ "                    </tr>\r\n" + "                    <tr>\r\n"
				+ "                        <td style=\"padding: 20px 0 30px 0;\">\r\n"
				+ "                        </td>\r\n" + "                    </tr>\r\n" + "                    <tr>\r\n"
				+ "                        <td>\r\n"
				+ "                            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\r\n"
				+ "                                <tr>\r\n"
				+ "                                    <td width=\"100%\" valign=\"top\">\r\n"
				+ "                                        <table border=\"1\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\r\n"
				+ "                                            <tr align=\"center\">\r\n"
				+ "                                                <td\r\n"
				+ "                                                    style=\"color: #153643; font-family: Arial, sans-serif; font-size: 30px;\">\r\n"
				+ "                                                    <h1>" + code + "</h1>\r\n"
				+ "                                                </td>\r\n"
				+ "                                            </tr>\r\n"
				+ "                                        </table>\r\n"
				+ "                                    </td>\r\n" + "                                </tr>\r\n"
				+ "                            </table>\r\n" + "                        </td>\r\n"
				+ "                    </tr>\r\n" + "                    <tr align=\"center\">\r\n"
				+ "                        <td style=\"padding: 40px 0 30px 0;\">\r\n"
				+ "                            <a href=\"http://localhost:8080/DTNsBike/login.html"
				+ "\" style=\"background-color: rgb(241, 75, 75);color:white;\r\n"
				+ "                                text-decoration: none;padding: 15px 50px 15px 50px;border-radius: 30px;\r\n"
				+ "                                box-shadow: rgba(0, 0, 0, 0.4) 0px 2px 4px, rgba(0, 0, 0, 0.3) 0px 7px 13px -3px, rgba(0, 0, 0, 0.2) 0px -3px 0px inset;                                \r\n"
				+ "                                \" class=\"dtn\">\r\n"
				+ "                                Đăng nhập ngay\r\n" + "                            </a>\r\n"
				+ "                        </td>\r\n" + "                    </tr>\r\n" + "                </table>\r\n"
				+ "            </td>\r\n" + "        </tr>\r\n" + "        <tr>\r\n"
				+ "            <td bgcolor=\"crimson\" style=\"padding: 30px 30px 30px 30px;\">\r\n"
				+ "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\r\n"
				+ "                    <tr>\r\n"
				+ "                        <td style=\"color: #ffffff; font-family: Arial, sans-serif; font-size: 14px;\">\r\n"
				+ "                            &reg; DTNsShop, @LinhButoka 2022<br />\r\n"
				+ "                            <a href=\"http://localhost:8080/DTNsBike/register.html\" style=\"color: #ffffff;\">\r\n"
				+ "                                <font color=\"#ffffff\">Đăng ký</font>\r\n"
				+ "                            </a> để nhận nhiều ưu đãi hấp dẫn từ DTNsBike. :))\r\n"
				+ "                        </td>\r\n" + "                        <td align=\"right\">\r\n" + "\r\n"
				+ "                        </td>\r\n" + "                    </tr>\r\n" + "                </table>\r\n"
				+ "            </td>\r\n" + "        </tr>\r\n" + "    </table>\r\n" + "</body>\r\n" + "\r\n"
				+ "</html>";
		return html;
	}

//	Trang gửi mã xác nhận OTP
	@GetMapping("/otp.html")
	public String otpPage() {
		return path + "accounts/otp";
	}

	@PostMapping("/otp.html")
	public String otpPost(Model m, @RequestParam("OTP") String ma) {
		String newpassword = randomPassService.randomPassword(8);
		int loi = 0;
		if (otp == null) {
			m.addAttribute("tbotp", "Không tìm thấy mã OTP, vui lòng yêu cầu gửi lại mã");
			loi++;
		}
		if (ma == null) {
			m.addAttribute("tbotp", "Vui lòng nhập mã OTP");
			loi++;
		}
		if (!otp.equals(ma)) {
			m.addAttribute("tbotp", "Mã OTP không đúng");
			loi++;
		}
		if (loi == 0) {
			Accounts account = accountdao.findByEmails(mail);
			account.setPassword(newpassword);
			accountdao.save(account);
			m.addAttribute("tbmk", "Mật khẩu mới của bạn là: " + newpassword);
		}

		return path + "accounts/otp";
	}
	
	@GetMapping("/register_OTP.html")
	public String xacnhandangky(Model m) {	
		return path+ "accounts/register_OTP";
	}
	
	@PostMapping("/register_OTP.html")
	public String xacnhandangkypost(Model m) {
		String first=req.getParameter("first");
		String second=req.getParameter("second");
		String third=req.getParameter("third");
		String fourth=req.getParameter("fourth");
		String fifth=req.getParameter("fifth");
		String sixth=req.getParameter("sixth");
		String examcode=first+second+third+fourth+fifth+sixth;
		
		int loi=0;
		
		if (otp==null) {
			m.addAttribute("tbotp", "Không tìm thấy mã OTP, vui lòng yêu cầu gửi lại mã");
			loi++;			
		}		
		if (otp!=null) {
			if (!otp.equals(examcode)) {
				m.addAttribute("tbotp","Mã OTP không đúng");
				loi++;
			}
		}		
		if (examcode.length()<6) {
			m.addAttribute("tbotp", "Vui lòng nhập đầy đủ mã OTP");
			loi++;
		}
		
		if (loi==0) {
			Accounts account=new Accounts();
			account.setUsername(usernamebig);
			account.setPassword(passwordbig);
			account.setEmail(emailbig);
			account.setPhone(phonebig);
			account.setFirstname(firstname);
			account.setMiddlename(middlename);
			account.setLastname(lastname);
			account.setPhoto("default.jpg");
			account.setBirthday(dateService.stringToDate("yyyy-MM-dd", "1990-01-01"));
			account.setGender("3");
			account.setActive(true);
			accountdao.save(account);
			m.addAttribute("emaildk",emailbig);
			
			Authorities authorities = new Authorities();
			authorities.setUsername(account);
			authorities.setRoleId(rolesDAO.getById("user"));
			authorities.setActive(true);
			authoritiesDAO.save(authorities);			
			
			m.addAttribute("dk", "Đăng ký thành công");
			
			return "redirect:/login.html";
		}
		return path+ "accounts/register_OTP";
	}

	@ModelAttribute("types")
	List<Types> findAllType() {
		return typ_service.findAllInProduct();
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
			Accounts acc = (Accounts) accountsDAO.findById(req.getUserPrincipal().getName()).get();
			return !accountsDAO.check2(acc.getUsername()).isEmpty() || !accountsDAO.check3(acc.getUsername()).isEmpty();
		} else {
			return false;
		}

	}
}
