package com.dtnsbike.controller.admin;

import java.io.File;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.dao.service.BrandsService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Brands;
import com.dtnsbike.model.UploadImgAccount;
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
public class BrandsController {
	String path = "admin/common/brands/";

	@Autowired
	BrandsService brandsService;

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

	@RequestMapping("/brand-list.html")
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
		TypeReference<List<Brands>> typeBrand = new TypeReference<List<Brands>>() {
		};
		List<Brands> listBrands = mapper.readValue(api.get("/DTNsBike/rest/brands").toString(), typeBrand);
		if (pageService.checkTotalPages(listBrands, page, size) == false) {
			page = 1;
		}

		Pageable pageable = PageRequest.of((page - 1), size);
		Integer totalPage = listBrands.size() / size;
		if (listBrands.size() % size > 0) {
			totalPage = totalPage + 1;
		}

		@SuppressWarnings("unchecked")
		List<Brands> brands = (List<Brands>) pageService.toPage(listBrands, pageable).getContent();
		m.addAttribute("listBrands", brands);

		m.addAttribute("listPage", pageService.listPage(listBrands, page, size));
		m.addAttribute("page", page);

		m.addAttribute("size", size);
		m.addAttribute("totalPage", totalPage.intValue());
		m.addAttribute("totalItems", listBrands.size());
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
		session.set("namebrand", name);

		return path + "brand-list.html";
	}

	int i = 0, i2 = 0;

//	Form cập nhật thương hiệu
	@RequestMapping("/brand-update")
	public String getFormUpdate(Model m, @ModelAttribute("brandModel") Brands model) {
		session.set("editBrand", "/admin/brand-update");
		m.addAttribute("imgBrand", session.get("imgBrand"));

		if (session.get("editBrandId") != null) {
			m.addAttribute("editBrandId", session.get("editBrandId"));
			if (session.get("brandModel") != null) {
				model = session.get("brandModel");
				m.addAttribute("brandModel", model);
			}
		} else {
			model = new Brands();
			m.addAttribute("imgBrand", session.get("imgBrand"));
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

		return path + "brand-update.html";
	}

//	Form cập nhật edit :v
	@RequestMapping("/brand-update/edit")
	public String editForm(Model m, @RequestParam("id") Optional<Integer> id,
			@ModelAttribute("brandModel") Brands model)
			throws JsonMappingException, JsonProcessingException, IOException {
		// Get brands
		TypeReference<Brands> type = new TypeReference<Brands>() {
		};
		String path = "/DTNsBike/rest/brands/" + id.get();
		Brands brands = mapper.readValue(api.get(path).toString(), type);
		// Set Brand model
		String img = "default.jpg";
		if (!brands.getImage().isEmpty()) {
			img = brands.getImage();
		}
		brands.setImage(img);
		model = brands;
		// Set session model
		session.set("imgBrand", img);
		session.set("brandModel", model);
		session.set("editBrandId", id.get());

		return "redirect:/admin/brand-update";
	}

//	Form update reset form 
	@RequestMapping("/brand-update/reset")
	public String resetForm(Model m) {
		session.set("imgBrand", null);
		session.remove("brandModel");
		session.remove("editBrandId");

		resetForm();

		return "redirect:/admin/brand-update";
	}

//	upload hình ảnh
	@PostMapping("/brands/upload/img")
	public String multiUploadFileModel(Model model, @ModelAttribute("uploadImgBrand") UploadImgAccount data,
			RedirectAttributes ra) throws IOException {
		File file = null;
		Brands br = session.get("brandModel");
		if (br != null) {
			file = fileService.save("brands_img", data.getFile(), "imgBrand" + br.getBrandid(), ra);
		} else {
			file = fileService.save("brands_img", data.getFile(), null, ra);
		}
		session.set("imgBrand", file.getName());
		return "redirect:/admin/brand-update";
	}

//	form update thêm thương hiệu
	@PostMapping("/brand-update/add")
	public String addForm(Model m, @Valid @ModelAttribute("brandModel") Brands model, Errors error)
			throws JsonMappingException, JsonProcessingException, IOException {
		// set ảnh :))
		String imgBrand = session.get("imgBrand");
		if (imgBrand != null) {
			model.setImage(imgBrand);
		} else {
			model.setImage("default.jpg");
		}
		// add
		TypeReference<Brands> typeBrand = new TypeReference<Brands>() {
		};
		String path = "/DTNsBike/rest/brands";

		Brands brands = new Brands();
		brands.setBrand(model.getBrand());
		brands.setDescription(model.getDescription());
		brands.setImage(model.getImage());
		int chk = 0;
		if (error.hasErrors()) {
			session.set("errorBrand", error);
			chk++;
		} else {
			session.set("errorBrand", null);
		}
		if (chk == 0) {
			JsonNode test = api.post(path, brands);
			if (mapper.readValue(test.toString(), typeBrand) != null) {
				i2++;
			}
		}

		return "redirect:/admin/brand-update";
	}

//	form update cập nhật
	@PostMapping("/brand-update/update")
	public String updateForm(Model m, @Valid @ModelAttribute("brandModel") Brands model, Errors error)
			throws JsonMappingException, JsonProcessingException, IOException {
		Integer id = session.get("editBrandId");
		// set ảnh :))
		String imgBrand = session.get("imgBrand");
		if (imgBrand != null) {
			model.setImage(imgBrand);
		} else {
			model.setImage("default.jpg");
		}
		if (id != null) {
			TypeReference<Brands> typeBrand = new TypeReference<Brands>() {
			};
			String path = "/DTNsBike/rest/brands/" + id;

			Brands brands = mapper.readValue(api.get(path).toString(), typeBrand);
			brands.setBrand(model.getBrand());
			brands.setDescription(model.getDescription());
			brands.setImage(model.getImage());

			int chk = 0;
			if (error.hasErrors()) {
				session.set("errorBrand", error);
				chk++;
			} else {
				session.set("errorBrand", null);
			}
			if (chk == 0) {
				JsonNode test = api.push(path, brands);
				if (mapper.readValue(test.toString(), typeBrand) != null) {
					i++;
				}
			}
		}

		return "redirect:/admin/brand-update/edit?id=" + id;
	}

//	form update xóa thương hiệu
	@RequestMapping("/brand-update/delete/{id}")
	public String deleteForm(@PathVariable("id") Integer id, Model m)
			throws JsonMappingException, JsonProcessingException, IOException {
		TypeReference<Boolean> type = new TypeReference<Boolean>() {
		};
		String path = "/DTNsBike/rest/brands/check/" + id;
		String path2 = "/DTNsBike/rest/brands/" + id;

		Boolean check = mapper.readValue(api.get(path).toString(), type);
		name = brandsService.findById(id).getBrand();
		if (check == true) {
			remove++;
			re = 0;
		} else {
			remove = 0;
			re++;
			api.delete(path2);
		}

		return "redirect:/admin/brand-list.html";
	}

	public void resetForm() {
		session.remove("remove");
		session.remove("checks");
		session.remove("checks2");
		session.remove("nameColor");
		session.remove("errorBrand");
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
