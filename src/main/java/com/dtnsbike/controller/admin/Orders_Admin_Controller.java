package com.dtnsbike.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.dao.service.OrderDetailsService;
import com.dtnsbike.dao.service.OrdersService;
import com.dtnsbike.dao.service.ProductDetailsService;
import com.dtnsbike.dao.service.SalesService;
import com.dtnsbike.dao.service.StatusService;
import com.dtnsbike.dao.service.WarrantiesService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.OrderDetails;
import com.dtnsbike.entity.Orders;
import com.dtnsbike.entity.ProductDetails;
import com.dtnsbike.entity.Sales;
import com.dtnsbike.entity.Warranties;
import com.dtnsbike.service.ConvertDateService;
import com.dtnsbike.service.ConvertPageService;
import com.dtnsbike.service.GetContentJsonService;
import com.dtnsbike.service.MailerService;
import com.dtnsbike.service.RestApiService;
import com.dtnsbike.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/admin")
public class Orders_Admin_Controller {
	String path = "admin/common/orders/";

	@Autowired
	OrdersService orderService;

	@Autowired
	SalesService saleService;

	@Autowired
	ProductDetailsService prod_service;

	@Autowired
	WarrantiesService warrService;

	@Autowired
	MailerService mailerService;

	@Autowired
	StatusService statusService;

	@Autowired
	OrderDetailsService orderdService;

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

//	Danh sách đơn hàng

	int check = 0, checks = 0;
	int check2 = 0;
	String name = null;

	@RequestMapping("/orders-list/{listid}")
	public String getList(Model m, @PathVariable("listid") String listid, HttpServletRequest request,
			@RequestParam("page") Optional<String> pages)
			throws JsonMappingException, JsonProcessingException, IOException {

		session.remove("check");
		session.remove("checks");
		if (listid.equals("all-list")) {
			// All Order
			getAllOrder(m, pages);
			m.addAttribute("listid", listid);

		} else if (listid.equals("wait-list")) {
			// Wait Order
			getWaitOrder(m, pages);
			m.addAttribute("listid", listid);
		} else if (listid.equals("shipped-list")) {
			// Shipped Order
			getShippOrder(m, pages);
			m.addAttribute("listid", listid);

		} else if (listid.equals("waitconfirm-list")) {
			// Waitconfirm order

			getWaitConfirmOrder(m, pages);
			m.addAttribute("listid", listid);

		} else if (listid.equals("done-list")) {
			// Done order
			getDoneOrder(m, pages);
			m.addAttribute("listid", listid);

		} else if (listid.equals("received-list")) {
			// Done order
			getrReceivedOrder(m, pages);
			m.addAttribute("listid", listid);

		} else if (listid.equals("cancel-list")) {
			// Cancel order
			getCancelOrder(m, pages);
			m.addAttribute("listid", listid);
		} else {
			return "redirect:/pagenotfound.html";
		}

		session.set("test", listid);

		return path + "orders-list.html";
	}

//	Danh sách tất cả đơn hàng 
	public void getAllOrder(Model m, Optional<String> pages)
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
		TypeReference<List<Orders>> type = new TypeReference<List<Orders>>() {
		};
		List<Orders> list = mapper.readValue(api.get("/DTNsBike/rest/orders").toString(), type);
		if (pageService.checkTotalPages(list, page, size) == false) {
			page = 1;
		}

		Pageable pageable = PageRequest.of((page - 1), size);
		Integer totalPage = list.size() / size;
		if (list.size() % size > 0) {
			totalPage = totalPage + 1;
		}

		@SuppressWarnings("unchecked")
		List<Orders> orders = (List<Orders>) pageService.toPage(list, pageable).getContent();
		m.addAttribute("listOrders", orders);

		m.addAttribute("listPage", pageService.listPage(list, page, size));
		m.addAttribute("page", page);

		m.addAttribute("size", size);
		m.addAttribute("totalPage", totalPage.intValue());
		m.addAttribute("totalItems", list.size());
		m.addAttribute("pageableItems", pageable);

		// Order set :)
		m.addAttribute("odd", orderdService);
		m.addAttribute("warran", warrService);

		session.remove("check");
		session.remove("checks");
		session.remove("checks2");

		if (check2 > 0) {
			session.set("checks2", true);
			check2 = 0;
		} else {
			session.remove("checks2");
		}

		if (check > 0) {
			session.set("check", true);
			check = 0;
		} else {
			session.remove("check");
		}

		if (checks > 0) {
			session.set("checks", true);
			checks = 0;
		} else {
			session.remove("checks");
		}

	}

//	Danh sách đơn hàng đang chờ
	public void getWaitOrder(Model m, Optional<String> pages)
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
		TypeReference<List<Orders>> type = new TypeReference<List<Orders>>() {
		};
		List<Orders> list = mapper.readValue(api.get("/DTNsBike/rest/orderss/clh").toString(), type);
		if (pageService.checkTotalPages(list, page, size) == false) {
			page = 1;
		}

		Pageable pageable = PageRequest.of((page - 1), size);
		Integer totalPage = list.size() / size;
		if (list.size() % size > 0) {
			totalPage = totalPage + 1;
		}

		@SuppressWarnings("unchecked")
		List<Orders> orders = (List<Orders>) pageService.toPage(list, pageable).getContent();
		m.addAttribute("listOrders", orders);

		m.addAttribute("listPage", pageService.listPage(list, page, size));
		m.addAttribute("page", page);

		m.addAttribute("size", size);
		m.addAttribute("totalPage", totalPage.intValue());
		m.addAttribute("totalItems", list.size());
		m.addAttribute("pageableItems", pageable);

		// Order set :)
		m.addAttribute("odd", orderdService);
		m.addAttribute("warran", warrService);

		session.remove("check");
		session.remove("checks");

		if (check > 0) {
			session.set("check", true);
			check = 0;
		} else {
			session.remove("check");
		}

		if (checks > 0) {
			session.set("checks", true);
			checks = 0;
		} else {
			session.remove("checks");
		}
	}

//	Danh sách đơn hàng đang giao
	public void getShippOrder(Model m, Optional<String> pages)
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
		TypeReference<List<Orders>> type = new TypeReference<List<Orders>>() {
		};
		List<Orders> list = mapper.readValue(api.get("/DTNsBike/rest/orderss/dangi").toString(), type);
		if (pageService.checkTotalPages(list, page, size) == false) {
			page = 1;
		}

		Pageable pageable = PageRequest.of((page - 1), size);
		Integer totalPage = list.size() / size;
		if (list.size() % size > 0) {
			totalPage = totalPage + 1;
		}

		@SuppressWarnings("unchecked")
		List<Orders> orders = (List<Orders>) pageService.toPage(list, pageable).getContent();
		m.addAttribute("listOrders", orders);

		m.addAttribute("listPage", pageService.listPage(list, page, size));
		m.addAttribute("page", page);

		m.addAttribute("size", size);
		m.addAttribute("totalPage", totalPage.intValue());
		m.addAttribute("totalItems", list.size());
		m.addAttribute("pageableItems", pageable);

		// Order set :)
		m.addAttribute("odd", orderdService);
		m.addAttribute("warran", warrService);

		session.remove("check");
		session.remove("checks");

		if (check > 0) {
			session.set("check", true);
			check = 0;
		} else {
			session.remove("check");
		}

		if (checks > 0) {
			session.set("checks", true);
			checks = 0;
		} else {
			session.remove("checks");
		}
	}

//	Danh sách đơn hàng chờ xác nhận
	public void getWaitConfirmOrder(Model m, Optional<String> pages)
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
		TypeReference<List<Orders>> type = new TypeReference<List<Orders>>() {
		};
		List<Orders> list = mapper.readValue(api.get("/DTNsBike/rest/orderss/cxn").toString(), type);
		if (pageService.checkTotalPages(list, page, size) == false) {
			page = 1;
		}

		Pageable pageable = PageRequest.of((page - 1), size);
		Integer totalPage = list.size() / size;
		if (list.size() % size > 0) {
			totalPage = totalPage + 1;
		}

		@SuppressWarnings("unchecked")
		List<Orders> orders = (List<Orders>) pageService.toPage(list, pageable).getContent();
		m.addAttribute("listOrders", orders);

		m.addAttribute("listPage", pageService.listPage(list, page, size));
		m.addAttribute("page", page);

		m.addAttribute("size", size);
		m.addAttribute("totalPage", totalPage.intValue());
		m.addAttribute("totalItems", list.size());
		m.addAttribute("pageableItems", pageable);

		// Order set :)
		m.addAttribute("odd", orderdService);
		m.addAttribute("warran", warrService);

		session.remove("check");
		session.remove("checks");
		session.remove("checks2");

		if (check2 > 0) {
			session.set("checks2", true);
			check2 = 0;
		} else {
			session.remove("checks2");
		}

		if (check > 0) {
			session.set("check", true);
			check = 0;
		} else {
			session.remove("check");
		}

		if (checks > 0) {
			session.set("checks", true);
			checks = 0;
		} else {
			session.remove("checks");
		}
	}

//	Danh sách đơn hàng đã giao
	public void getDoneOrder(Model m, Optional<String> pages)
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
		TypeReference<List<Orders>> type = new TypeReference<List<Orders>>() {
		};
		List<Orders> list = mapper.readValue(api.get("/DTNsBike/rest/orderss/dagi").toString(), type);
		if (pageService.checkTotalPages(list, page, size) == false) {
			page = 1;
		}

		Pageable pageable = PageRequest.of((page - 1), size);
		Integer totalPage = list.size() / size;
		if (list.size() % size > 0) {
			totalPage = totalPage + 1;
		}

		@SuppressWarnings("unchecked")
		List<Orders> orders = (List<Orders>) pageService.toPage(list, pageable).getContent();
		m.addAttribute("listOrders", orders);

		m.addAttribute("listPage", pageService.listPage(list, page, size));
		m.addAttribute("page", page);

		m.addAttribute("size", size);
		m.addAttribute("totalPage", totalPage.intValue());
		m.addAttribute("totalItems", list.size());
		m.addAttribute("pageableItems", pageable);

		// Order set :)
		m.addAttribute("odd", orderdService);
		m.addAttribute("warran", warrService);

		session.remove("check");
		session.remove("checks");

		if (check > 0) {
			session.set("check", true);
			check = 0;
		} else {
			session.remove("check");
		}

		if (checks > 0) {
			session.set("checks", true);
			checks = 0;
		} else {
			session.remove("checks");
		}
	}

//	Danh sách đơn hàng đang chờ
	public void getCancelOrder(Model m, Optional<String> pages)
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
		TypeReference<List<Orders>> type = new TypeReference<List<Orders>>() {
		};
		List<Orders> list = mapper.readValue(api.get("/DTNsBike/rest/orderss/dh").toString(), type);
		if (pageService.checkTotalPages(list, page, size) == false) {
			page = 1;
		}

		Pageable pageable = PageRequest.of((page - 1), size);
		Integer totalPage = list.size() / size;
		if (list.size() % size > 0) {
			totalPage = totalPage + 1;
		}

		@SuppressWarnings("unchecked")
		List<Orders> orders = (List<Orders>) pageService.toPage(list, pageable).getContent();
		m.addAttribute("listOrders", orders);

		m.addAttribute("listPage", pageService.listPage(list, page, size));
		m.addAttribute("page", page);

		m.addAttribute("size", size);
		m.addAttribute("totalPage", totalPage.intValue());
		m.addAttribute("totalItems", list.size());
		m.addAttribute("pageableItems", pageable);

		// Order set :)
		m.addAttribute("odd", orderdService);
		m.addAttribute("warran", warrService);

		session.remove("check");
		session.remove("checks");

		if (check > 0) {
			session.set("check", true);
			check = 0;
		} else {
			session.remove("check");
		}

		if (checks > 0) {
			session.set("checks", true);
			checks = 0;
		} else {
			session.remove("checks");
		}
	}

// Danh sách đã nhận hàng
	public void getrReceivedOrder(Model m, Optional<String> pages)
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
		TypeReference<List<Orders>> type = new TypeReference<List<Orders>>() {
		};
		List<Orders> list = mapper.readValue(api.get("/DTNsBike/rest/orderss/dnh").toString(), type);
		if (pageService.checkTotalPages(list, page, size) == false) {
			page = 1;
		}

		Pageable pageable = PageRequest.of((page - 1), size);
		Integer totalPage = list.size() / size;
		if (list.size() % size > 0) {
			totalPage = totalPage + 1;
		}

		@SuppressWarnings("unchecked")
		List<Orders> orders = (List<Orders>) pageService.toPage(list, pageable).getContent();
		m.addAttribute("listOrders", orders);

		m.addAttribute("listPage", pageService.listPage(list, page, size));
		m.addAttribute("page", page);

		m.addAttribute("size", size);
		m.addAttribute("totalPage", totalPage.intValue());
		m.addAttribute("totalItems", list.size());
		m.addAttribute("pageableItems", pageable);

		// Order set :)
		m.addAttribute("odd", orderdService);
		m.addAttribute("warran", warrService);

		session.remove("check");
		session.remove("checks");

		if (check > 0) {
			session.set("check", true);
			check = 0;
		} else {
			session.remove("check");
		}

		if (checks > 0) {
			session.set("checks", true);
			checks = 0;
		} else {
			session.remove("checks");
		}
	}

//	Form cập nhật hủy đơn hàng
	@PostMapping("/orders-update/cancel/{id}")
	public String getFormCancel(@PathVariable("id") Integer id, @RequestParam("message") String message)
			throws JsonMappingException, JsonProcessingException, IOException {

		TypeReference<Orders> type = new TypeReference<Orders>() {
		};

		String path = "/DTNsBike/rest/orders/" + id;

		Orders order = mapper.readValue(api.get(path).toString(), type);

		Accounts account = session.get("account");

		String body = "Lý do hủy đơn: " + message;

		// send email vào hàng chờ ...
		try {
			mailerService.send(order.getUserOrder().getEmail(),
					"Tài khoản " + account.getUsername() + " đã hủy đơn hàng DH" + id + " của bạn", body);
		} catch (MessagingException e) {
			System.out.println("Không gửi được gmail!");
		}
		// Hủy đơn hàng :))
		order.setStatusId(statusService.findById("dh"));
		api.push(path, order);
		List<OrderDetails> list = orderdService.findByOrder(order.getId());
		if (!list.isEmpty()) {
			for (OrderDetails ord : list) {
				ProductDetails prod = prod_service.findById(ord.getProductsId().getId());
				prod.setAmount(prod.getAmount() + ord.getAmount());
				prod_service.update(prod);
			}
		}
		if (order.getSaleId() != null) {
			Sales sale = saleService.findById(order.getSaleId().getId()).get();
			sale.setAmount(sale.getAmount() + 1);
			saleService.update(sale);
		}

		String uri = session.get("test");
		if (uri != null) {
			return "redirect:/admin/orders-list/" + uri;
		}
		return "redirect:/admin/orders-list/all-list";
	}

//	Form cập nhật đơn hàng
	@SuppressWarnings("unlikely-arg-type")
	@PostMapping("/orders-update/{id}")
	public String getFormUpdate(@PathVariable("id") Optional<Integer> id, HttpServletRequest req) {

		List<OrderDetails> o = orderdService.findOne(id.get());
		Orders or = orderService.findById(id.get());
		Accounts acc = session.get("account");

		List<Integer> list = new ArrayList<>();
		List<String> list2 = new ArrayList<>();

		int count = 0;
		int count2 = 0;
		if (or.getStatusId().getId().equals("cxn")) {
			// Update đơn hàng :v
			or.setStatusId(statusService.findById("clh"));

			for (int i = 0; i < o.size(); i++) {

				Integer time = Integer.valueOf(req.getParameter("time" + i));
				String numberFrame = req.getParameter("numberFrame" + i);

				list.add(time);
				try {
					list2.add(numberFrame);
				} catch (Exception e) {
					count++;
					break;
				}

				if (list2.get(i).equals("") || list.get(i).equals("") || list.get(i).equals("0")) {
					count++;
					break;
				} else if (list2.get(i).toString().length() != 6) {
					count++;
					break;
				} else if (warrService.findFrameNumber(list2.get(i).toString()) != null) {
					count2++;
					break;
				} else {
					// Thời gian bảo hành ((: hỏi chấm .
					o.get(i).setWarrantyPeriod(list.get(i));

					// Thêm bảo hành :)) ?

					Warranties warr = new Warranties();
					warr.setId(o.get(i).getId());
					warr.setUserWar(acc);
					warr.setOrderdetailid(o.get(i));
					warr.setFramenumber(list2.get(i));
					warrService.add(warr);

				}
			}
		} else if (or.getStatusId().getId().equals("clh")) {
			or.setStatusId(statusService.findById("dangi"));
			orderService.update(or);
		} else if (or.getStatusId().getId().equals("dangi")) {
			or.setStatusId(statusService.findById("dagi"));
			orderService.update(or);
		}
		name = String.valueOf(id.get());

		session.remove("name");
		session.set("name", name);

		if (count2 > 0 && count == 0) {
			check2++;
			checks = 0;
		} else if (count2 == 0 && count == 0) {
			checks++;
			check = 0;
			check2 = 0;
		}
		if (count > 0 && count2 == 0) {
			check++;
			checks = 0;
		} else if (count2 == 0 && count == 0) {
			checks++;
			check = 0;
			check2 = 0;
		}

		String uri = session.get("test");
		if (uri != null) {
			return "redirect:/admin/orders-list/" + uri;
		}

		return "redirect:/admin/orders-list/all-list";
	}

//	Chi tiết đơn hàng
	@RequestMapping("/orders-details.html")
	public String order_Details() {
		return path + "orders-details.html";
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
