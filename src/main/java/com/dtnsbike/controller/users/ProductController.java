package com.dtnsbike.controller.users;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.dao.interfaces.OrdersDAO;
import com.dtnsbike.dao.interfaces.ProductDetailsDAO;
import com.dtnsbike.dao.interfaces.ProductReviewsDAO;
import com.dtnsbike.dao.interfaces.ProductsDAO;
import com.dtnsbike.dao.service.BrandsService;
import com.dtnsbike.dao.service.CategoryService;
import com.dtnsbike.dao.service.ColorsService;
import com.dtnsbike.dao.service.FavoritesService;
import com.dtnsbike.dao.service.ProductReviewsService;
import com.dtnsbike.dao.service.ProductsService;
import com.dtnsbike.dao.service.SizesService;
import com.dtnsbike.dao.service.TypesService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Brands;
import com.dtnsbike.entity.Colors;
import com.dtnsbike.entity.OrderDetails;
import com.dtnsbike.entity.Orders;
import com.dtnsbike.entity.ProductDetails;
import com.dtnsbike.entity.ProductReviews;
import com.dtnsbike.entity.Products;
import com.dtnsbike.entity.Sizes;
import com.dtnsbike.entity.Types;
import com.dtnsbike.model.CheckoutModel;
import com.dtnsbike.model.ColorDetailModel;
import com.dtnsbike.model.ReviewModel;
import com.dtnsbike.model.SizeDetailModel;
import com.dtnsbike.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
public class ProductController {

	@Autowired
	ProductsService pro_service;
	@Autowired
	ProductsDAO pro_DAO;
	@Autowired
	ProductDetailsDAO proDetailDao;
	@Autowired
	ProductReviewsDAO proReviewDao;
	@Autowired
	ProductReviewsService proreview_service;
	@Autowired
	FavoritesService fav_service;
	@Autowired
	CategoryService cate_service;
	@Autowired
	ColorsService color_service;
	@Autowired
	SizesService size_service;
	@Autowired
	BrandsService brand_service;
	@Autowired
	OrdersDAO orderDAO;
	@Autowired
	AccountsDAO accountsDAO;

	@Autowired
	SessionService session;
	@Autowired
	TypesService typ_service;
	@Autowired
	HttpServletRequest req;

	String path = "users/common/";

//	Trang sản phẩm
	@RequestMapping("/shop.html")
	public String productPage(Model m, @RequestParam("field") Optional<String> field,
			@RequestParam("show") Optional<Integer> show, @RequestParam("sort") Optional<Integer> criteria,
			@RequestParam("p") Optional<Integer> p, @RequestParam("search_query") Optional<String> kw,
			@RequestParam("cateid") Optional<Integer> cateid, @RequestParam("colorid") Optional<String> colorid,
			@RequestParam("sizeid") Optional<String> sizeid, @RequestParam("brandid") Optional<Integer> brandid) {

		Accounts ac = (Accounts) session.get("account");
		m.addAttribute("hhh", true);

		String kws = session.get("keywords");
		if (kws == null) {
			kws = " ";
		}
		String kwords = kw.orElse(kws);
		session.set("keywords", kwords);

		int s = criteria.orElse(0);
		int paging = p.orElse(0);
		int shows = show.orElse(9);

		Sort sort;
		Pageable pageable;

		if (s == 0) {
			sort = Sort.by(Direction.ASC, field.orElse("id"));
		} else {
			sort = Sort.by(Direction.DESC, field.orElse("id"));
		}

		m.addAttribute("sort", s);
		m.addAttribute("field2", field.orElse("id"));
		m.addAttribute("show", shows);

		m.addAttribute("number", paging);

		pageable = PageRequest.of(paging, shows, sort);
		Page<Products> list = null;

		try {
			if (cateid.isPresent()) {
				m.addAttribute("cateid", cate_service.findById(cateid.get()));
				list = pro_service.findAll("%" + kwords + "%", cateid.get(), pageable);
			} else if (colorid.isPresent()) {
				m.addAttribute("colorid", colorid.get());
				list = pro_service.findAllColor("%" + kwords + "%", colorid.get(), pageable);
			} else if (sizeid.isPresent()) {
				m.addAttribute("sizeid", sizeid.get());
				list = pro_service.findAllSize("%" + kwords + "%", sizeid.get(), pageable);
			} else if (brandid.isPresent()) {
				m.addAttribute("brandid", brandid.get());
				list = pro_service.findAllBrand("%" + kwords + "%", brandid.get(), pageable);
			} else {
				m.addAttribute("cateid", null);
				m.addAttribute("colorid", null);
				m.addAttribute("sizeid", null);
				m.addAttribute("brandid", null);
				list = pro_service.findAll("%" + kwords + "%", pageable);
			}
		} catch (Exception ex) {
			return "redirect:/shop.html";
		}
		m.addAttribute("products", list);

		if (ac != null) {
			m.addAttribute("check", fav_service);
		} else {
			m.addAttribute("check", null);
		}

		session.set("uri", req.getRequestURI());

		return path + "products/shop";
	}

//	Trang chi tiết sản phẩm
	@SuppressWarnings("deprecation")
	@RequestMapping("/detail.html")
	public String productDetailPage(Model m, @RequestParam("id") String id) {
		m.addAttribute("hhh", true);

		Accounts ac = (Accounts) session.get("account");
		if (!NumberUtils.isNumber(id)) {
			return "redirect:/pagenotfound.html";
		}
		Products product = pro_service.findById(Integer.parseInt(id));

		if (product == null || product.getAvaliable() == false) {
			return "redirect:/pagenotfound.html";
		}
		m.addAttribute("item", product);

		List<ProductReviews> productreviews = proReviewDao.findAllByPro(Integer.parseInt(id));
		if (ac != null) {
			List<Orders> order = orderDAO.find_by_danhan(ac.getUsername());
			for (int i = 0; i < order.size(); i++) {
				List<OrderDetails> listOD = order.get(i).getOrderdetail();
				for (int e = 0; e < listOD.size(); e++) {
					ProductDetails proD = listOD.get(e).getProductsId();
					if (proD.getProductid().getId() == Integer.parseInt(id)) {
						if (productreviews.size() > 0) {
							for (int j = 0; j < productreviews.size(); j++) {
								if (!productreviews.get(j).getUsername().getUsername().equals(ac.getUsername())) {
									m.addAttribute("activeRw", "add");
								} else {
									m.addAttribute("activeRw", "update");
								}
							}
						} else {
							m.addAttribute("activeRw", "add");
						}
					}

				}
			}
		}
		m.addAttribute("product_rw", productreviews);

		List<ColorDetailModel> listColor = proDetailDao.getColorPD(Integer.parseInt(id)).get();
		m.addAttribute("listColorPD", listColor);

		List<SizeDetailModel> listSize = proDetailDao.getSizePD(Integer.parseInt(id)).get();
		m.addAttribute("listSizePD", listSize);

		if (ac != null) {
			m.addAttribute("check", fav_service);
			m.addAttribute("favcheck", fav_service.checkProFav(ac, product));
		} else {
			m.addAttribute("check", null);
			m.addAttribute("favcheck", false);
		}

		m.addAttribute("products2",
				pro_service.findAllRecom(Integer.parseInt(id), product.getCatePro().getId(), PageRequest.of(0, 8)));
		session.set("uri", req.getRequestURI());
		session.set("idd", Integer.valueOf(id));

		return path + "products/detail";
	}

	@GetMapping("/getProductDetail/{id}")
	@ResponseBody
	public ResponseEntity<List<ProductDetails>> getPro(@PathVariable("id") String id) {
		Accounts ac = (Accounts) session.get("account");
		if (!NumberUtils.isParsable(id)) {
			return ResponseEntity.noContent().build();
		}
		Optional<List<ProductDetails>> list = proDetailDao.getListProDetails(Integer.parseInt(id));
		if (!list.isPresent()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(list.get());
	}

	@GetMapping("/getCounrPro")
	@ResponseBody
	public ResponseEntity<Integer> getCountPro(@RequestParam("id") String id) {
		Accounts ac = (Accounts) session.get("account");
		int count = 1;
		if (!NumberUtils.isParsable(id)) {
			return ResponseEntity.notFound().build();
		}
		Optional<ProductDetails> productDetail = proDetailDao.findById(Integer.parseInt(id));
		if (!productDetail.isPresent()) {
			return ResponseEntity.ok(Integer.parseInt(id));
		}
		count = productDetail.get().getAmount();
		return ResponseEntity.ok(count);
	}

//	Trang sản phẩm khuyến mãi
	@RequestMapping("/sales.html")
	public String salePage(Model m, @RequestParam("field") Optional<String> field,
			@RequestParam("show") Optional<Integer> show, @RequestParam("sort") Optional<Integer> criteria,
			@RequestParam("p") Optional<Integer> p, @RequestParam("cateid") Optional<Integer> cateid,
			@RequestParam("colorid") Optional<String> colorid, @RequestParam("sizeid") Optional<String> sizeid,
			@RequestParam("brandid") Optional<Integer> brandid) {
		Accounts ac = (Accounts) session.get("account");
		m.addAttribute("hhh", true);

		int s = criteria.orElse(0);
		int paging = p.orElse(0);
		int shows = show.orElse(9);

		Sort sort;
		Pageable pageable;

		if (s == 0) {
			sort = Sort.by(Direction.ASC, field.orElse("id"));
		} else {
			sort = Sort.by(Direction.DESC, field.orElse("id"));
		}

		m.addAttribute("sort", s);
		m.addAttribute("field2", field.orElse("id"));
		m.addAttribute("show", shows);

		m.addAttribute("number", paging);

		pageable = PageRequest.of(paging, shows, sort);
		Page<Products> list = null;
		try {
			if (cateid.isPresent()) {
				m.addAttribute("cateid", cate_service.findById(cateid.get()));
				list = pro_service.findAllSaleCate("%" + " " + "%", cateid.get(), pageable);
			} else if (colorid.isPresent()) {
				m.addAttribute("colorid", colorid.get());
				list = pro_service.findAllColor2("%" + " " + "%", colorid.get(), pageable);
			} else if (sizeid.isPresent()) {
				m.addAttribute("sizeid", sizeid.get());
				list = pro_service.findAllSize2("%" + " " + "%", sizeid.get(), pageable);
			} else if (brandid.isPresent()) {
				m.addAttribute("brandid", brandid.get());
				list = pro_service.findAllBrand2("%" + " " + "%", brandid.get(), pageable);
			} else {
				m.addAttribute("cateid", null);
				m.addAttribute("colorid", null);
				m.addAttribute("sizeid", null);
				m.addAttribute("brandid", null);
				list = pro_service.findSale("%" + " " + "%", pageable);
			}
		} catch (Exception ex) {
			return "redirect:/sales.html";
		}
//	    load list sale product :)
		m.addAttribute("products", list);

		session.set("uri", req.getRequestURI());

		if (ac != null) {
			m.addAttribute("check", fav_service);
		} else {
			m.addAttribute("check", null);
		}

		return path + "products/sales";
	}

	// Đánh giá sản phẩm nhanh
	@PostMapping("/review/create")
	@ResponseBody
	public ResponseEntity<ReviewModel> reviewPage(@RequestBody ReviewModel form) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		Accounts acc = session.get("account");
		if (acc == null) {
			return ResponseEntity.notFound().build();
		}
		if (!NumberUtils.isParsable(form.getProductId())) {
			form.setMessage("Đánh giá thất bại sản phẩm không tồn tại");
			return ResponseEntity.ok(form);
		}
		ProductReviews proReview = new ProductReviews();
		Optional<Products> product = pro_DAO.findById(Integer.parseInt(form.getProductId()));
		if (!product.isPresent()) {
			form.setMessage("Đánh giá thất bại!!! Sản phẩm không tồn tại");
			return ResponseEntity.ok(form);
		}
		List<ProductReviews> listReviews = proReviewDao.findAllByUser(acc.getUsername());
		for (int i = 0; i < listReviews.size(); i++) {
			if (listReviews.get(i).getProductid().getId() == Integer.parseInt(form.getProductId())) {
				form.setMessage("Đánh giá thất bại!!! Bạn đã đánh giá rồi");
				return ResponseEntity.ok(form);
			}
		}
		proReview.setUsername(acc);
		proReview.setStars(form.getStar());
		proReview.setComment(form.getContent());
		proReview.setProductid(product.get());
		proReviewDao.save(proReview);
		return ResponseEntity.ok(form);
	}

	@ModelAttribute("types")
	List<Types> findAllType(Model m) {
		m.addAttribute("count", typ_service);
		return typ_service.findAllInProduct();
	}

	@ModelAttribute("colors")
	List<Colors> findAllColor() {
		return color_service.findInProduct();
	}

	@ModelAttribute("sizes")
	List<Sizes> findAllSize() {
		return size_service.findInProduct();
	}

	@ModelAttribute("brands")
	List<Brands> findAllBrands() {
		return brand_service.findInProduct();
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

	@ModelAttribute("check_count")
	CategoryService catecount() {
		return cate_service;
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
