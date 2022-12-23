package com.dtnsbike.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.dao.service.SizesService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Sizes;
import com.dtnsbike.service.ConvertDateService;
import com.dtnsbike.service.ConvertPageService;
import com.dtnsbike.service.FileManagerService;
import com.dtnsbike.service.GetContentJsonService;
import com.dtnsbike.service.RestApiService;
import com.dtnsbike.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/admin")
public class SizesController {
	String path = "admin/common/sizes/";

	@Autowired
	SizesService sizesService;

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

//	Danh sách thương hiệu
	int remove = 0, re = 0;
	String name = null;

	@RequestMapping("/size-list.html")
	public String getList(Model m, HttpServletRequest request, @RequestParam("page") Optional<String> pages)
			throws JsonMappingException, JsonProcessingException, IOException {
//        @RequestParam("size") Integer size
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
			}
		}
		if (size < 5 || size > 20 || !arr.contains(size)) {
			size = 5;
		}
		TypeReference<List<Sizes>> typeSizes = new TypeReference<List<Sizes>>() {
		};
		List<Sizes> listSizes = mapper.readValue(api.get("/DTNsBike/rest/sizes").toString(), typeSizes);
		if (pageService.checkTotalPages(listSizes, page, size) == false) {
			page = 1;
		}

		Pageable pageable = PageRequest.of((page - 1), size);
		Integer totalPage = listSizes.size() / size;
		if (listSizes.size() % size > 0) {
			totalPage = totalPage + 1;
		}

		@SuppressWarnings("unchecked")
		List<Sizes> sizess = (List<Sizes>) pageService.toPage(listSizes, pageable).getContent();
		m.addAttribute("listSizes", sizess);

		m.addAttribute("listPage", pageService.listPage(listSizes, page, size));
		m.addAttribute("page", page);

		m.addAttribute("size", size);
		m.addAttribute("totalPage", totalPage.intValue());
		m.addAttribute("totalItems", listSizes.size());
		m.addAttribute("pageableItems", pageable);

		resetForm();

		// Check alert remove
		if (remove > 0) {
			session.set("remove", true);
			remove = 0;
		} else {
			session.set("remove", false);
		}
		if (re > 0) {
			session.set("checks2", true);
			re = 0;
		} else {
			session.set("checks2", false);
		}
		session.set("nameSize", name);

		return path + "size-list.html";
	}

	int i = 0, i2 = 0;

//	Form cập nhật thương hiệu
	@RequestMapping("/size-update")
	public String getFormUpdate(Model m, @ModelAttribute("sizeModel") Sizes model) {
		session.set("editSize", "/admin/size-update");

		if (session.get("editSizeId") != null) {
			m.addAttribute("editSizeId", session.get("editSizeId"));
			if (session.get("sizeModel") != null) {
				model = session.get("sizeModel");
				m.addAttribute("sizeModel", model);
			}
		} else {
			model = new Sizes();
		}
		// Check alert update
		if (i > 0) {
			session.set("checks", true);
			i = 0;
		} else {
			session.set("checks", false);
		}
		// Check alert add
		if (i2 > 0) {
			session.set("checks2", true);
			i2 = 0;
		} else {
			session.set("checks2", false);
		}
		// Check validation

		return path + "size-update.html";
	}

//	Form cập nhật edit :v
	@RequestMapping("/size-update/edit")
	public String editForm(Model m, @RequestParam("id") Optional<String> id, @ModelAttribute("sizeModel") Sizes model)
			throws JsonMappingException, JsonProcessingException, IOException {
		// Get sizes
		TypeReference<Sizes> type = new TypeReference<Sizes>() {
		};
		String path = "/DTNsBike/rest/sizes/" + id.get();
		Sizes sizes = mapper.readValue(api.get(path).toString(), type);
		// Set Brand model
		model = sizes;
		// Set session model
		session.set("sizeModel", model);
		session.set("editSizeId", id.get());

		return "redirect:/admin/size-update";
	}

//	Form update reset form 
	@RequestMapping("/size-update/reset")
	public String resetForm(Model m) {

		session.remove("sizeModel");
		session.remove("editSizeId");
		resetForm();

		return "redirect:/admin/size-update";
	}

//	form update thêm 
	@PostMapping("/size-update/add")
	public String addForm(Model m, @Valid @ModelAttribute("sizeModel") Sizes model, Errors error)
			throws JsonMappingException, JsonProcessingException, IOException {
		// add
		TypeReference<Sizes> type = new TypeReference<Sizes>() {
		};
		String path = "/DTNsBike/rest/sizes";

		Sizes sizes = new Sizes();
		sizes = model;
		int chk = 0;
		Pattern pattern = Pattern.compile("[^A-Za-z0-9]");
		if (!model.getId().isEmpty()) {
			String id = model.getId();
			Optional<Sizes> check = sizesService.findById(id);
			if (model.getId().length() > 10) {
				session.set("size_mess", "Mã kích thước không được quá 10 ký tự !");
				chk++;
			}else if (!check.isEmpty()) {
				session.set("size_mess", "Kích thước đã tồn tại !");
				chk++;
			} else if (pattern.matcher(model.getId()).find()) {
				session.set("size_mess", "Mã kích thước không được chứa dấu cách hoặc ký tự đặc biệt !");
				chk++;
			} else {
				session.set("size_mess", null);
			}
			 
		}
		if (error.hasErrors()) {
			session.set("errorSize", error);
			chk++;
		} else {
			session.set("errorSize", null);
		}

		if (chk == 0) {
			JsonNode test = api.post(path, sizes);
			if (mapper.readValue(test.toString(), type) != null) {
				i2++;
				session.set("errorSize", null);
			}
		}

		return "redirect:/admin/size-update";
	}

//	form update cập nhật
	@PostMapping("/size-update/update")
	public String updateForm(Model m, @Valid @ModelAttribute("sizeModel") Sizes model, Errors error)
			throws JsonMappingException, JsonProcessingException, IOException {
		String id = session.get("editSizeId");
		if (id != null) {
			TypeReference<Sizes> type = new TypeReference<Sizes>() {
			};
			String path = "/DTNsBike/rest/sizes/" + id;

			Sizes sizes = mapper.readValue(api.get(path).toString(), type);
			sizes.setName(model.getName());

			int chk = 0;
			session.set("errorSize", null);
			if (model.getName().isEmpty()) {
				session.set("errorSize", error);
				chk++;
			}
			if (chk == 0) {
				JsonNode test = api.push(path, sizes);
				if (mapper.readValue(test.toString(), type) != null) {
					i++;
				}
			}
		}

		return "redirect:/admin/size-update/edit?id=" + id;
	}

//	form update xóa thương hiệu
	@RequestMapping("/size-update/delete/{id}")
	public String deleteForm(@PathVariable("id") String id, Model m)
			throws JsonMappingException, JsonProcessingException, IOException {
		TypeReference<Boolean> type = new TypeReference<Boolean>() {
		};
		String path = "/DTNsBike/rest/sizes/check/" + id;
		String path2 = "/DTNsBike/rest/sizes/" + id;

		Boolean check = mapper.readValue(api.get(path).toString(), type);
		name = sizesService.findById(id).get().getId();
		if (check == true) {
			remove++;
			re = 0;
		} else {
			remove = 0;
			re++;
			api.delete(path2);
		}

		return "redirect:/admin/size-list.html";
	}

	public void resetForm() {
		session.remove("errorSize");
		session.remove("remove");
		session.remove("checks");
		session.remove("checks2");
		session.remove("nameSize");
		session.remove("size_mess");
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
