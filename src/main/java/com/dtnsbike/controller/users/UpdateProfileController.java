package com.dtnsbike.controller.users;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.dao.interfaces.OrderDetailsDAO;
import com.dtnsbike.dao.interfaces.OrdersDAO;
import com.dtnsbike.dao.interfaces.ProductDetailsDAO;
import com.dtnsbike.dao.interfaces.ProductReviewsDAO;
import com.dtnsbike.dao.interfaces.ProductsDAO;
import com.dtnsbike.dao.interfaces.SalesDAO;
import com.dtnsbike.dao.interfaces.StatusDAO;
import com.dtnsbike.dao.service.CartsService;
import com.dtnsbike.dao.service.FavoritesService;
import com.dtnsbike.dao.service.TypesService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Carts;
import com.dtnsbike.entity.OrderDetails;
import com.dtnsbike.entity.Orders;
import com.dtnsbike.entity.ProductDetails;
import com.dtnsbike.entity.ProductReviews;
import com.dtnsbike.entity.Sales;
import com.dtnsbike.entity.Types;
import com.dtnsbike.model.ChangePassForm;
import com.dtnsbike.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
public class UpdateProfileController {

	String path = "users/common/";
	@Autowired
	SessionService session;
	@Autowired
	AccountsDAO accDao;
	@Autowired
	SalesDAO salesDao;
	@Autowired
	OrdersDAO orderdao;
	@Autowired
	ProductDetailsDAO proDdao;
	@Autowired
	StatusDAO statusdao;
	@Autowired
	TypesService typ_service;
	@Autowired
	CartsService cartDAO;
	@Autowired
	ProductReviewsDAO proReviewDao;
	@Autowired
	HttpServletRequest req;
	@Autowired
	AccountsDAO accountsDAO;
	@Autowired
	FavoritesService fav_service;

	@RequestMapping("/update_profile/account")
	public String updateAccountPage(Model model) {
		model.addAttribute("accPage", "active");
		Accounts acc = session.get("account");
		if (acc == null) {
			return "redirect:/index.html?err=notLogin";
		}
		return path + "accounts/update_profile";
	}

	@GetMapping("/update_profile/changePass")
	public String updateChangePasswordPage(Model model, @ModelAttribute("changePassForm") ChangePassForm form) {
		model.addAttribute("changePassPage", "active");
		Accounts accS = session.get("account");
		Optional<Accounts> acc = accDao.findByIdAndActiveTrue(accS.getUsername(), true);
		if (!acc.isPresent()) {
			return "redirect:/index.html?err=notLogin";
		}
		return path + "accounts/update_profile";
	}

	@PostMapping("/update_profile/changePass")
	public String postUpdateChangePasswordPage(Model model,
			@Validated @ModelAttribute("changePassForm") ChangePassForm form, Errors error) {
		model.addAttribute("changePassPage", "active");
		Accounts acc = session.get("account");
		String pass = acc.getPassword();
		String oldPass = form.getPassOld();
		String newPass = form.getPassNew();
		String rePass = form.getPassRe();
		int loi = 0;
		if (error.hasErrors()) {
			loi++;
		}
		if (error.getFieldError("passOld") == null) {
			if (!pass.equals(oldPass)) {
				model.addAttribute("errorPassOld", "Mật khẩu cũ không đúng !!!");
				loi++;
			}

		}
		if (error.getFieldError("passNew") == null && error.getFieldError("passRe") == null) {
			if (!newPass.equals(rePass)) {
				model.addAttribute("errorPassRe", "Nhập lại mật khẩu không đúng !!!");
				loi++;
			}
		}
		if (loi == 0) {
			acc.setPassword(newPass);
			accDao.save(acc);
			model.addAttribute("success", true);
			form.setPassNew(null);
			form.setPassOld(null);
			form.setPassRe(null);
		}
		return path + "accounts/update_profile";
	}

	@RequestMapping("/update_profile/address")
	public String updateAddressPage(Model model) {
		model.addAttribute("addressPage", "active");
		Accounts acc = session.get("account");
		if (acc == null) {
			return "redirect:/index.html?err=notLogin";
		}
		return path + "accounts/update_profile";
	}

	@RequestMapping("/update_profile/orders")
	public String updateOrdersPage(Model model, @RequestParam("p") Optional<String> p,
			@RequestParam("keyword") Optional<String> key) {
		model.addAttribute("orderPage", "active");
		Accounts acc = session.get("account");
		if (acc == null) {
			return "redirect:/index.html?err=notLogin";
		}
		List<ProductReviews> productreviews = proReviewDao.findAllByUser(acc.getUsername());
		List<Orders> order = orderdao.find_by_danhan(acc.getUsername());
		// tất cả đơn hàng
		if (!p.isPresent() || p.get().equals("all")) {
			if (key.isPresent()) {
				List<OrderDetails> list = proDdao.findByKey("%" + key.get() + "%");
				List<Orders> listOrder = new ArrayList<>();
				for (int i = list.size() - 1; i > 0; i--) {
					String use = list.get(i).getOrdersId().getUserOrder().getUsername();
					if (use.equals(acc.getUsername())) {
						listOrder.add(list.get(i).getOrdersId());
					}
				}
				model.addAttribute("order", listOrder);
			} else {
				model.addAttribute("order", orderdao.findByUserOrder(acc));
			}
			model.addAttribute("activeNav", "all");
			return path + "accounts/update_profile";
		}
		// danh sách đơn hàng chờ xác nhận
		if (p.get().equals("confirm")) {
			List<Orders> order_xacnhan = orderdao.find_by_choxacnhan(acc.getUsername());
			model.addAttribute("xacnhan", order_xacnhan);
			model.addAttribute("activeNav", "confirm");
			return path + "accounts/update_profile";
		}
//		if (p.get().equals("receive")) {
//			// danh sách đơn hàng chờ lấy hàng
//			List<Orders> order_layhang = orderdao.find_by_cholayhang(acc.getUsername());
//			model.addAttribute("layhang", order_layhang);
//			model.addAttribute("activeNav", "receive");
//			return path + "accounts/update_profile";
//		}
//		if (p.get().equals("deliver")) {
//			// danh sách đơn hàng đang giao
//			List<Orders> order_danggiao = orderdao.find_by_danggiao(acc.getUsername());
//			model.addAttribute("danggiao", order_danggiao);
//			model.addAttribute("activeNav", "deliver");
//			return path + "accounts/update_profile";
//		}
		if (p.get().equals("delivered")) {
			// danh sách đơn hàng đã giao
			List<Orders> order_dagiao = orderdao.find_by_Dagiao(acc.getUsername());
			model.addAttribute("dagiao", order_dagiao);
			model.addAttribute("activeNav", "delivered");
			return path + "accounts/update_profile";
		}
		if (p.get().equals("cancel")) {
			// danh sách đơn hàng đã hủy
			List<Orders> order_dahuy = orderdao.find_by_huy(acc.getUsername());
			model.addAttribute("dahuy", order_dahuy);
			model.addAttribute("activeNav", "cancel");
			return path + "accounts/update_profile";
		}
		return path + "accounts/update_profile";

	}

	@GetMapping("/update_profile/orders/cancel/{id}")
	public String ordersCancel(Model m, @PathVariable("id") String id, @RequestParam("p") Optional<String> p) {
		Accounts acc = session.get("account");
		if (acc == null) {
			return "redirect:/pageNotFound.html";
		}
		if (!NumberUtils.isParsable(id)) {
			return "redirect:/update_profile/orders?err=orderNotPresent";
		}
		Optional<Orders> order = orderdao.findById(Integer.parseInt(id));

		if (!order.isPresent() || !order.get().getStatusId().getId().equals("cxn")) {
			return "redirect:/update_profile/orders?err=canNotCancelOrder";
		}
		for (int i = 0; i < order.get().getOrderdetail().size(); i++) {
			ProductDetails productDetail = order.get().getOrderdetail().get(i).getProductsId();
			productDetail.setAmount(productDetail.getAmount() + order.get().getOrderdetail().get(i).getAmount());
			proDdao.save(productDetail);
		}
		if (order.get().getSaleId() != null) {
			Sales sale = order.get().getSaleId();
			Date toDay = new Date();
			if (sale.getStartdate().before(toDay)) {
				if (sale.getEnddate().after(toDay)) {
					sale.setAmount(sale.getAmount() + 1);
					salesDao.save(sale);
				}
			}
		}
		order.get().setStatusId(statusdao.findById("dh").get());
		orderdao.save(order.get());
		if (!p.isPresent() || p.get().equals("all")) {
			return "redirect:/update_profile/orders?p=all";
		}
		if (p.get().equals("confirm")) {
			return "redirect:/update_profile/orders?p=confirm";
		}
		if (p.get().equals("receive")) {
			return "redirect:/update_profile/orders?p=receive";
		}
		return "redirect:/update_profile/orders";
	}

	@GetMapping("/update_profile/orders/accept/{id}")
	public String ordersSuccess(Model m, @PathVariable("id") String id, @RequestParam("p") Optional<String> p) {
		Accounts acc = session.get("account");
		if (acc == null) {
			return "redirect:/pageNotFound.html";
		}
		if (!NumberUtils.isParsable(id)) {
			return "redirect:/update_profile/orders?err=orderNotPresent";
		}
		Optional<Orders> order = orderdao.findById(Integer.parseInt(id));

		if (!order.isPresent() || !order.get().getStatusId().getId().equals("dagi")) {
			return "redirect:/update_profile/orders?err=canNotAcceptOrder";
		}
		order.get().setStatusId(statusdao.findById("dnh").get());
		orderdao.save(order.get());
		if (!p.isPresent() || p.get().equals("all")) {
			return "redirect:/update_profile/orders?p=all";
		}
		if (p.get().equals("confirm")) {
			return "redirect:/update_profile/orders?p=confirm";
		}
		if (p.get().equals("delivered")) {
			return "redirect:/update_profile/orders?p=delivered";
		}
		return "redirect:/update_profile/orders";
	}

	@GetMapping("/update_profile/orders/rebuy/{id}")
	public String reBuy(Model m, @PathVariable("id") String id, @RequestParam("p") Optional<String> p) {
		Accounts acc = session.get("account");
		String err = null;
		if (acc == null) {
			return "redirect:/pageNotFound.html";
		}
		if (!NumberUtils.isParsable(id)) {
			return "redirect:/update_profile/orders?err=orderNotPresent";
		}
		Optional<Orders> order = orderdao.findById(Integer.parseInt(id));

		if (!order.isPresent() || (!order.get().getStatusId().getId().equals("dnh")
				&& !order.get().getStatusId().getId().equals("dh"))) {
			return "redirect:/update_profile/orders?err=canNotBuyAgain";
		}
		for (int i = 0; i < order.get().getOrderdetail().size(); i++) {
			ProductDetails proDetail = order.get().getOrderdetail().get(i).getProductsId();
			String name = System.currentTimeMillis() + "";
			String cartName = acc.getUsername() + Integer.toHexString(name.hashCode());
			Carts carth = cartDAO.findProductDetailId(proDetail.getId(), acc.getUsername());
			if (proDetail.getAmount() > 0) {
				if (proDetail.getProductid().getAvaliable() == true) {
					if (carth == null) {
						Carts cart = new Carts();
						cart.setId(cartName);
						cart.setQty(1);
						cart.setProCart(proDetail);
						cart.setUserCart(acc);
						carth = cart;
						cartDAO.add(cart);
					} else {
						carth.setQty(carth.getQty() + 1);
						cartDAO.add(carth);
					}
				} else {
					err = "?err=ProductIsNotSell";
				}
			} else {
				err = "?err=haveProductOutOfStock";
			}
		}
		if (err != null) {
			return "redirect:/cart.html" + err;
		}
		return "redirect:/cart.html";
	}

	@RequestMapping("/update_profile/voucher")
	public String updateVoucherPage(Model model) {
		List<Sales> list = salesDao.findAllActive();
		Accounts acc = session.get("account");
		if (acc == null) {
			return "redirect:/index.html?err=notLogin";
		}
		model.addAttribute("voucherPage", "active");
		model.addAttribute("voucherList", list);
		return path + "accounts/update_profile";
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

	@ModelAttribute("favcount")
	Integer favcount() {
		Accounts ac = (Accounts) session.get("account");
		return fav_service.findAllUser(ac).size();
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
