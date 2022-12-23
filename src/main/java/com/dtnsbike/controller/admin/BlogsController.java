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
import com.dtnsbike.dao.service.BlogsService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Blogs;
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
public class BlogsController {
	String path = "admin/common/blogs/";

	@Autowired
	BlogsService blogs_service;

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

//	Danh sách bài viết
	int remove = 0, re = 0;
	String name = null;

	@RequestMapping("/blog-list.html")
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
		TypeReference<List<Blogs>> type = new TypeReference<List<Blogs>>() {
		};
		List<Blogs> listBlogs = mapper.readValue(api.get("/DTNsBike/rest/blogs").toString(), type);
		if (pageService.checkTotalPages(listBlogs, page, size) == false) {
			page = 1;
		}

		Pageable pageable = PageRequest.of((page - 1), size);
		Integer totalPage = listBlogs.size() / size;
		if (listBlogs.size() % size > 0) {
			totalPage = totalPage + 1;
		}

		@SuppressWarnings("unchecked")
		List<Brands> blogs = (List<Brands>) pageService.toPage(listBlogs, pageable).getContent();
		m.addAttribute("listBlogs", blogs);

		m.addAttribute("listPage", pageService.listPage(listBlogs, page, size));
		m.addAttribute("page", page);
		m.addAttribute("totalPage", totalPage.intValue());
		m.addAttribute("totalItems", listBlogs.size());
		m.addAttribute("pageableItems", pageable);

		m.addAttribute("size", size);

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
		session.set("nameBlogs", name);

		return path + "blog-list.html";
	}// Form update reset form

	@RequestMapping("/blog-update/reset")
	public String resetForm(Model m) {
		session.remove("imgBlogs");
		session.remove("blogsModel");
		session.remove("editBlogId");

		resetForm();

		return "redirect:/admin/blog-update";
	}

	int i = 0, i2 = 0;

//	Form cập nhật bài viết
	@RequestMapping("/blog-update")
	public String getFormUpdate(Model m, @ModelAttribute("blogsModel") Blogs model) {
		session.set("editBlog", "/admin/blog-update");
		m.addAttribute("imgBlogs", session.get("imgBlogs"));

		if (session.get("editBlogId") != null) {
			m.addAttribute("editBlogId", session.get("editBlogId"));
			if (session.get("blogsModel") != null) {
				model = session.get("blogsModel");
				m.addAttribute("blogsModel", model);
			}
		} else {
			model = new Blogs();
			m.addAttribute("imgBlogs", session.get("imgBlogs"));
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

		return path + "blog-update.html";
	}

//	Form cập nhật edit :v
	@RequestMapping("/blog-update/edit")
	public String editForm(Model m, @RequestParam("id") Optional<Integer> id, @ModelAttribute("blogsModel") Blogs model)
			throws JsonMappingException, JsonProcessingException, IOException {
		// Get blog
		TypeReference<Blogs> type = new TypeReference<Blogs>() {
		};
		String path = "/DTNsBike/rest/blogs/" + id.get();
		Blogs blogs = mapper.readValue(api.get(path).toString(), type);
		// Set Blog model
		String img = "default.jpg";
		if (!blogs.getImages().isEmpty()) {
			img = blogs.getImages();
		}
		blogs.setImages(img);
		model = blogs;
		// Set session model
		session.set("imgBlogs", img);
		session.set("blogsModel", model);
		session.set("editBlogId", id.get());

		return "redirect:/admin/blog-update";
	}

//	upload hình ảnh
	@PostMapping("/blogs/upload/img")
	public String multiUploadFileModel(Model model, @ModelAttribute("uploadImgBlogs") UploadImgAccount data,
			RedirectAttributes ra) throws IOException {
		File file = null;
		Blogs bl = session.get("blogsModel");
		if (bl != null) {
			file = fileService.save("blogs_img", data.getFile(), "imgBlog" + bl.getBlogid(), ra);
		} else {
			file = fileService.save("blogs_img", data.getFile(), null, ra);
		}
		session.set("imgBlogs", file.getName());
		return "redirect:/admin/blog-update";
	}

//	form update thêm thương hiệu
	@PostMapping("/blog-update/add")
	public String addForm(Model m, @Valid @ModelAttribute("blogsModel") Blogs model, Errors error)
			throws JsonMappingException, JsonProcessingException, IOException {
		// set ảnh :))
		String imgBlog = session.get("imgBlogs");
		if (imgBlog != null) {
			model.setImages(imgBlog);
		} else {
			model.setImages("default.jpg");
		}
		// add
		TypeReference<Blogs> type = new TypeReference<Blogs>() {
		};
		String path = "/DTNsBike/rest/blogs";

		Blogs blogs = new Blogs();
		Accounts acc = session.get("account");

		blogs.setBlogid(model.getBlogid());
		blogs.setAccounts(acc);
		blogs.setDescription(model.getDescription());
		blogs.setContent(model.getContent());
		blogs.setStatus(model.getStatus());
		blogs.setTitle(model.getTitle());
		blogs.setImages(model.getImages());

		int chk = 0;
		if (model.getBlogid() != null) {
			if (!blogs_service.findById(model.getBlogid()).isPresent()) {
				chk++;
				session.set("blog_mess", "Mã bài viết đã tồn tại !");
			} else {
				session.set("blog_mess", null);
			}
		}
		if (error.hasErrors()) {
			session.set("errorBlogs", error);
			chk++;
		} else {
			session.set("errorBlogs", null);
		}
		if (chk == 0) {
			JsonNode test = api.post(path, blogs);
			if (mapper.readValue(test.toString(), type) != null) {
				i2++;
				session.remove("imgBlogs");
			}
		}

		return "redirect:/admin/blog-update";
	}

//	form update cập nhật
	@PostMapping("/blog-update/update")
	public String updateForm(Model m, @Valid @ModelAttribute("blogsModel") Blogs model, Errors error)
			throws JsonMappingException, JsonProcessingException, IOException {
		Integer id = session.get("editBlogId");
		// set ảnh :))
		String imgBl = session.get("imgBlogs");
		if (imgBl != null) {
			model.setImages(imgBl);
		} else {
			model.setImages("default.jpg");
		}
		if (id != null) {
			TypeReference<Blogs> type = new TypeReference<Blogs>() {
			};
			String path = "/DTNsBike/rest/blogs/" + id;

			Blogs blogs = mapper.readValue(api.get(path).toString(), type);
			blogs.setDescription(model.getDescription());
			blogs.setContent(model.getContent());
			blogs.setStatus(model.getStatus());
			blogs.setTitle(model.getTitle());
			blogs.setImages(model.getImages());

			int chk = 0;
			if (error.hasErrors()) {
				session.set("errorBlogs", error);
				chk++;
			} else {
				session.set("errorBlogs", null);
			}
			if (chk == 0) {
				JsonNode test = api.push(path, blogs);
				if (mapper.readValue(test.toString(), type) != null) {
					i++;
				}
			}
		}

		return "redirect:/admin/blog-update/edit?id=" + id;
	}

//	form update xóa 
	@RequestMapping("/blog-update/delete/{id}")
	public String deleteForm(@PathVariable("id") Integer id, Model m)
			throws JsonMappingException, JsonProcessingException, IOException {
		TypeReference<Blogs> type = new TypeReference<Blogs>() {
		};
		TypeReference<Boolean> type2 = new TypeReference<Boolean>() {
		};

		Accounts acc = session.get("account");

		String path2 = "/DTNsBike/rest/blogs/" + id;

		Blogs bl = mapper.readValue(api.get(path2).toString(), type);

		String path = "/DTNsBike/rest/blogs/check/" + bl.getAccounts().getUsername();

		Boolean check = mapper.readValue(api.get(path).toString(), type2);

		path = "/DTNsBike/rest/blogs/check/" + acc.getUsername();

		Boolean check2 = mapper.readValue(api.get(path).toString(), type2);

		name = blogs_service.findById(id).get().getTitle();
		if (check == true && check2 == false || bl.getViews() > 0) {
			remove++;
			re = 0;
		} else {
			remove = 0;
			re++;
			api.delete(path2);
		}

		return "redirect:/admin/blog-list.html";
	}

	public void resetForm() {
		session.remove("remove");
		session.remove("checks");
		session.remove("checks2");
		session.remove("nameBlogs");
		session.remove("errorBlogs");
		session.remove("blog_mess");
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
