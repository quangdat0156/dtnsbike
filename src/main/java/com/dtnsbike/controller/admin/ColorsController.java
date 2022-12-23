package com.dtnsbike.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.dtnsbike.dao.service.ColorsService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Colors;
import com.dtnsbike.service.ConvertPageService;
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
public class ColorsController {
	String path = "admin/common/colors/";

	@Autowired
	ColorsService colorsService;

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
	AccountsDAO accountDao;

	@Autowired
	HttpServletRequest request;

//	Danh sách màu sắc
	int remove = 0, re = 0;
	String name = null;

	@RequestMapping("/colors-list.html")
	public String getList(Model m, HttpServletRequest request, @RequestParam("page") Optional<String> pages)
			throws JsonMappingException, JsonProcessingException, IOException {
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
		TypeReference<List<Colors>> typeBrand = new TypeReference<List<Colors>>() {
		};
		List<Colors> listColors = mapper.readValue(api.get("/DTNsBike/rest/colors").toString(), typeBrand);
		if (pageService.checkTotalPages(listColors, page, size) == false) {
			page = 1;
		}

		Pageable pageable = PageRequest.of((page - 1), size);
		Integer totalPage = listColors.size() / size;
		if (listColors.size() % size > 0) {
			totalPage = totalPage + 1;
		}

		@SuppressWarnings("unchecked")
		List<Colors> Colors = (List<Colors>) pageService.toPage(listColors, pageable).getContent();
		m.addAttribute("listColors", Colors);

		m.addAttribute("listPage", pageService.listPage(listColors, page, size));
		m.addAttribute("page", page);

		m.addAttribute("size", size);
		m.addAttribute("totalPage", totalPage.intValue());
		m.addAttribute("totalItems", listColors.size());
		m.addAttribute("pageableItems", pageable);

		resetForm();

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
		session.set("nameColor", name);

		return path + "colors-list.html";
	}

//	Form cập nhật màu sắc
	int i = 0, i2 = 0;

	@RequestMapping("/colors-update")
	public String getFormUpdate(Model m, @ModelAttribute("colorModel") Colors model) {
		session.set("editColor", "/admin/colors-update");

		if (session.get("editColorId") != null) {
			m.addAttribute("editColorId", session.get("editColorId"));
			if (session.get("colorModel") != null) {
				model = session.get("colorModel");
				m.addAttribute("colorModel", model);
			}
		} else {
			model = new Colors();
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

		return path + "colors-update.html";
	}

//	Form cập nhật edit :v
	@RequestMapping("/colors-update/edit")
	public String editForm(Model m, @RequestParam("id") Optional<String> id, @ModelAttribute("colorModel") Colors model)
			throws JsonMappingException, JsonProcessingException, IOException {
		// Get sizes
		TypeReference<Colors> type = new TypeReference<Colors>() {
		};
		String path = "/DTNsBike/rest/colors/" + id.get();
		Colors color = mapper.readValue(api.get(path).toString(), type);
		// Set Brand model
		model = color;
		// Set session model
		session.set("colorModel", model);
		session.set("editColorId", id.get());

		return "redirect:/admin/colors-update";
	}

//	Form update reset form 
	@RequestMapping("/colors-update/reset")
	public String resetForm(Model m) {
		session.remove("colorsModel");
		session.remove("editColorId");

		resetForm();

		return "redirect:/admin/colors-update";
	}

//	form update thêm 
	@PostMapping("/colors-update/add")
	public String addForm(Model m, @Valid @ModelAttribute("colorModel") Colors model, Errors error)
			throws JsonMappingException, JsonProcessingException, IOException {
		// add
		TypeReference<Colors> type = new TypeReference<Colors>() {
		};
		String path = "/DTNsBike/rest/colors";
		int chk = 0;
		String idd = model.getId().substring(model.getId().indexOf("#") + 1);
		model.setId(idd);
		Colors color = new Colors();
		color = model;

		if (!colorsService.findById(idd).isEmpty()) {
			chk++;
			session.set("color_mess", "Màu sắc này đã tồn tại !");
		} else {
			session.set("color_mess", null);
		}
		if (error.hasErrors()) {
			session.set("errorColor", error);
			chk++;
		} else {
			session.set("errorColor", null);
		}
		if (chk == 0) {
			JsonNode test = api.post(path, color);
			if (mapper.readValue(test.toString(), type) != null) {
				i2++;
				session.set("errorColor", null);
			}
		}

		return "redirect:/admin/colors-update";
	}

//	form update cập nhật
	@PostMapping("/colors-update/update")
	public String updateForm(Model m, @Valid @ModelAttribute("colorModel") Colors model, Errors error)
			throws JsonMappingException, JsonProcessingException, IOException {
		String id = session.get("editColorId");
		if (id != null) {
			TypeReference<Colors> type = new TypeReference<Colors>() {
			};
			String path = "/DTNsBike/rest/colors/" + id;

			Colors color = mapper.readValue(api.get(path).toString(), type);
			color.setName(model.getName());

			int chk = 0;
			session.set("errorColor", null);
			if (error.hasErrors()) {
				session.set("errorColor", error);
				chk++;
			}
			if (chk == 0) {
				JsonNode test = api.push(path, color);
				if (mapper.readValue(test.toString(), type) != null) {
					i++;
				}
			}
		}

		return "redirect:/admin/colors-update/edit?id=" + id;
	}

//	form update xóa thương hiệu
	@RequestMapping("/colors-update/delete/{id}")
	public String deleteForm(@PathVariable("id") String id, Model m)
			throws JsonMappingException, JsonProcessingException, IOException {
		TypeReference<Boolean> type = new TypeReference<Boolean>() {
		};
		String path = "/DTNsBike/rest/colors/check/" + id;
		String path2 = "/DTNsBike/rest/colors/" + id;

		Boolean check = mapper.readValue(api.get(path).toString(), type);
		name = colorsService.findById(id).get().getName();
		if (check == true) {
			remove++;
			re = 0;
		} else {
			remove = 0;
			re++;
			api.delete(path2);
		}

		return "redirect:/admin/colors-list.html";
	}

	public void resetForm() {
		session.remove("remove");
		session.remove("checks");
		session.remove("checks2");
		session.remove("nameColor");
		session.remove("color_mess");
		session.remove("errorColor");
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
