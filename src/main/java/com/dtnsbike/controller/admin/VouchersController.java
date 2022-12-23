package com.dtnsbike.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
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
import com.dtnsbike.dao.service.SalesService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Sales;
import com.dtnsbike.service.ConvertDateService;
import com.dtnsbike.service.ConvertPageService;
import com.dtnsbike.service.RestApiService;
import com.dtnsbike.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/admin")
public class VouchersController {
	String path = "admin/common/vouchers/";

	@Autowired
	RestApiService api;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	ConvertPageService pageService;

	@Autowired
	SalesService salesservice;

	@Autowired
	SessionService session;

	@Autowired
	HttpServletRequest request;

	@Autowired
	ConvertDateService dateService;
	
	@Autowired
	AccountsDAO accountDao;

	int remove = 0, re = 0;
	String name = null;

//	Danh sách mã giảm giá
	@RequestMapping("/voucher-list.html")
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
		TypeReference<List<Sales>> typeBrand = new TypeReference<List<Sales>>() {
		};
		List<Sales> listSales = mapper.readValue(api.get("/DTNsBike/rest/sales").toString(), typeBrand);
		if (pageService.checkTotalPages(listSales, page, size) == false) {
			page = 1;
		}

		Pageable pageable = PageRequest.of((page - 1), size);
		Integer totalPage = listSales.size() / size;
		if (listSales.size() % size > 0) {
			totalPage = totalPage + 1;
		}

		@SuppressWarnings("unchecked")
		List<Sales> Sales = (List<Sales>) pageService.toPage(listSales, pageable).getContent();
		m.addAttribute("listSales", Sales);

		m.addAttribute("listPage", pageService.listPage(listSales, page, size));
		m.addAttribute("page", page);

		m.addAttribute("size", size);
		m.addAttribute("totalPage", totalPage.intValue());
		m.addAttribute("totalItems", listSales.size());
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
		session.set("nameVoucher", name);

		return path + "voucher_list.html";
	}

	int i = 0, i2 = 0;

//	Form cập nhật mã giảm giá
	@RequestMapping("/voucher-update")
	public String getFormUpdate(Model m, @ModelAttribute("voucherModel") Sales model) {
		session.set("editVoucher", "/admin/voucher-update");

		if (session.get("editVoucherId") != null) {
			m.addAttribute("editVoucherId", session.get("editVoucherId"));
			if (session.get("voucherModel") != null) {
				model = session.get("voucherModel");
				m.addAttribute("voucherModel", model);
			}
		} else {
			model = new Sales();
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

		return path + "voucher-update.html";
	}

//	Form cập nhật edit
	@RequestMapping("/voucher-update/edit")
	public String editForm(Model m, @RequestParam("id") Optional<String> id,
			@ModelAttribute("voucherModel") Sales model)
			throws JsonMappingException, JsonProcessingException, IOException {
		// Get sizes
		TypeReference<Sales> type = new TypeReference<Sales>() {
		};
		String path = "/DTNsBike/rest/sales/" + id.get();
		Sales sale = mapper.readValue(api.get(path).toString(), type);
		// Set Brand model
		model = sale;
		// Set session model
		session.set("voucherModel", model);
		session.set("editVoucherId", id.get());

		return "redirect:/admin/voucher-update";
	}

//	Form update reset form 
	@RequestMapping("/voucher-update/reset")
	public String resetForm(Model m) {

		session.remove("voucherModel");
		session.remove("editVoucherId");
		resetForm();

		return "redirect:/admin/voucher-update";
	}

//	form update thêm 
	@PostMapping("/voucher-update/add")
	public String addForm(Model m, @Valid @ModelAttribute("voucherModel") Sales model, Errors error)
			throws JsonMappingException, JsonProcessingException, IOException {
		// add
		TypeReference<Sales> type = new TypeReference<Sales>() {
		};
		String path = "/DTNsBike/rest/sales";

		Sales sale = new Sales();
		sale = model;
		int chk = 0;
		if (!model.getId().isEmpty()) {
			String id = model.getId();
			Optional<Sales> check = salesservice.findById(id);
			if (check.isPresent()) {
				session.set("voucher_mess", "Mã voucher đã tồn tại !");
				chk++;
			} else {
				session.set("voucher_mess", null);
			}
			// bắt lỗi tiếng việt
			Pattern pattern = Pattern.compile("[^A-Za-z0-9]");
			Matcher matcer = pattern.matcher(model.getId());
			if (matcer.find()) {
				session.set("voucher_mess", "Vui lòng không nhập dấu cách hoặc kí tự đặc biệt!");
				chk++;
			} else {
				sale.setId(model.getId());
				session.set("voucher_mess", null);
			}
		}

		// điều kiện ngày tháng năm
		if (model.getStartdate() != null && model.getEnddate() != null) {
			if (model.getStartdate().getTime() >= model.getEnddate().getTime()) {
				session.set("voucherdate_erros", "Ngày bắt đầu phải trước ngày kết thúc");
				chk++;
			} else {
				session.set("voucherdate_erros", null);
			}
		}

		// điều kiện giá trị và số lượng
		if (model.getAmount() != null && model.getValue() != null && model.getActive() != null) {
			if (model.getAmount() <= 0) {
				session.set("amount_erros", "Số lượng phải lớn hơn 0");
				chk++;
			} else {
				session.set("amount_erros", null);
			}

			if (model.getValue() <= 0 || model.getValue() > 99) {
				session.set("value_erros", "Giá trị giảm giá không hợp lệ");
				chk++;
			} else {
				session.set("value_erros", null);
			}
			if (model.getAmount() > 99) {
				session.set("amount_erros", "Vượt quá sô lượng cho phép !!!");
				chk++;
			} else {
				session.set("amount_erros", null);
			}
		}

		else {
			if (error.hasErrors()) {
				session.set("errorVoucher", error);
				chk++;
			} else {
				session.set("errorVoucher", null);
			}
		}

		if (chk == 0) {
			JsonNode test = api.post(path, sale);
			if (mapper.readValue(test.toString(), type) != null) {
				i2++;
				session.set("errorVoucher", null);
			}
		}

		return "redirect:/admin/voucher-update";
	}

//	form update cập nhật
	@PostMapping("/voucher-update/update")
	public String updateForm(Model m, @Valid @ModelAttribute("voucherModel") Sales model, Errors error)
			throws JsonMappingException, JsonProcessingException, IOException {
		String id = session.get("editVoucherId");

		int loi = 0;
		// điều kiện ngày tháng năm
		if (model.getStartdate() != null && model.getEnddate() != null) {
			if (model.getStartdate().getTime() >= model.getEnddate().getTime()) {
				session.set("voucherdate_erros", "Ngày bắt đầu phải trước ngày kết thúc");
				loi++;
			} else {
				session.set("voucherdate_erros", null);
			}
		}

		// điều kiện giá trị và số lượng
		if (model.getAmount() != null && model.getValue() != null && model.getActive() != null) {
			if (model.getAmount() <= 0) {
				session.set("amount_erros", "Số lượng phải lớn hơn 0");
				loi++;
			} else {
				session.set("amount_erros", null);
			}

			if (model.getValue() <= 0 || model.getValue() > 99) {
				session.set("value_erros", "Giá trị giảm giá không hợp lệ");
				loi++;
			} else {
				session.set("value_erros", null);
			}
			if (model.getAmount() > 99) {
				session.set("amount_erros", "Vượt quá sô lượng cho phép !!!");
				loi++;
			} else {
				session.set("amount_erros", null);
			}
		}

		if (id != null && loi == 0) {
			TypeReference<Sales> type = new TypeReference<Sales>() {
			};
			String path = "/DTNsBike/rest/sales/" + id;

			Sales sale = mapper.readValue(api.get(path).toString(), type);
			sale.setValue(model.getValue());
			sale.setAmount(model.getAmount());
			sale.setDescription(model.getDescription());
			sale.setActive(model.getActive());
			sale.setStartdate(model.getStartdate());
			sale.setEnddate(model.getEnddate());
			int chk = 0;
			session.set("errorVoucher", null);
			if (model.getId().isEmpty()) {
				session.set("errorVoucher", error);
				chk++;
			}
			if (chk == 0) {
				JsonNode test = api.push(path, sale);
				if (mapper.readValue(test.toString(), type) != null) {
					i++;
				}
			}
		}

		return "redirect:/admin/voucher-update/edit?id=" + id;
	}

//	form update xóa voucher
	@RequestMapping("/voucher-update/delete/{id}")
	public String deleteForm(@PathVariable("id") String id, Model m)
			throws JsonMappingException, JsonProcessingException, IOException {
		TypeReference<Boolean> type = new TypeReference<Boolean>() {
		};
		String path = "/DTNsBike/rest/sales/check/" + id;
		String path2 = "/DTNsBike/rest/sales/" + id;

		Boolean check = mapper.readValue(api.get(path).toString(), type);
		name = salesservice.findById(id).get().getId();
		if (check == true) {
			remove++;
			re = 0;
		} else {
			remove = 0;
			re++;
			api.delete(path2);
		}

		return "redirect:/admin/voucher-list.html";
	}

////	Form reset 
	public void resetForm() {
		session.remove("voucherModel");
		session.remove("editVoucherId");
		session.remove("checks");
		session.remove("checks2");
		session.remove("nameVoucher");
		session.remove("voucher_mess");
		session.remove("errorVoucher");
		session.remove("amount_erros");
		session.remove("value_erros");
		session.remove("voucherdate_erros");
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
