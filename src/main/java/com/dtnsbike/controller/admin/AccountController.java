package com.dtnsbike.controller.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.servlet.http.*;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.dao.service.AccountsService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Authorities;
import com.dtnsbike.entity.Roles;
import com.dtnsbike.model.AccountAdminModel;
import com.dtnsbike.model.UserResetpassModel;
import com.dtnsbike.service.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

@Controller
@RequestMapping("/admin")
public class AccountController {

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
	FieldErrorService fieldErrorService;

	@Autowired
	RandomPassService randomPassService;

	@Autowired
	MailerService mailerService;

	@Autowired
	AccountsDAO accountDao;

	@PostMapping("/account/form/update/{id}")
	public String updateFormAccount(Model m, @Valid @ModelAttribute("accountModel") AccountAdminModel accountModel,
			BindingResult rs, @PathVariable("id") Optional<String> username, Errors er)
			throws JsonMappingException, JsonProcessingException, IOException {
		session.set("editURI", "/admin/account/form/edit/" + username.get());
		Optional<String> path = Optional.of(session.get("editUserID"));
		if (path == null) {
			return "redirect:/admin/account-list.html";
		}

		// Khai báo thông báo lỗi update
		String errEmailModel = new String();
		String errPhoneModel = new String();
		String errBirthdayModel = new String();
		String errActiveModel = new String();

		// Kiểm tra username trên uri
		if (!username.isPresent()) {
			return "redirect:/admin/account/form/edit/" + path.get();
		} else {
			if (!username.get().equalsIgnoreCase(path.get())) {
				return "redirect:/admin/account-list.html";
			}
		}

		// Kiểm tra lỗi form nhập
		Integer err = 0;
		Accounts accounts = new Accounts();
		TypeReference<Accounts> typeAcc = new TypeReference<Accounts>() {
		};
		String pathAcc = "/DTNsBike/rest/accounts/" + username.get();

		if (api.get(pathAcc) == null) {

			return "redirect:/admin/account/form/edit/" + path.get();
		} else {
			accounts = mapper.readValue(api.get(pathAcc).toString(), typeAcc);
		}

		// Kiểm tra username

		// Kiểm tra firstname;
		if (accountModel.getFirstname() == null) {
		} else {
			accounts.setFirstname(accountModel.getFirstname());
		}

		// Kiểm tra middlename;
		if (accountModel.getMiddlename() == null) {
			accounts.setMiddlename("");
		} else {
			accounts.setMiddlename(accountModel.getMiddlename());
		}

		// Kiểm tra lastname;
		accounts.setLastname(accountModel.getLastname());

		// Kiểm tra email
		if (fieldErrorService.checkCodeFieldErrors("email", "NotBlank", rs)) {
			errEmailModel = "*Vui lòng nhập email!";
			m.addAttribute("errEmailModel", errEmailModel);
			err++;
		} else {
			if (fieldErrorService.checkCodeFieldErrors("email", "Email", rs)) {
				errEmailModel = "*Email không hợp lệ!";
				m.addAttribute("errEmailModel", errEmailModel);
				err++;
			} else {
				if (fieldErrorService.checkCodeFieldErrors("email", "DuplicateInsertGmailUsers", rs)) {
					if (!accountModel.getEmail().trim().equalsIgnoreCase(accounts.getEmail().trim())) {
						errEmailModel = "*Email đã được sử dụng!";
						m.addAttribute("errEmailModel", errEmailModel);
						err++;
					}
				} else {
					accounts.setEmail(accountModel.getEmail().trim());
				}
			}
		}

		// Kiểm tra phone number
		if (accountModel.getPhone() != null) {
			if (fieldErrorService.checkCodeFieldErrors("phone", "Pattern", rs)) {
				errPhoneModel = "*Số điện thoại không hợp lệ!";
				m.addAttribute("errPhoneModel", errPhoneModel);
				err++;
			} else {
				if (fieldErrorService.checkCodeFieldErrors("phone", "DuplicatePhone", rs)) {
					if (!accountModel.getPhone().trim().equalsIgnoreCase(accounts.getPhone().trim())) {
						errPhoneModel = "*Số điện thoại đã được sử dụng!";
						m.addAttribute("errPhoneModel", errPhoneModel);
						err++;
					}
				} else {
					accounts.setPhone(accountModel.getPhone().trim());
				}
			}
		}

		// Kiểm tra giới tính
		if (accountModel.getGender() != null) {
			if (!accountModel.getGender().equalsIgnoreCase("1") && !accountModel.getGender().equalsIgnoreCase("2")
					&& !accountModel.getGender().equalsIgnoreCase("3")) {
				accounts.setGender("3");
			} else {
				accounts.setGender(accountModel.getGender());
			}
		} else {
			accounts.setGender("3");
		}

		// Kiểm tra ngày sinh
		if (accountModel.getBirthday() != null) {
			Date birthday = dateService.stringToDate("yyyy-MM-dd", accountModel.getBirthday());
			if (birthday == null) {
				errBirthdayModel = "*Ngày sinh không hợp lệ!";
				m.addAttribute("errBirthdayModel", errBirthdayModel);
				err++;
			} else {
				accounts.setBirthday(birthday);
			}
		}

		// Bỏ qua khi đang chỉnh sửa thông tin cho chính tài khoản đăng nhập
		if (request.getUserPrincipal().getName().equalsIgnoreCase(accounts.getUsername())) {
			// Get role
			TypeReference<List<Authorities>> typeRole = new TypeReference<List<Authorities>>() {
			};
			String pathAuth = "/DTNsBike/rest/auths/user/" + username.get();
			List<Authorities> listRole = mapper.readValue(api.get(pathAuth).toString(), typeRole);
			// Set role
			List<String> role = new ArrayList<>();
			for (int i = 0; i < listRole.size(); i++) {
				role.add(listRole.get(i).getRoleId().getId());
			}
			accountModel.setActive(accounts.getActive());
			accountModel.setUsername(username.get());
			accountModel.setRole(role);
		} else {
			if (fieldErrorService.checkCodeFieldErrors("active", "AssertTrue", rs) == false
					&& fieldErrorService.checkCodeFieldErrors("active", "AssertFalse", rs) == false) {
				errActiveModel = "*Vui lòng chọn trạng thái!";
				m.addAttribute("errActiveModel", errActiveModel);
				err++;
			} else {
				accounts.setActive(accountModel.getActive());
			}
		}
		// Set ảnh
		if (!accounts.getPhoto().equals(session.get("imgs"))) {
			accounts.setPhoto(session.get("imgs"));
		}
		// Tiến hành lệnh cập nhật
		if (err == 0) {
			String pathUpdate = "/DTNsBike/rest/accounts/" + username.get();
			api.push(pathUpdate, accounts);
			TypeReference<Roles> roleType = new TypeReference<Roles>() {
			};
			TypeReference<List<Authorities>> typeRole = new TypeReference<List<Authorities>>() {
			};
			String pathAuth = "/DTNsBike/rest/auths/user/" + username.get();
			List<Authorities> listAuth = mapper.readValue(api.get(pathAuth).toString(), typeRole);
			Authorities auth = new Authorities();
			Roles role = new Roles();
			List<String> listRole = new ArrayList<>();
			// Check role admin
			if (accountModel.getRole().contains("admin")) {
				int check = 0;
				Integer authId = null;
				for (int i = 0; i < listAuth.size(); i++) {
					if (listAuth.get(i).getRoleId().getId().equalsIgnoreCase("admin")) {
						authId = listAuth.get(i).getAuthoritiesid();
						check++;
						break;
					}
				}
				if (check != 0) {
					auth = new Authorities();
					auth.setAuthoritiesid(authId);
					role = mapper.readValue(api.get("/DTNsBike/rest/roles/admin").toString(), roleType);
					auth.setRoleId(role);
					auth.setActive(true);
					auth.setUsername(accounts);
					String pathPutAuth = "/DTNsBike/rest/auths/" + authId;
					listRole.add("admin");
					// put
					api.push(pathPutAuth, auth);
				} else {
					auth = new Authorities();
					role = mapper.readValue(api.get("/DTNsBike/rest/roles/admin").toString(), roleType);
					auth.setRoleId(role);
					auth.setActive(true);
					auth.setUsername(accounts);
					String pathPutAuth = "/DTNsBike/rest/auths";
					listRole.add("admin");
					// post
					api.post(pathPutAuth, auth);
				}
			} else {
				int check = 0;
				Integer authId = null;
				for (int i = 0; i < listAuth.size(); i++) {
					if (listAuth.get(i).getRoleId().getId().equalsIgnoreCase("admin")) {
						authId = listAuth.get(i).getAuthoritiesid();
						check++;
						break;
					}
				}
				if (check != 0) {
					auth = new Authorities();
					auth.setAuthoritiesid(authId);
					role = mapper.readValue(api.get("/DTNsBike/rest/roles/admin").toString(), roleType);
					auth.setRoleId(role);
					auth.setActive(false);
					auth.setUsername(accounts);
					String pathPutAuth = "/DTNsBike/rest/auths/" + authId;
					api.push(pathPutAuth, auth);
					listRole.add("admin");
					// put
				}
			}

			// Check role staff
			if (accountModel.getRole().contains("staff")) {
				int check = 0;
				Integer authId = null;
				for (int i = 0; i < listAuth.size(); i++) {
					if (listAuth.get(i).getRoleId().getId().equalsIgnoreCase("staff")) {
						authId = listAuth.get(i).getAuthoritiesid();
						check++;
						break;
					}
				}
				if (check != 0) {
					auth = new Authorities();
					auth.setAuthoritiesid(authId);
					role = mapper.readValue(api.get("/DTNsBike/rest/roles/staff").toString(), roleType);
					auth.setRoleId(role);
					auth.setActive(true);
					auth.setUsername(accounts);
					String pathPutAuth = "/DTNsBike/rest/auths/" + authId;
					listRole.add("staff");
					// put
					api.push(pathPutAuth, auth);
				} else {
					auth = new Authorities();
					role = mapper.readValue(api.get("/DTNsBike/rest/roles/staff").toString(), roleType);
					auth.setRoleId(role);
					auth.setActive(true);
					auth.setUsername(accounts);
					String pathPutAuth = "/DTNsBike/rest/auths";
					listRole.add("staff");
					// post
					api.post(pathPutAuth, auth);
				}
			} else {
				int check = 0;
				Integer authId = null;
				for (int i = 0; i < listAuth.size(); i++) {
					if (listAuth.get(i).getRoleId().getId().equalsIgnoreCase("staff")) {
						authId = listAuth.get(i).getAuthoritiesid();
						check++;
						break;
					}
				}
				if (check != 0) {
					auth = new Authorities();
					auth.setAuthoritiesid(authId);
					role = mapper.readValue(api.get("/DTNsBike/rest/roles/staff").toString(), roleType);
					auth.setRoleId(role);
					auth.setActive(false);
					auth.setUsername(accounts);
					String pathPutAuth = "/DTNsBike/rest/auths/" + authId;
					api.push(pathPutAuth, auth);
					listRole.add("staff");
					// put
				}
			}

			// Check role user
			if (accountModel.getRole().contains("user")) {
				int check = 0;
				Integer authId = null;
				for (int i = 0; i < listAuth.size(); i++) {
					if (listAuth.get(i).getRoleId().getId().equalsIgnoreCase("user")) {
						authId = listAuth.get(i).getAuthoritiesid();
						check++;
						break;
					}
				}
				if (check != 0) {
					auth = new Authorities();
					auth.setAuthoritiesid(authId);
					role = mapper.readValue(api.get("/DTNsBike/rest/roles/user").toString(), roleType);
					auth.setRoleId(role);
					auth.setActive(true);
					auth.setUsername(accounts);
					String pathPutAuth = "/DTNsBike/rest/auths/" + authId;
					listRole.add("user");
					// put
					api.push(pathPutAuth, auth);
				} else {
					auth = new Authorities();
					role = mapper.readValue(api.get("/DTNsBike/rest/roles/user").toString(), roleType);
					auth.setRoleId(role);
					auth.setActive(true);
					auth.setUsername(accounts);
					String pathPutAuth = "/DTNsBike/rest/auths";
					listRole.add("user");
					// post
					api.post(pathPutAuth, auth);
				}
			} else {
				int check = 0;
				Integer authId = null;
				for (int i = 0; i < listAuth.size(); i++) {
					if (listAuth.get(i).getRoleId().getId().equalsIgnoreCase("user")) {
						authId = listAuth.get(i).getAuthoritiesid();
						check++;
						break;
					}
				}
				if (check != 0) {
					auth = new Authorities();
					auth.setAuthoritiesid(authId);
					role = mapper.readValue(api.get("/DTNsBike/rest/roles/user").toString(), roleType);
					auth.setRoleId(role);
					auth.setActive(false);
					auth.setUsername(accounts);
					String pathPutAuth = "/DTNsBike/rest/auths/" + authId;
					api.push(pathPutAuth, auth);
					listRole.add("user");
					// put
				}
			}
			m.addAttribute("message", "Cập Nhật Thành Công!");
			m.addAttribute("imgs", session.get("imgs"));
			m.addAttribute("editUserID", username.get());
		}
		m.addAttribute("imgs", session.get("imgs"));
		m.addAttribute("editUserID", username.get());
		TypeReference<List<Roles>> typeRole = new TypeReference<List<Roles>>() {
		};
		List<Roles> listRoles = mapper.readValue(api.get("/DTNsBike/rest/roles").toString(), typeRole);
		m.addAttribute("listRoles", listRoles);
		return "admin/common/accounts/account-update.html";

	}

	@GetMapping("/account/form/update/{id}")
	public String updateGetFormAccount(Model m, @ModelAttribute("accountModel") AccountAdminModel accountModel,
			@PathVariable("id") Optional<String> username, Errors er)
			throws JsonMappingException, JsonProcessingException, IOException {
		session.set("editURI", "/admin/account/form/edit/" + username.get());
		return "forward:/admin/account/form/edit/" + username.get();

	}

	@GetMapping("/account/form/add")
	public String addGetFormAccount(Model m, @ModelAttribute("accountModel") AccountAdminModel accountModel,
			@PathVariable("id") Optional<String> username, Errors er)
			throws JsonMappingException, JsonProcessingException, IOException {
		return "forward:/admin/account/account-update.html";
	}

	@PostMapping("/account/form/add")
	public String addFormAccount(Model m, @Valid @ModelAttribute("accountModel") AccountAdminModel accountModel,
			BindingResult rs) throws JsonMappingException, JsonProcessingException, IOException {
		AccountAdminModel accountModels = session.get("accountModels");

		if (session.get("accountModels") != null) {
			accountModel = accountModels;
			m.addAttribute("accountModel", accountModels);
			session.remove("accountModels");
		}
		if (session.get("imgs") != null) {
			m.addAttribute("imgs", session.get("imgs"));
		}

		TypeReference<List<Roles>> typeRole = new TypeReference<List<Roles>>() {
		};
		List<Roles> listRoles = mapper.readValue(api.get("/DTNsBike/rest/roles").toString(), typeRole);
		// Kiểm tra lỗi form nhập
		Integer err = 0;
		Accounts accounts = new Accounts();

		// Kiểm tra uesrname
		if (fieldErrorService.checkCodeFieldErrors("username", "NotBlank", rs)) {
			m.addAttribute("errUsernameModel", "*Vui lòng nhập tên đăng nhập!");
			err++;
		} else {
			if (fieldErrorService.checkCodeFieldErrors("username", "DuplicateUsername", rs)) {
				m.addAttribute("errUsernameModel", "*Tên đăng nhập đã được sử dụng!");
				err++;
			} else {
				Pattern pattern = Pattern.compile("[^A-Za-z0-9]");
				Matcher matcer = pattern.matcher(accountModel.getUsername());
				if (!matcer.find()) {
					accounts.setUsername(accountModel.getUsername());
				} else {
					m.addAttribute("errUsernameModel", "*Vui lòng không nhập dấu cách & kí tự đặc biệt!");
					err++;
				}
			}
		}

		// Kiểm tra firstname
		if (fieldErrorService.checkCodeFieldErrors("firstname", "NotBlank", rs)) {
			m.addAttribute("errFirstnameModel", "*Vui lòng nhập tên của bạn!");
			err++;
		} else {
			accounts.setFirstname(accountModel.getFirstname().trim());
		}

		// Kiểm tra lastname
		if (fieldErrorService.checkCodeFieldErrors("lastname", "NotBlank", rs)) {
			m.addAttribute("errLastnameModel", "*Vui lòng nhập họ của bạn!");
			err++;
		} else {
			accounts.setLastname(accountModel.getLastname().trim());
		}

		// Kiểm tra middlename
		accounts.setMiddlename(accountModel.getMiddlename().trim());

		// Kiểm tra email
		if (fieldErrorService.checkCodeFieldErrors("email", "NotBlank", rs)) {
			m.addAttribute("errEmailModel", "*Vui lòng nhập email!");
			err++;
		} else {
			if (fieldErrorService.checkCodeFieldErrors("email", "Email", rs)) {
				m.addAttribute("errEmailModel", "*Email không hợp lệ!");
				err++;
			} else {
				if (fieldErrorService.checkCodeFieldErrors("email", "DuplicateInsertGmailUsers", rs)) {
					m.addAttribute("errEmailModel", "*Email đã được sử dụng!");
					err++;
				} else {
					accounts.setEmail(accountModel.getEmail());
				}
			}
		}

		// Kiểm tra ngày sinh
		if (fieldErrorService.checkCodeFieldErrors("birthday", "NotBlank", rs)) {
			m.addAttribute("errBirthdayModel", "*Vui chọn ngày sinh!");
			err++;
		} else {
			Date birthDate = dateService.stringToDate("yyyy-MM-dd", accountModel.getBirthday());
			if (birthDate == null) {
				m.addAttribute("errBirthdayModel", "*Ngày sinh không hợp lệ!");
				err++;
			} else {
				accounts.setBirthday(birthDate);
			}
		}

		// Kiểm tra số điện thoại
		if (fieldErrorService.checkCodeFieldErrors("phone", "NotBlank", rs)) {
			m.addAttribute("errPhoneModel", "*Vui lòng nhập số điện thoại!");
			err++;
		} else {
			if (fieldErrorService.checkCodeFieldErrors("phone", "Pattern", rs)) {
				m.addAttribute("errPhoneModel", "*Số điện thoại chưa đúng định dạng!");
				err++;
			} else {
				if (fieldErrorService.checkCodeFieldErrors("phone", "DuplicatePhone", rs)) {
					m.addAttribute("errPhoneModel", "*Số điện thoại đã được sử dụng!");
					err++;
				} else {
					accounts.setPhone(accountModel.getPhone().trim());
				}
			}
		}

		// Kiểm tra giới tính
		if (fieldErrorService.checkCodeFieldErrors("gender", "NotBlank", rs)) {
			m.addAttribute("errGenderModel", "*Vui lòng chọn giới tính!");
			err++;
		} else {
			if (!accountModel.getGender().equalsIgnoreCase("1") && !accountModel.getGender().equalsIgnoreCase("2")
					&& !accountModel.getGender().equalsIgnoreCase("3")) {
				m.addAttribute("errGenderModel", "*Vui lòng chọn giới tính!");
				err++;
			} else {
				accounts.setGender(accountModel.getGender().trim());
			}
		}

		// Kiểm tra phân quyền
		if (fieldErrorService.checkCodeFieldErrors("role", "NotEmpty", rs)) {
			m.addAttribute("errRoleModel", "*Vui lòng cấp ít nhất 1 quyền cho tài khoản!");
			err++;
		}

		// Kiểm tra trạng thái
		if (fieldErrorService.checkCodeFieldErrors("active", "AssertTrue", rs) == false
				&& fieldErrorService.checkCodeFieldErrors("active", "AssertFalse", rs) == false) {
			m.addAttribute("errActiveModel", "*Vui lòng chọn trạng thái!");
			err++;
		} else {
			accounts.setActive(accountModel.getActive());
		}
		Accounts accountsNew = new Accounts();
		if (err == 0) {
			String filename = session.get("imgs");
			if (filename != null) {
				accounts.setPhoto(filename);
			} else {
				accounts.setPhoto("default.jpg");
			}
			accounts.setPassword(randomPassService.randomPassword(16));
			api.post("/DTNsBike/rest/accounts", accounts);

			TypeReference<Accounts> typeAcc = new TypeReference<Accounts>() {
			};
			String pathAcc = "/DTNsBike/rest/accounts/" + accounts.getUsername();
			accountsNew = mapper.readValue(api.get(pathAcc).toString(), typeAcc);

			if (accountsNew != null) {
				String body = "Mật khẩu đăng nhập vào website DTNsBike là: " + accountsNew.getPassword();
				try {
					mailerService.send(accountsNew.getEmail(), "Thông Tin Tài Khoản DTNsBike", body);
				} catch (MessagingException e) {
					System.out.println("Không gửi được gmail!");
				}

				// Check role admin
				TypeReference<List<Authorities>> typeRoles = new TypeReference<List<Authorities>>() {
				};
				TypeReference<Roles> roleType = new TypeReference<Roles>() {
				};
				String pathAuth = "/DTNsBike/rest/auths/user/" + accountsNew.getUsername();
				List<Authorities> listAuth = mapper.readValue(api.get(pathAuth).toString(), typeRoles);
				Authorities auth = new Authorities();
				Roles role = new Roles();
				List<String> listRole = new ArrayList<>();
				if (accountModel.getRole().contains("admin")) {
					int check = 0;
					Integer authId = null;
					for (int i = 0; i < listAuth.size(); i++) {
						if (listAuth.get(i).getRoleId().getId().equalsIgnoreCase("admin")) {
							authId = listAuth.get(i).getAuthoritiesid();
							check++;
							break;
						}
					}
					if (check != 0) {
						auth = new Authorities();
						auth.setAuthoritiesid(authId);
						role = mapper.readValue(api.get("/DTNsBike/rest/roles/admin").toString(), roleType);
						auth.setRoleId(role);
						auth.setActive(true);
						auth.setUsername(accounts);
						String pathPutAuth = "/DTNsBike/rest/auths/" + authId;
						listRole.add("admin");
						// put
						api.push(pathPutAuth, auth);
					} else {
						auth = new Authorities();
						role = mapper.readValue(api.get("/DTNsBike/rest/roles/admin").toString(), roleType);
						auth.setRoleId(role);
						auth.setActive(true);
						auth.setUsername(accounts);
						String pathPutAuth = "/DTNsBike/rest/auths";
						listRole.add("admin");
						// post
						api.post(pathPutAuth, auth);
					}
				} else {
					int check = 0;
					Integer authId = null;
					for (int i = 0; i < listAuth.size(); i++) {
						if (listAuth.get(i).getRoleId().getId().equalsIgnoreCase("admin")) {
							authId = listAuth.get(i).getAuthoritiesid();
							check++;
							break;
						}
					}
					if (check != 0) {
						auth = new Authorities();
						auth.setAuthoritiesid(authId);
						role = mapper.readValue(api.get("/DTNsBike/rest/roles/admin").toString(), roleType);
						auth.setRoleId(role);
						auth.setActive(false);
						auth.setUsername(accounts);
						String pathPutAuth = "/DTNsBike/rest/auths/" + authId;
						api.push(pathPutAuth, auth);
						listRole.add("admin");
						// put
					}
				}

				// Check role staff
				if (accountModel.getRole().contains("staff")) {
					int check = 0;
					Integer authId = null;
					for (int i = 0; i < listAuth.size(); i++) {
						if (listAuth.get(i).getRoleId().getId().equalsIgnoreCase("staff")) {
							authId = listAuth.get(i).getAuthoritiesid();
							check++;
							break;
						}
					}
					if (check != 0) {
						auth = new Authorities();
						auth.setAuthoritiesid(authId);
						role = mapper.readValue(api.get("/DTNsBike/rest/roles/staff").toString(), roleType);
						auth.setRoleId(role);
						auth.setActive(true);
						auth.setUsername(accounts);
						String pathPutAuth = "/DTNsBike/rest/auths/" + authId;
						listRole.add("staff");
						// put
						api.push(pathPutAuth, auth);
					} else {
						auth = new Authorities();
						role = mapper.readValue(api.get("/DTNsBike/rest/roles/staff").toString(), roleType);
						auth.setRoleId(role);
						auth.setActive(true);
						auth.setUsername(accounts);
						String pathPutAuth = "/DTNsBike/rest/auths";
						listRole.add("staff");
						// post
						api.post(pathPutAuth, auth);
					}
				} else {
					int check = 0;
					Integer authId = null;
					for (int i = 0; i < listAuth.size(); i++) {
						if (listAuth.get(i).getRoleId().getId().equalsIgnoreCase("staff")) {
							authId = listAuth.get(i).getAuthoritiesid();
							check++;
							break;
						}
					}
					if (check != 0) {
						auth = new Authorities();
						auth.setAuthoritiesid(authId);
						role = mapper.readValue(api.get("/DTNsBike/rest/roles/staff").toString(), roleType);
						auth.setRoleId(role);
						auth.setActive(false);
						auth.setUsername(accounts);
						String pathPutAuth = "/DTNsBike/rest/auths/" + authId;
						api.push(pathPutAuth, auth);
						listRole.add("staff");
						// put
					}
				}

				// Check role user
				if (accountModel.getRole().contains("user")) {
					int check = 0;
					Integer authId = null;
					for (int i = 0; i < listAuth.size(); i++) {
						if (listAuth.get(i).getRoleId().getId().equalsIgnoreCase("user")) {
							authId = listAuth.get(i).getAuthoritiesid();
							check++;
							break;
						}
					}
					if (check != 0) {
						auth = new Authorities();
						auth.setAuthoritiesid(authId);
						role = mapper.readValue(api.get("/DTNsBike/rest/roles/user").toString(), roleType);
						auth.setRoleId(role);
						auth.setActive(true);
						auth.setUsername(accounts);
						String pathPutAuth = "/DTNsBike/rest/auths/" + authId;
						listRole.add("user");
						// put
						api.push(pathPutAuth, auth);
					} else {
						auth = new Authorities();
						role = mapper.readValue(api.get("/DTNsBike/rest/roles/user").toString(), roleType);
						auth.setRoleId(role);
						auth.setActive(true);
						auth.setUsername(accounts);
						String pathPutAuth = "/DTNsBike/rest/auths";
						listRole.add("user");
						// post
						api.post(pathPutAuth, auth);
					}
				} else {
					int check = 0;
					Integer authId = null;
					for (int i = 0; i < listAuth.size(); i++) {
						if (listAuth.get(i).getRoleId().getId().equalsIgnoreCase("user")) {
							authId = listAuth.get(i).getAuthoritiesid();
							check++;
							break;
						}
					}
					if (check != 0) {
						auth = new Authorities();
						auth.setAuthoritiesid(authId);
						role = mapper.readValue(api.get("/DTNsBike/rest/roles/user").toString(), roleType);
						auth.setRoleId(role);
						auth.setActive(false);
						auth.setUsername(accounts);
						String pathPutAuth = "/DTNsBike/rest/auths/" + authId;
						api.push(pathPutAuth, auth);
						listRole.add("user");
						// put
					}
				}
				session.set("editUserID", accountsNew.getUsername());
				m.addAttribute("editUserID", accountsNew.getUsername());
				m.addAttribute("message", "Thêm Thành Công!");
				m.addAttribute("imgs", session.get("imgs"));
				m.addAttribute("listRoles", listRoles);
				session.set("accountModel", accountModels);
				return "redirect:/admin/account/form/edit/" + accountsNew.getUsername();
			}
		}
		m.addAttribute("listRoles", listRoles);
		session.set("accountModel", accountModels);
		return "admin/common/accounts/account-update.html";
	}

	@RequestMapping("/account/form/cancel")
	public String cancelFormAccount() {
		session.set("accountModel", null);
		Optional<String> id = Optional.of(session.get("editUserID"));
		return "redirect:/admin/account/form/edit/" + id.get();
	}

	@RequestMapping("/account/form/reset")
	public String resetFormAccount() {
		session.set("imgs", null);
		session.remove("accountModel");
		session.remove("accountModels");
		session.remove("editUserID");
		return "redirect:/admin/account/account-update.html?load=true";
	}

	@RequestMapping("/account/form/edit/{id}")
	public String editFormAccount(Model m, @PathVariable("id") Optional<String> username)
			throws JsonMappingException, JsonProcessingException, IOException {
		m.addAttribute("editUserID", username.get());

		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<Roles>> typeRoles = new TypeReference<List<Roles>>() {
		};
		List<Roles> listRoles = mapper.readValue(api.get("/DTNsBike/rest/roles").toString(), typeRoles);
		m.addAttribute("listRoles", listRoles);
		AccountAdminModel accountModel = new AccountAdminModel();
		TypeReference<Accounts> typeAcc = new TypeReference<Accounts>() {
		};
		String pathAcc = "/DTNsBike/rest/accounts/" + username.get();
		if (api.get(pathAcc) == null) {
			return "redirect:/admin/account-list.html";
		}
		Accounts users = mapper.readValue(api.get(pathAcc).toString(), typeAcc);
		// Get role
		TypeReference<List<Authorities>> typeRole = new TypeReference<List<Authorities>>() {
		};
		String pathAuth = "/DTNsBike/rest/auths/user/" + username.get();
		List<Authorities> listRole = mapper.readValue(api.get(pathAuth).toString(), typeRole);
		// Set role
		List<String> role = new ArrayList<>();
		for (int i = 0; i < listRole.size(); i++) {
			if (listRole.get(i).getActive() == true) {
				role.add(listRole.get(i).getRoleId().getId());
			}
		}
		// Set user model
		accountModel = new AccountAdminModel();
		if (session.get("accountModel") == null) {
			accountModel.setUsername(users.getUsername());
			accountModel.setFirstname(users.getFirstname());
			accountModel.setMiddlename(users.getMiddlename());
			accountModel.setLastname(users.getLastname());
			accountModel.setEmail(users.getEmail());
			accountModel.setPhone(users.getPhone());
			accountModel.setGender(users.getGender());
			accountModel.setBirthday(dateService.dateToString("yyyy-MM-dd", users.getBirthday()));
			accountModel.setActive(users.getActive());
			accountModel.setRole(role);
			String img = "default.jpg";
			if (!users.getPhoto().isEmpty()) {
				img = users.getPhoto();
			}
			session.set("imgs", img);
			m.addAttribute("imgs", img);
		} else {
			accountModel = session.get("accountModel");
			m.addAttribute("imgs", session.get("imgs"));
			if (!username.get().equals(accountModel.getUsername())) {
				session.set("editUserID", username.get());
				return "redirect:/admin/account/form/cancel";
			}
		}
		m.addAttribute("accountModel", accountModel);
		session.set("editUserID", username.get());
		m.addAttribute("editUserID", username.get());
		return "admin/common/accounts/account-update.html";
	}

	@PostMapping("/accounts/upload/img/{id}")
	public String multiUploadFileModel(Model model, @ModelAttribute("accountAdminModel") AccountAdminModel accountModel,
			HttpServletRequest request, HttpServletResponse response, RedirectAttributes ra,
			@PathVariable("id") String username) throws IOException {
		Optional<String> id = Optional.of(session.get("editUserID"));
		Accounts accounts = new Accounts();
		TypeReference<Accounts> typeAcc = new TypeReference<Accounts>() {
		};
		String pathAcc = "/DTNsBike/rest/accounts/" + id.get();

		if (api.get(pathAcc) == null) {

			return "redirect:/admin/account/form/edit/" + id.get();
		} else {
			accounts = mapper.readValue(api.get(pathAcc).toString(), typeAcc);
		}

		File file = fileService.save("accounts_img", accountModel.getFile(), null, ra);
		if (file == null) {
			return "redirect:/admin/account/form/edit/" + id.get();
		}
		// Bỏ qua khi đang chỉnh sửa thông tin cho chính tài khoản đăng nhập
		if (request.getUserPrincipal().getName().equalsIgnoreCase(accounts.getUsername())) {
			// Get role
			TypeReference<List<Authorities>> typeRole = new TypeReference<List<Authorities>>() {
			};
			String pathAuth = "/DTNsBike/rest/auths/user/" + id.get();
			List<Authorities> listRole = mapper.readValue(api.get(pathAuth).toString(), typeRole);
			// Set role
			List<String> role = new ArrayList<>();
			for (int i = 0; i < listRole.size(); i++) {
				role.add(listRole.get(i).getRoleId().getId());
			}
			accountModel.setActive(accounts.getActive());
			accountModel.setRole(role);
		}
		session.set("imgs", file.getName());
		session.set("accountModel", accountModel);
		return "redirect:/admin/account/form/edit/" + id.get();
	}

	@GetMapping("/accounts/upload/img/{id}")
	public String getMultiUploadFileModel(Model model,
			@ModelAttribute("accountAdminModel") AccountAdminModel accountModel, RedirectAttributes ra,
			@PathVariable("id") String username) throws IOException {
		Optional<String> id = Optional.of(session.get("editUserID"));
		return "redirect:/admin/account/form/edit/" + id.get();
	}

	@PostMapping("/accounts/upload/imgaccountinsert")
	public String imgaccountinsertMultiUploadFileModel(Model model,
			@ModelAttribute("accountAdminModel") AccountAdminModel accountModel, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes ra) throws IOException {

		File file = fileService.save("accounts_img", accountModel.getFile(), null, ra);
		if (file == null) {
			session.set("accountModels", accountModel);
			return "redirect:/admin/account/form/add";
		}
		session.set("uriInsert", true);
		session.set("imgs", file.getName());
		session.set("accountModels", accountModel);
		session.set("accountModel", accountModel);
		return "redirect:/admin/account/form/add";
	}

	@GetMapping("/accounts/upload/imgaccountinsert")
	public String getImgaccountinsertMultiUploadFileModel(Model model,
			@ModelAttribute("accountAdminModel") AccountAdminModel accountModel, RedirectAttributes ra)
			throws IOException {
		return "redirect:/admin/account/form/add";
	}

	@GetMapping({ "/account/form/resetpass", "/account/form/reset/pass/{id}" })
	public String getResetPass(Model m, @ModelAttribute("userResetpassModel") UserResetpassModel userResetpassModel) {
		m.addAttribute("message", session.get("message"));
		session.remove("myRoleAccount");
		session.remove("myAccount");
		session.remove("message");
		return "admin/common/accounts/account-resetpass.html";
	}

	@PostMapping("/account/form/resetpass")
	public String postResetPass(Model m,
			@Valid @ModelAttribute("userResetpassModel") UserResetpassModel userResetpassModel, BindingResult rs)
			throws JsonMappingException, JsonProcessingException, IOException {
		int err = 0;

		if (fieldErrorService.checkCodeFieldErrors("username", "NotBlank", rs)) {
			err++;
			m.addAttribute("message", "Vui lòng nhập tên đăng nhập để tìm tài khoản.");
		} else {
			if (fieldErrorService.checkCodeFieldErrors("username", "DuplicateUsername", rs) == false) {
				err++;
				m.addAttribute("message", "Không tìm thấy tài khoản.");
			}
		}
		if (err == 0) {
			Accounts accounts = new Accounts();
			TypeReference<Accounts> typeAcc = new TypeReference<Accounts>() {
			};
			String pathAcc = "/DTNsBike/rest/accounts/" + userResetpassModel.getUsername();
			accounts = mapper.readValue(api.get(pathAcc).toString(), typeAcc);

			TypeReference<List<Authorities>> typeAuth = new TypeReference<List<Authorities>>() {
			};
			String pathAuth = "/DTNsBike/rest/auths/user/" + userResetpassModel.getUsername();
			List<Authorities> listAuth = mapper.readValue(api.get(pathAuth).toString(), typeAuth);
			List<String> listRole = new ArrayList<>();
			if (api.get(pathAuth) != null) {
				for (int i = 0; i < listAuth.size(); i++) {
					if (listAuth.get(i).getActive() == true) {
						listRole.add(listAuth.get(i).getRoleId().getName());
					}
				}
				String role = new String();
				if (listRole != null) {
					for (int i = 0; i < listRole.size(); i++) {
						if (i < listRole.size() - 1) {
							if (i == 0) {
								role = role + listRole.get(i) + ", ";
							} else {
								role = role + listRole.get(i).toLowerCase() + ", ";
							}
						} else {
							if (i == 0) {
								role = role + listRole.get(i);
							} else {
								role = role + listRole.get(i).toLowerCase() + ".";
							}
						}
					}
				}
			}
			session.set("myAccount", accounts);
		} else {
			session.remove("myRoleAccount");
			session.remove("myAccount");
		}
		return "admin/common/accounts/account-resetpass.html";
	}

	@PostMapping("/account/form/reset/pass/{id}")
	public String resetPass(Model m, @PathVariable("id") Optional<String> username)
			throws JsonMappingException, JsonProcessingException, IOException {
		if (username.isPresent()) {
			Accounts accounts = new Accounts();
			TypeReference<Accounts> typeAcc = new TypeReference<Accounts>() {
			};
			String pathAcc = "/DTNsBike/rest/accounts/" + username.get();
			if (api.get(pathAcc) != null) {
				accounts = mapper.readValue(api.get(pathAcc).toString(), typeAcc);
				String pass = randomPassService.randomPassword(16);
				accounts.setPassword(pass);
				api.post("/DTNsBike/rest/accounts", accounts);
				String body = "Mật khẩu của bạn vừa được hệ thống tạo mới.\r\n"
						+ "Mật khẩu đăng nhập vào website DTNsBike là: " + pass;
				try {
					mailerService.send(accounts.getEmail(), "Thông Tin Tài Khoản DTNsBike", body);
				} catch (MessagingException e) {
					System.out.println("Không gửi được gmail!");
				}
				session.set("message", "Đã làm mới mật khẩu cho tài khoản " + accounts.getUsername() + ".");
			}
		}
		return "redirect:/admin/account/form/resetpass";
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
