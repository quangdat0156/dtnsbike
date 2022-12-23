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
import com.dtnsbike.dao.service.CategoryService;
import com.dtnsbike.dao.service.TypesService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Types;
import com.dtnsbike.service.ConvertDateService;
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
public class CategoriesController {
	String path = "admin/common/categories/";

	@Autowired
	CategoryService CategoriesService;

	@Autowired
	TypesService typesService;

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
	AccountsDAO accountDao;

//	Danh sách danh mục sản phẩm
	int remove = 0, re = 0;
	String name = null;

	@RequestMapping("/category-list.html")
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
		TypeReference<List<Types>> typeBrand = new TypeReference<List<Types>>() {
		};
		List<Types> listTypes = mapper.readValue(api.get("/DTNsBike/rest/types").toString(), typeBrand);
		if (pageService.checkTotalPages(listTypes, page, size) == false) {
			page = 1;
		}

		Pageable pageable = PageRequest.of((page - 1), size);
		Integer totalPage = listTypes.size() / size;
		if (listTypes.size() % size > 0) {
			totalPage = totalPage + 1;
		}

		@SuppressWarnings("unchecked")
		List<Types> type = (List<Types>) pageService.toPage(listTypes, pageable).getContent();
		m.addAttribute("listTypes", type);

		m.addAttribute("listPage", pageService.listPage(listTypes, page, size));
		m.addAttribute("page", page);

		m.addAttribute("size", size);
		m.addAttribute("totalPage", totalPage.intValue());
		m.addAttribute("totalItems", listTypes.size());
		m.addAttribute("pageableItems", pageable);

		// Check alert remove

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
		session.set("nameCategory", name);

		return path + "category-list.html";
	}

//	Form cập nhật danh mục sản phẩm
	int i = 0, i2 = 0;

	@RequestMapping("/category-update")
	public String getFormUpdate(Model m, @ModelAttribute("categoryModel") Types model) {
		session.set("editCategory", "/admin/category-update");

		if (session.get("editCategoryId") != null) {
			m.addAttribute("editCategoryId", session.get("editCategoryId"));
			if (session.get("categoryModel") != null) {
				model = session.get("categoryModel");
				m.addAttribute("categoryModel", model);
			}
		} else {
			model = new Types();
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

		return path + "category-update.html";
	}

//	Form cập nhật edit :v
	@RequestMapping("/category-update/edit")
	public String editForm(Model m, @RequestParam("id") Optional<Integer> id,
			@ModelAttribute("categoryModel") Types model)
			throws JsonMappingException, JsonProcessingException, IOException {
		// Get sizes
		TypeReference<Types> type = new TypeReference<Types>() {
		};
		String path = "/DTNsBike/rest/types/" + id.get();
		Types cate = mapper.readValue(api.get(path).toString(), type);
		// Set Brand model
		model = cate;
		// Set session model
		session.set("categoryModel", model);
		session.set("editCategoryId", id.get());

		return "redirect:/admin/category-update";
	}

//	Form update reset form 
	@RequestMapping("/category-update/reset")
	public String resetForm(Model m) {
		session.remove("categoryModel");
		session.remove("editCategoryId");
		
		resetForm();

		return "redirect:/admin/category-update";
	}

//	form update thêm 
	@PostMapping("/category-update/add")
	public String addForm(Model m, @Valid @ModelAttribute("categoryModel") Types model, Errors error)
			throws JsonMappingException, JsonProcessingException, IOException {
		// add
		TypeReference<Types> type = new TypeReference<Types>() {
		};
		String path = "/DTNsBike/rest/types";

		Types types = new Types();
		types = model;
		int chk = 0;
		if (error.hasErrors()) {
			session.set("errorCategory", error);
			chk++;
		} else {
			session.set("errorCategory", null);
		}

		if (chk == 0) {
			JsonNode test = api.post(path, types);
			if (mapper.readValue(test.toString(), type) != null) {
				i2++;
				session.set("errorCategory", null);
			}
		}

		return "redirect:/admin/category-update";
	}

//	form update cập nhật
	@PostMapping("/category-update/update")
	public String updateForm(Model m, @Valid @ModelAttribute("categoryModel") Types model, Errors error)
			throws JsonMappingException, JsonProcessingException, IOException {
		Integer id = session.get("editCategoryId");
		if (id != null) {
			TypeReference<Types> type = new TypeReference<Types>() {
			};
			String path = "/DTNsBike/rest/types/" + id;

			Types types = mapper.readValue(api.get(path).toString(), type);
			types.setName(model.getName());
			types.setDescription(model.getDescription());

			int chk = 0;
			session.set("errorCategory", null);
			if (error.hasErrors()) {
				session.set("errorCategory", error);
				chk++;
			}
			if (chk == 0) {
				JsonNode test = api.push(path, types);
				if (mapper.readValue(test.toString(), type) != null) {
					i++;
				}
			}
		}

		return "redirect:/admin/category-update/edit?id=" + id;
	}

//	form update xóa thương hiệu
	@RequestMapping("/category-update/delete/{id}")
	public String deleteForm(@PathVariable("id") Integer id, Model m)
			throws JsonMappingException, JsonProcessingException, IOException {
		TypeReference<Boolean> type = new TypeReference<Boolean>() {
		};
		String path = "/DTNsBike/rest/types/check/" + id;
		String path2 = "/DTNsBike/rest/types/" + id;

		Boolean check = mapper.readValue(api.get(path).toString(), type);
		name = typesService.findById(id).getName();
		if (check == true) {
			remove++;
			re = 0;
		} else {
			remove = 0;
			re++;
			api.delete(path2);
		}

		return "redirect:/admin/category-list.html";
	}
	
	public void resetForm() {
		session.remove("errorCategory");
		session.remove("remove");
		session.remove("checks");
		session.remove("checks2");
		session.remove("nameCategory");
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
