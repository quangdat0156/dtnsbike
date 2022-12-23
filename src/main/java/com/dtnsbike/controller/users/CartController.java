package com.dtnsbike.controller.users;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dtnsbike.config.Config;
import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.dao.interfaces.CartsDAO;
import com.dtnsbike.dao.interfaces.ContactsDAO;
import com.dtnsbike.dao.interfaces.OrderDetailsDAO;
import com.dtnsbike.dao.interfaces.OrdersDAO;
import com.dtnsbike.dao.interfaces.StatusDAO;
import com.dtnsbike.dao.service.CartsService;
import com.dtnsbike.dao.service.FavoritesService;
import com.dtnsbike.dao.service.ProductDetailsService;
import com.dtnsbike.dao.service.SalesService;
import com.dtnsbike.dao.service.TypesService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Carts;
import com.dtnsbike.entity.Contacts;
import com.dtnsbike.entity.OrderDetails;
import com.dtnsbike.entity.Orders;
import com.dtnsbike.entity.ProductDetails;
import com.dtnsbike.entity.Sales;
import com.dtnsbike.entity.Status;
import com.dtnsbike.entity.Types;
import com.dtnsbike.model.CartModel;
import com.dtnsbike.model.CheckoutModel;
import com.dtnsbike.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
public class CartController {

	String path = "users/common/";
	@Autowired
	TypesService typ_service;
	@Autowired
	ProductDetailsService proD_service;
	@Autowired
	CartsService cartDAO;
	@Autowired
	CartsDAO cartSV;
	@Autowired
	SalesService sale_Service;
	@Autowired
	SessionService session;
	@Autowired
	OrdersDAO orderDAO;
	@Autowired
	ContactsDAO contactDAO;
	@Autowired
	OrderDetailsDAO orderDetailsDAO;
	@Autowired
	StatusDAO statusDAO;

	@Autowired
	AccountsDAO accountsDAO;
	@Autowired
	HttpServletRequest req;
	
	@Autowired
	FavoritesService fav_service;

//	Trang giỏ hàng
	@RequestMapping("/cart.html")

	public String salePage(Model model) {
		return path + "cart-checkout/cart";
	}

//  Trang giỏ hàng
	@GetMapping("/cartList")
	@ResponseBody
	public ResponseEntity<List<CartModel>> getCartList() {
		Accounts acc = session.get("account");
		if (acc == null) {
			return ResponseEntity.status(404).build();
		}
		String color = "";
		String size = "";
		List<Carts> list = cartDAO.findAllbyUser(acc.getUsername());
		List<CartModel> listCart = new ArrayList<>();
		if (list == null) {
			return ResponseEntity.noContent().build();
		}
		for (int i = 0; i < list.size(); i++) {
			Double oldPrice = list.get(i).getProCart().getProductid().getPrice();
			Double vat = list.get(i).getProCart().getProductid().getVat();
			Double sale = list.get(i).getProCart().getProductid().getDiscountid().getValue();
			Double price = (oldPrice + (oldPrice * vat / 100));
			CartModel modelCart = new CartModel();
			modelCart.setId(list.get(i).getId());
			modelCart.setPhoto(list.get(i).getProCart().getProductid().getImg());
			modelCart.setName(list.get(i).getProCart().getProductid().getName());
			if (list.get(i).getProCart().getColorid() != null) {
				color = list.get(i).getProCart().getColorid().getName();
			}
			modelCart.setColor(color);
			if (list.get(i).getProCart().getSizeid() != null) {
				size = "Size" + list.get(i).getProCart().getSizeid().getId();
			}
			if (list.get(i).getProCart().getAmount() <= 0) {
				modelCart.setMessage("Sản phẩm đã hết hàng");
			}
			modelCart.setSize(size);
			modelCart.setPrice(price - (price * sale / 100));
			modelCart.setOldPrice(price);
			modelCart.setProductId(list.get(i).getProCart().getId());
			modelCart.setProduct(list.get(i).getProCart().getProductid().getId());
			modelCart.setQty(list.get(i).getQty());
			modelCart.setTotal((price - (price * sale / 100)) * list.get(i).getQty());
			listCart.add(modelCart);
		}
		return ResponseEntity.ok(listCart);
	}

	@GetMapping("/addToCart")
	@ResponseBody
	public ResponseEntity<JsonNode> addToCart(@RequestParam("id") String id, @RequestParam("qty") String qty) {
		Accounts acc = session.get("account");
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		if (acc == null) {
			node.put("url", "login.html");
			return ResponseEntity.status(404).body(node);
		}
		if (!NumberUtils.isParsable(id) || !NumberUtils.isParsable(qty)) {
			return ResponseEntity.notFound().build();
		}
		ProductDetails proDetail = proD_service.findById(Integer.parseInt(id));
		String name = System.currentTimeMillis() + "";
		String cartName = acc.getUsername() + Integer.toHexString(name.hashCode());
		Carts carth = cartDAO.findProductDetailId(Integer.parseInt(id), acc.getUsername());
		if (proDetail.getAmount() == 0) {
			node.put("message", "<p>Sản phẩm này đã hết</p>");
			node.put("icon", "<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i>");
			return ResponseEntity.badRequest().body(node);
		}
		if (carth == null) {
			Carts cart = new Carts();
			cart.setId(cartName);
			int qtyNew = Integer.parseInt(qty);
			if (qtyNew > proDetail.getAmount()) {
				qtyNew = proDetail.getAmount();
			}
			cart.setQty(qtyNew);
			cart.setProCart(proDetail);
			cart.setUserCart(acc);
			carth = cart;
			cartDAO.add(cart);
			node.put("message", "<p>Thêm vào giỏ hàng thành công</p>");
			node.put("icon", "<i class='far fa-check-circle text-success mb-1' style='font-size: 60px;'></i>");
		} else {
			int qtyNew = Integer.parseInt(qty);
			if (qtyNew + carth.getQty() > proDetail.getAmount() && carth.getQty() > 0) {
				node.put("message", "<p>Bạn đã có " + carth.getQty() + " sản phẩm trong giỏ hàng. Không thể thêm "
						+ qtyNew + "sản phẩm vào được nữa</p>");
				node.put("icon", "<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i>");
				return ResponseEntity.badRequest().body(node);
			}
			carth.setQty(qtyNew + carth.getQty());
			cartDAO.add(carth);
			node.put("message", "<p>Thêm vào giỏ hàng thành công</p>");
			node.put("icon", "<i class='far fa-check-circle text-success mb-1' style='font-size: 60px;'></i>");
		}
		return ResponseEntity.ok(node);
	}

	@PutMapping("/updateCart")
	@ResponseBody
	public ResponseEntity<CartModel> putCartList(@RequestBody CartModel cartPut) {
		Accounts acc = session.get("account");
		if (acc == null) {
			return ResponseEntity.status(404).build();
		}
		Carts cart = cartDAO.findById(cartPut.getId());
		if (cart == null) {
			return ResponseEntity.status(404).build();
		} else if (!cart.getUserCart().getUsername().equals(acc.getUsername())) {
			return ResponseEntity.status(404).build();
		} else {
			if (cartPut.getQty() == null) {
				cartPut.setQty(cart.getQty());
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(cartPut);
			}
			int maxQty = cart.getProCart().getAmount();
			int qty = cartPut.getQty();
			if (maxQty == 0) {
				cartPut.setQty(cart.getQty());
				return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(cartPut);
			}
			if (qty > maxQty) {
				cartPut.setQty(maxQty);
				cart.setQty(maxQty);
				cartDAO.update(cart);
				cartPut.setTotal(cart.getQty() * cartPut.getPrice());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cartPut);
			}
			cart.setQty(qty);
			cartDAO.update(cart);
			cartPut.setTotal(cart.getQty() * cartPut.getPrice());

		}
		return ResponseEntity.ok(cartPut);
	}

	@DeleteMapping("/deleteCart/{id}")
	@ResponseBody
	public ResponseEntity<CartModel> deleteCartList(@PathVariable("id") String id) {
		Accounts acc = session.get("account");
		if (acc == null) {
			return ResponseEntity.status(404).build();
		}
		Carts cart = cartDAO.findById(id);
		if (cart == null) {
			return ResponseEntity.status(404).build();
		} else if (!cart.getUserCart().getUsername().equals(acc.getUsername())) {
			return ResponseEntity.status(404).build();
		} else {
			cartDAO.delete(id);
		}
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/voucherAct/{id}")
	@ResponseBody
	public ResponseEntity<JsonNode> checkVoucher(@PathVariable("id") Optional<String> id, HttpServletResponse resp) {
		Accounts acc = session.get("account");
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		if (acc == null) {
			return ResponseEntity.status(404).build();
		}
		List<Carts> cart = cartDAO.findAll();
		if (cart.size() <= 0) {
			return ResponseEntity.status(404).build();
		}
		if (!id.isPresent()) {
			return ResponseEntity.status(404).build();
		}
		Optional<Sales> sale = sale_Service.findById(id.get());
		if (!sale.isPresent()) {
			node.put("message",
					"<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i><p>Mã giảm giá này không tồn tại</p>");
			node.put("value", "");
			return ResponseEntity.ok(node);
		} else {
			if (sale.get().getAmount() <= 0) {
				node.put("message",
						"<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i><p>Số lần dùng voucher đã hết</p>");
				node.put("value", "");
				return ResponseEntity.ok(node);
			} else {
				Date toDay = new Date();
				if (sale.get().getStartdate().after(toDay)) {
					node.put("message",
							"<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i><p>Mã giảm giá này không tồn tại</p>");
					node.put("value", "");
					return ResponseEntity.ok(node);
				}
				if (sale.get().getEnddate().before(toDay)) {
					node.put("message",
							"<i class='far fa-times-circle text-danger mb-1' style='font-size: 60px;'></i><p>Mã giảm giá này đã hết thời hạn sử dụng</p>");
					node.put("value", "");
					return ResponseEntity.ok(node);
				}
			}
		}
		node.put("value", sale.get().getValue());
		return ResponseEntity.ok(node);
	}

//	Trang thanh toán
	@RequestMapping("/checkout.html")
	public String checkoutPage(HttpServletRequest request) {
		Accounts acc = session.get("account");
		if (acc == null) {
			return "redirect:/pageNotFound.html";
		}
		List<Carts> cart = cartDAO.findAllbyUser(acc.getUsername());
		if (cart.size() <= 0) {
			return "redirect:/index.html?err=cartIsNull";
		}
		for (int i = 0; i < cart.size(); i++) {
			if (cart.get(i).getProCart().getAmount() == 0) {
				return "redirect:/cart.html?err=ProductsOutOfStock";
			}
		}
		List<Contacts> adr = contactDAO.findAllByUserAr(acc.getUsername());
		if (adr.size() <= 0) {
			return "redirect:/update_profile/address";
		}
		try {
			// Begin process return from VNPAY
			Map fields = new HashMap();
			for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
				String fieldName = URLEncoder.encode((String) params.nextElement(),
						StandardCharsets.US_ASCII.toString());
				String fieldValue = URLEncoder.encode(request.getParameter(fieldName),
						StandardCharsets.US_ASCII.toString());
				if ((fieldValue != null) && (fieldValue.length() > 0)) {
					fields.put(fieldName, fieldValue);
				}
			}

			String vnp_SecureHash = request.getParameter("vnp_SecureHash");
			if (fields.containsKey("vnp_SecureHashType")) {
				fields.remove("vnp_SecureHashType");
			}
			if (fields.containsKey("vnp_SecureHash")) {
				fields.remove("vnp_SecureHash");
			}

			// Check checksum
			String signValue = Config.hashAllFields(fields);
			if (signValue.equals(vnp_SecureHash)) {

				boolean checkOrderId = true; // vnp_TxnRef exists in your database
				boolean checkAmount = true; // vnp_Amount is valid (Check vnp_Amount VNPAY returns compared to the
				boolean checkOrderStatus = true; // PaymnentStatus = 0 (pending)

				if (checkOrderId) {
					if (checkAmount) {
						if (checkOrderStatus) {
							if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
								CheckoutModel form = session.get("checkoutModel");
								String color = null, size = null;
								Optional<Status> status = statusDAO.findById(form.getStatusId());
								Optional<Contacts> contact = contactDAO.findContactIdByUserAr(
										Integer.parseInt(form.getAddressId()), acc.getUsername());
								Orders order = new Orders();
								order.setUserOrder(acc);
								order.setAddress(contact.get().getAddress());
								order.setFullname(contact.get().getFullname());
								order.setPhone(contact.get().getPhone());
								if (form.getSaleId() != null) {
									Optional<Sales> sale = sale_Service.findById(form.getSaleId());
									if (sale.isPresent()) {
										order.setSaleId(sale.get());
										order.setSalevalue(sale.get().getValue());
										sale.get().setAmount(sale.get().getAmount() - 1);
										sale_Service.update(sale.get());
									}
								} else {
									order.setSalevalue(0);
								}
								if (status.isPresent()) {
									order.setStatusId(status.get());
								} else {
									order.setStatusId(statusDAO.findById("cxn").get());
								}
								Orders orderNew = orderDAO.save(order);
								for (int e = 0; e < form.getCart().size(); e++) {
									OrderDetails orderDetailS = new OrderDetails();
									Carts cartN = cartDAO.findById(form.getCart().get(e).getId());
									ProductDetails proD = proD_service.findById(cartN.getProCart().getId());
									int qty = cartN.getQty();
									if (proD.getAmount() - qty >= 0) {
										proD.setAmount(proD.getAmount() - qty);
										orderDetailS.setAmount(qty);
									}
									orderDetailS
											.setDiscount(cartN.getProCart().getProductid().getDiscountid().getValue());
									Double price = cartN.getProCart().getProductid().getPrice();
									orderDetailS.setPrice(price);
									orderDetailS.setProductsId(cartN.getProCart());
									if (cartN.getProCart().getSizeid() != null) {
										color = cartN.getProCart().getSizeid().getId();
									}
									if (cartN.getProCart().getSizeid() != null) {
										size = cartN.getProCart().getSizeid().getId();
									}
									orderDetailS.setSize(size);
									orderDetailS.setVat(cartN.getProCart().getProductid().getVat());
									orderDetailS
											.setWarrantyPeriod(cartN.getProCart().getProductid().getWarrantyperiod());
									orderDetailS.setOrdersId(orderNew);
									orderDetailsDAO.save(orderDetailS);
									proD_service.update(proD);
									cartDAO.delete(cartN.getId());
								}
								session.remove("checkoutModel");
								return "redirect:/update_profile/orders?p=confirm";
							} else {
								session.remove("checkoutModel");
								return "redirect:/checkout.html?message=cancel";
							}
						} else {
							return "redirect:/checkout.html?err=Order already confirmed";
						}
					} else {
						return "redirect:/checkout.html?err=Invalid Amount";
					}
				} else {
					return "redirect:/checkout.html?err=Order not Found";

				}
			}
		} catch (Exception e) {
			return "redirect:/checkout.html?err=Unknow error";
		}
		return path + "cart-checkout/checkout";
	}

	@RequestMapping("/createCheckOut")
	@ResponseBody
	public ResponseEntity<CheckoutModel> blockCheckout() {

		return ResponseEntity.notFound().build();
	}

	@PostMapping("/createCheckOut")
	@ResponseBody
	public ResponseEntity<JsonNode> checkVoucher(@RequestBody CheckoutModel form) {
		Orders order = new Orders();
		ObjectMapper mapper = new ObjectMapper();
		String color = null, size = null;
		Accounts acc = session.get("account");
		;
		ObjectNode node = mapper.createObjectNode();
		Optional<Status> status = statusDAO.findById(form.getStatusId());
		if (!NumberUtils.isParsable(form.getAddressId())) {
			node.put("message", "Đặt hàng thất bại địa chỉ không tồn tại");
			return ResponseEntity.ok(node);
		}
		Optional<Contacts> contact = contactDAO.findContactIdByUserAr(Integer.parseInt(form.getAddressId()),
				acc.getUsername());
		if (!contact.isPresent()) {
			node.put("message", "Đặt hàng thất bại địa chỉ không tồn tại");
			return ResponseEntity.ok(node);
		}
		if (form.getCart().isEmpty()) {
			node.put("message", "Đặt hàng thất bại giỏ hàng không tồn tại");
			return ResponseEntity.ok(node);
		}
		for (int e = 0; e < form.getCart().size(); e++) {
			Carts cart = cartDAO.findIdbyUser(form.getCart().get(e).getId(), acc.getUsername());
			if (cart == null) {
				node.put("message", "Đặt hàng thất bại giỏ hàng không tồn tại");
				return ResponseEntity.ok(node);
			}
			ProductDetails prd = proD_service.findById(cart.getProCart().getId());
			if (prd == null) {
				node.put("message", "Đặt hàng thất bại một số sản phẩm không tồn tại");
				return ResponseEntity.ok(node);
			}
			if (prd.getAmount() == 0) {
				node.put("message", "Đặt hàng thất bại một số sản phẩm đã hết hàng");
				return ResponseEntity.ok(node);
			}
			int qty = cart.getQty();
			if (prd.getAmount() - qty < 0) {
				node.put("message", "Đặt hàng thất bại một số sản phẩm vượt quá số lượng cho phép");
				node.put("return", "cart.html");
				return ResponseEntity.ok(node);
			}
		}
		order.setUserOrder(acc);
		order.setAddress(contact.get().getAddress());
		order.setFullname(contact.get().getFullname());
		order.setPhone(contact.get().getPhone());
		if (form.getSaleId() != null) {
			Optional<Sales> sale = sale_Service.findById(form.getSaleId());
			if (sale.isPresent()) {
				order.setSaleId(sale.get());
				order.setSalevalue(sale.get().getValue());
				sale.get().setAmount(sale.get().getAmount() - 1);
				sale_Service.update(sale.get());
			}
		} else {
			order.setSalevalue(0);
		}
		if (status.isPresent()) {
			order.setStatusId(status.get());
		} else {
			order.setStatusId(statusDAO.findById("cxn").get());
		}
		Orders orderNew = orderDAO.save(order);
		for (int e = 0; e < form.getCart().size(); e++) {
			OrderDetails orderDetailS = new OrderDetails();
			Carts cart = cartDAO.findById(form.getCart().get(e).getId());
			ProductDetails proD = proD_service.findById(cart.getProCart().getId());
			int qty = cart.getQty();
			if (proD.getAmount() - qty >= 0) {
				proD.setAmount(proD.getAmount() - qty);
				orderDetailS.setAmount(qty);
			} else {
				node.put("message", "Đặt hàng thất bại một số sản phẩm vượt quá số lượng cho phép");
				node.put("return", "cart.html");
				return ResponseEntity.ok(node);
			}
			orderDetailS.setDiscount(cart.getProCart().getProductid().getDiscountid().getValue());
			Double price = cart.getProCart().getProductid().getPrice();
			orderDetailS.setPrice(price);
			orderDetailS.setProductsId(cart.getProCart());
			if (cart.getProCart().getSizeid() != null) {
				color = cart.getProCart().getSizeid().getId();
			}
			if (cart.getProCart().getSizeid() != null) {
				size = cart.getProCart().getSizeid().getId();
			}
			orderDetailS.setSize(size);
			orderDetailS.setVat(cart.getProCart().getProductid().getVat());
			orderDetailS.setWarrantyPeriod(cart.getProCart().getProductid().getWarrantyperiod());
			orderDetailS.setOrdersId(orderNew);
			orderDetailsDAO.save(orderDetailS);
			proD_service.update(proD);
			cartDAO.delete(cart.getId());
		}
		node.put("url", "update_profile/orders?p=confirm");
		return ResponseEntity.ok(node);
	}

	@PostMapping("/checkoutVNPay")
	@ResponseBody
	public ResponseEntity<JsonNode> checkOutVNPAY(HttpServletRequest req, @RequestBody CheckoutModel form)
			throws UnsupportedEncodingException {
		ObjectMapper mapper = new ObjectMapper();
		Accounts acc = session.get("account");
		ObjectNode node = mapper.createObjectNode();
		Optional<Status> status = statusDAO.findById(form.getStatusId());
		if (!NumberUtils.isParsable(form.getAddressId())) {
			node.put("message", "Đặt hàng thất bại địa chỉ không tồn tại");
			return ResponseEntity.ok(node);
		}
		Optional<Contacts> contact = contactDAO.findContactIdByUserAr(Integer.parseInt(form.getAddressId()),
				acc.getUsername());
		if (!contact.isPresent()) {
			node.put("message", "Đặt hàng thất bại địa chỉ không tồn tại");
			return ResponseEntity.ok(node);
		}
		if (form.getCart().isEmpty()) {
			node.put("message", "Đặt hàng thất bại giỏ hàng không tồn tại");
			return ResponseEntity.ok(node);
		}
		for (int e = 0; e < form.getCart().size(); e++) {
			Carts cart = cartDAO.findIdbyUser(form.getCart().get(e).getId(), acc.getUsername());
			if (cart == null) {
				node.put("message", "Đặt hàng thất bại giỏ hàng không tồn tại");
				return ResponseEntity.ok(node);
			}
			ProductDetails prd = proD_service.findById(cart.getProCart().getId());
			if (prd == null) {
				node.put("message", "Đặt hàng thất bại một số sản phẩm không tồn tại");
				return ResponseEntity.ok(node);
			}
			if (prd.getAmount() == 0) {
				node.put("message", "Đặt hàng thất bại một số sản phẩm đã hết hàng");
				return ResponseEntity.ok(node);
			}
			int qty = cart.getQty();
			if (prd.getAmount() - qty < 0) {
				node.put("message", "Đặt hàng thất bại một số sản phẩm vượt quá số lượng cho phép");
				node.put("return", "cart.html");
				return ResponseEntity.ok(node);
			}
		}
		session.set("checkoutModel", form);
		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String vnp_Version = "2.1.0";
		String vnp_Command = "pay";
		String vnp_OrderInfo = "Hóa Đơn thanh toán của " + acc.getUsername() + " " + formatter.format(cld.getTime());
		String orderType = "other";
		String vnp_TxnRef = Config.getRandomNumber(8);
		String vnp_IpAddr = Config.getIpAddress(req);
		String vnp_TmnCode = Config.vnp_TmnCode;

		double amount = Double.parseDouble(req.getParameter("amount")) * 100;
		NumberFormat nf = DecimalFormat.getInstance();
		nf.setMaximumFractionDigits(0);
		String price = nf.format(amount);
		Map vnp_Params = new HashMap<>();
		vnp_Params.put("vnp_Version", vnp_Version); // Phiên bản cũ là 2.0.0, 2.0.1 thay đổi sang 2.1.0
		vnp_Params.put("vnp_Command", vnp_Command);
		vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
		vnp_Params.put("vnp_Amount", String.valueOf(price.replace(".", "")));
		vnp_Params.put("vnp_CurrCode", "VND");
		String bank_code = req.getParameter("bankcode");
		if (bank_code != null && !bank_code.isEmpty()) {
			vnp_Params.put("vnp_BankCode", bank_code);
		}
		vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
		vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
		vnp_Params.put("vnp_OrderType", orderType);

		String locate = req.getParameter("language");
		if (locate != null && !locate.isEmpty()) {
			vnp_Params.put("vnp_Locale", locate);
		} else {
			vnp_Params.put("vnp_Locale", "vn");
		}
		vnp_Params.put("vnp_ReturnUrl", Config.vnp_Returnurl);
		vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

		String vnp_CreateDate = formatter.format(cld.getTime());

		vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

		// Build data to hash and querystring
		List fieldNames = new ArrayList(vnp_Params.keySet());
		Collections.sort(fieldNames);
		StringBuilder hashData = new StringBuilder();
		StringBuilder query = new StringBuilder();
		Iterator itr = fieldNames.iterator();

		// Build data với phiên bản mới 2.1.0:

		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = (String) vnp_Params.get(fieldName);
			if ((fieldValue != null) && (fieldValue.length() > 0)) {
				// Build hash data
				hashData.append(fieldName);
				hashData.append('=');
				hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				// Build query
				query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
				query.append('=');
				query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				if (itr.hasNext()) {
					query.append('&');
					hashData.append('&');
				}
			}
		}
		String queryUrl = query.toString();

		// Tạo vnp_SecureHash và tạo URL chuyển hướng phiên bản mới 2.1.0

		String vnp_SecureHash = Config.hmacSHA512(Config.vnp_HashSecret, hashData.toString());
		queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
		String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;
		node.put("code", "00");
		node.put("message", "success");
		node.put("url", paymentUrl);
		return ResponseEntity.ok(node);
	}

//	@GetMapping("/checkVNPAY")
//	@ResponseBody
//	public ResponseEntity<JsonNode> checkVNPAY(HttpServletRequest req) throws UnsupportedEncodingException {
//		
//return ResponseEntity.noContent().build();
//	}

	public @ModelAttribute("types") List<Types> findAllType() {
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
