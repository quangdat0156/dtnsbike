package com.dtnsbike.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.dao.interfaces.OrderDetailsDAO;
import com.dtnsbike.dao.interfaces.OrdersDAO;
import com.dtnsbike.dao.interfaces.ProductDetailsDAO;
import com.dtnsbike.dao.interfaces.ProductsDAO;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Orders;
import com.dtnsbike.entity.ReportProducts;
import com.dtnsbike.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
public class AdminController {

	String path = "admin/common/";

	@Autowired
	AccountsDAO accDAO;

	@Autowired
	ProductsDAO productDAO;

	@Autowired
	OrdersDAO orderDAO;

	@Autowired
	ProductDetailsDAO productdetailDAO;

	@Autowired
	OrderDetailsDAO orderdetailDAO;

	@Autowired
	HttpServletRequest request;
	
	@Autowired
	SessionService session;

//	Index Home Page aka Trang chủ
	@RequestMapping("/admin")
	public String indexAdmin() {
		return "redirect:/admin/index.html";
	}

	@RequestMapping("/admin/index.html")
	public String index(Model m) throws JSONException {

		Integer countAcc = accDAO.countAcc();
		m.addAttribute("countacc", countAcc);

		m.addAttribute("top3Acc", accDAO.getListTop());

		Integer countProduct = productDAO.countProduct();
		m.addAttribute("countprod", countProduct);

		Integer countOrder = orderDAO.countOrder();
		m.addAttribute("countorder", countOrder);

		Integer countProduct_saphethang = productdetailDAO.countProduct_Saphethang();
		m.addAttribute("countprod_saphethang", countProduct_saphethang);

		List<Orders> top3_cxn = orderDAO.Order_top3_cxn();
		if(top3_cxn!=null) {
			m.addAttribute("top3_cxn", top3_cxn);
		}

		JSONObject obj = new JSONObject();
		JSONObject obj2 = new JSONObject();
		JSONObject obj3 = new JSONObject();
		JSONObject obj4 = new JSONObject();

		for (int i = 1; i < 7; i++) {
			obj.put(String.valueOf(i), "Hello " + i);// orderDAO.report1(i).size()
			obj2.put(String.valueOf(i), orderDAO.report2(i).size());
			obj3.put(String.valueOf(i), orderDAO.report3(i));
			obj4.put(String.valueOf(i), orderDAO.report4(i));
		}

		m.addAttribute("report3", obj3);
		m.addAttribute("report4", obj4);

		// REport 1
		Object[][] data = null;
		List<Object[]> dd = new ArrayList<>();
		List<Object[]> listt2 = orderdetailDAO.report2();
		if (!listt2.isEmpty()) {
			Object[] fi = { "T", "test" };
			dd.add(fi);
			for (int i = 0; i < listt2.size(); i++) {
				Object[] dd1 = new Object[] { listt2.get(i)[1].toString(),
						Integer.valueOf(listt2.get(i)[2].toString()) };
				dd.add(dd1);
			}
			data = new Object[][] { dd.toArray() };
			m.addAttribute("report", data);
		}
		// Report 2
		Object[][] data2 = null;
		List<Object[]> dd2 = new ArrayList<>();
		List<Object[]> listt1 = orderdetailDAO.report3();
		if (!listt1.isEmpty()) {
			Object[] fi = { "Trạng thái đơn hàng", "Doanh thu chưa tính thuế giảm giá", "Doanh Thu" };
			dd2.add(fi);
			for (int i = 0; i < listt1.size(); i++) {
				Object[] dd1 = new Object[] { listt1.get(i)[1].toString(), Double.valueOf(listt1.get(i)[2].toString()),
						Double.valueOf(listt1.get(i)[3].toString()) };
				dd2.add(dd1);
			}
			data = new Object[][] { dd2.toArray() };
			m.addAttribute("report2", data);
		}

//		Accounts acc = accDAO.findById(request.getUserPrincipal().getName()).get();
//		m.addAttribute("check_role",accDAO.check(acc.getUsername()) != null);
		return "admin/index";
	}

//	Index Home Page aka Trang chủ 2
	@RequestMapping("/admin/index2.html")
	public String index2() {
		return path + "dashboard/index2";
	}

	@ModelAttribute("accountProfile")
	public Accounts getListSizes() throws JsonMappingException, JsonProcessingException, IOException {
		return accDAO.findById(request.getUserPrincipal().getName()).get();
	}
	
	@ModelAttribute("check_role")
	Boolean checkRole() {
		Accounts acc = accDAO.findById(request.getUserPrincipal().getName()).get();
		return accDAO.check(acc.getUsername()) != null;
	}

}
