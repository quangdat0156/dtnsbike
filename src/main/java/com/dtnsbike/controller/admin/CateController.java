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
import com.dtnsbike.entity.Categories;
import com.dtnsbike.entity.Types;
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
public class CateController {
	String path = "admin/common/cate/";

	@Autowired
	CategoryService cateService;

	@Autowired
	TypesService typeService;

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

//	Danh sách danh mục con
	int remove = 0, re = 0;
	String name = null;

	@RequestMapping("/cate-list.html")
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
		TypeReference<List<Categories>> typeBrand = new TypeReference<List<Categories>>() {
		};
		List<Categories> listCate = mapper.readValue(api.get("/DTNsBike/rest/categories").toString(), typeBrand);
		if (pageService.checkTotalPages(listCate, page, size) == false) {
			page = 1;
		}

		Pageable pageable = PageRequest.of((page - 1), size);
		Integer totalPage = listCate.size() / size;
		if (listCate.size() % size > 0) {
			totalPage = totalPage + 1;
		}

		@SuppressWarnings("unchecked")
		List<Categories> cate = (List<Categories>) pageService.toPage(listCate, pageable).getContent();
		m.addAttribute("listCate", cate);

		m.addAttribute("listPage", pageService.listPage(listCate, page, size));
		m.addAttribute("page", page);

		m.addAttribute("size", size);
		m.addAttribute("totalPage", totalPage.intValue());
		m.addAttribute("totalItems", listCate.size());
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
		session.set("nameCate", name);

		return path + "cate-list.html";
	}

//	Form cập nhật danh mục con
	int i = 0, i2 = 0;

	@RequestMapping("/cate-update")
	public String getFormUpdate(Model m, @ModelAttribute("cateModel") Categories model) {
		session.set("editCate", "/admin/cate-update");

		if (session.get("editCateId") != null) {
			m.addAttribute("editCateId", session.get("editCateId"));
			if (session.get("cateModel") != null) {
				model = session.get("cateModel");
				m.addAttribute("cateModel", model);
			}
		} else {
			model = new Categories();
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

		return path + "cate-update.html";
	}

//	Form cập nhật edit :v
	@RequestMapping("/cate-update/edit")
	public String editForm(Model m, @RequestParam("id") Optional<Integer> id,
			@ModelAttribute("cateModel") Categories model)
			throws JsonMappingException, JsonProcessingException, IOException {
		// Get sizes
		TypeReference<Categories> type = new TypeReference<Categories>() {
		};
		String path = "/DTNsBike/rest/categories/" + id.get();
		Categories cate = mapper.readValue(api.get(path).toString(), type);
		// Set Brand model
		model = cate;
		// Set session model
		session.set("cateModel", model);
		session.set("editCateId", id.get());

		return "redirect:/admin/cate-update";
	}

//	Form update reset form 
	@RequestMapping("/cate-update/reset")
	public String resetForm(Model m) {

		session.remove("cateModel");
		session.remove("editCateId");
		resetForm();

		return "redirect:/admin/cate-update";
	}

//	form update thêm 
	@PostMapping("/cate-update/add")
	public String addForm(Model m, @Valid @ModelAttribute("caterModel") Categories model, Errors error)
			throws JsonMappingException, JsonProcessingException, IOException {
		// add
		TypeReference<Categories> type = new TypeReference<Categories>() {
		};
		String path = "/DTNsBike/rest/categories";

		Categories cate = new Categories();
		cate = model;
		int chk = 0;

		if (error.hasErrors()) {
			session.set("errorCate", error);
			chk++;
		} else {
			session.set("errorCate", null);
		}
		if (chk == 0) {
			JsonNode test = api.post(path, cate);
			if (mapper.readValue(test.toString(), type) != null) {
				i2++;
				session.set("errorCate", null);
			}
		}

		return "redirect:/admin/cate-update";
	}

//	form update cập nhật
	@PostMapping("/cate-update/update")
	public String updateForm(Model m, @Valid @ModelAttribute("cateModel") Categories model, Errors error)
			throws JsonMappingException, JsonProcessingException, IOException {
		Integer id = session.get("editCateId");
		if (id != null) {
			TypeReference<Categories> type = new TypeReference<Categories>() {
			};
			String path = "/DTNsBike/rest/categories/" + id;

			Categories cate = mapper.readValue(api.get(path).toString(), type);
			cate.setName(model.getName());
			cate.setTypeId(model.getTypeId());
			cate.setDescription(model.getDescription());

			int chk = 0;
			session.set("errorCate", null);
			if (error.hasErrors()) {
				session.set("errorCate", error);
				chk++;
			}
			if (chk == 0) {
				JsonNode test = api.push(path, cate);
				if (mapper.readValue(test.toString(), type) != null) {
					i++;
				}
			}
		}

		return "redirect:/admin/cate-update/edit?id=" + id;
	}

//	form update xóa 
	@RequestMapping("/cate-update/delete/{id}")
	public String deleteForm(@PathVariable("id") Integer id, Model m)
			throws JsonMappingException, JsonProcessingException, IOException {
		TypeReference<Boolean> type = new TypeReference<Boolean>() {
		};
		String path = "/DTNsBike/rest/categories/check/" + id;
		String path2 = "/DTNsBike/rest/categories/" + id;

		Boolean check = mapper.readValue(api.get(path).toString(), type);
		name = cateService.findById(id).getName();
		if (check == true) {
			remove++;
			re = 0;
		} else {
			remove = 0;
			re++;
			api.delete(path2);
		}

		return "redirect:/admin/cate-list.html";
	}

	public void resetForm() {
		session.remove("remove");
		session.remove("checks");
		session.remove("checks2");
		session.remove("nameCate");
		session.remove("errorCate");
	}

	@ModelAttribute("cate")
	public List<Types> listType() {
		return typeService.findAll();
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
