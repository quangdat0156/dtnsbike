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
import com.dtnsbike.dao.interfaces.BlogsDAO;
import com.dtnsbike.dao.interfaces.OrderDetailsDAO;
import com.dtnsbike.dao.interfaces.OrdersDAO;
import com.dtnsbike.dao.interfaces.ProductDetailsDAO;
import com.dtnsbike.dao.interfaces.ProductsDAO;
import com.dtnsbike.dao.interfaces.SalesDAO;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.ProductDetails;
import com.dtnsbike.entity.ReportCustomer;
import com.dtnsbike.entity.ReportPro;
import com.dtnsbike.entity.ReportProducts;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
@RequestMapping("/admin")
public class ReportsController {
	String path = "admin/common/reports/";

	@Autowired
	SalesDAO saleDAO;

	@Autowired
	AccountsDAO accDAO;

	@Autowired
	ProductsDAO productDAO;

	@Autowired
	OrdersDAO orderDAO;

	@Autowired
	OrderDetailsDAO orderdetailDAO;

	@Autowired
	BlogsDAO blogDAO;

	@Autowired
	ProductDetailsDAO productdetailDAO;
	
	@Autowired
	AccountsDAO accountDao;
	
	@Autowired
	HttpServletRequest request;

//	Danh sách sản phẩm
	@RequestMapping("/reports.html")
	public String getList(Model m) throws JSONException {
		Integer countSale = saleDAO.countSale();
		m.addAttribute("countsale", countSale);

		Integer countAcc = accDAO.countAcc();
		m.addAttribute("countacc", countAcc);

		Integer countProduct = productDAO.countProduct();
		m.addAttribute("countprod", countProduct);

		Integer countOrder = orderDAO.countOrder();
		m.addAttribute("countorder", countOrder);

		Integer countOrderHUY = orderDAO.countOrder_byHuy();
		m.addAttribute("count_orderhuy", countOrderHUY);

		Integer countBlog = blogDAO.countBlog();
		m.addAttribute("countblog", countBlog);
		// sản phẩm sắp hết hàng
		Integer countProduct_saphethang = productdetailDAO.countProduct_Saphethang();
		m.addAttribute("countprod_saphethang", countProduct_saphethang);
		// top 3 sản phẩm bán chạy
		List<ReportPro> top3prod = orderdetailDAO.findTop3Pro();
		m.addAttribute("top3", top3prod);
		// sản phẩm đã hết hàng
		List<ProductDetails> hethang = productdetailDAO.Prod_hethang();
		m.addAttribute("hethang", hethang);

		JSONObject obj = new JSONObject();
		JSONObject obj2 = new JSONObject();
		JSONObject obj3 = new JSONObject();
		JSONObject obj4 = new JSONObject();

		for (int i = 1; i < 12; i++) {
			obj.put(String.valueOf(i), orderDAO.report1(i).size());
			obj2.put(String.valueOf(i), orderDAO.report2(i).size());
			obj3.put(String.valueOf(i), orderDAO.report3(i));
			obj4.put(String.valueOf(i), orderDAO.report4(i));
		}

		m.addAttribute("report", obj);
		m.addAttribute("report2", obj2);
		m.addAttribute("report3", obj3);
		m.addAttribute("report4", obj4);

		m.addAttribute("total", orderdetailDAO.getSum2());

		// TK Theo khách hàng
		List<ReportCustomer> reportCus = new ArrayList<>();
		List<Object[]> listt = orderdetailDAO.report1();
		if (!listt.isEmpty()) {
			for (int i = 0; i < listt.size(); i++) {

				ReportCustomer cus = new ReportCustomer();
				cus.setId(i+1);
				cus.setUsername(listt.get(i)[0].toString());
				cus.setFullname(listt.get(i)[1].toString());
				cus.setAddress(listt.get(i)[2].toString());
				cus.setPhone(listt.get(i)[3].toString());

				double total = Double.valueOf(listt.get(i)[4].toString());
				cus.setTotal(total);
				cus.setCount(listt.get(i)[5].toString());

				reportCus.add(cus);
			}
		}

		m.addAttribute("reportCustomer", reportCus);
		//Thống kê theo sản phẩm.
		List<ReportProducts> reportPro = new ArrayList<>();
		List<Object[]> listt2 = orderdetailDAO.report2();
		if (!listt2.isEmpty()) {
			for (int i = 0; i < listt2.size(); i++) {

				ReportProducts pro = new ReportProducts();
				pro.setId(i+1);
				pro.setIdPro(Integer.valueOf(listt2.get(i)[0].toString()));
				pro.setProducts(listt2.get(i)[1].toString());
				pro.setAmount(Integer.valueOf(listt2.get(i)[2].toString()));
				pro.setTotal(Double.valueOf(listt2.get(i)[3].toString()));

				reportPro.add(pro);
			}
		}

		m.addAttribute("reportProducts", reportPro);
		

		return path + "reports.html";
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
