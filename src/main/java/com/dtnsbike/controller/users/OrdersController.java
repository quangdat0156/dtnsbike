package com.dtnsbike.controller.users;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.dao.interfaces.OrderDetailsDAO;
import com.dtnsbike.dao.interfaces.OrdersDAO;
import com.dtnsbike.dao.interfaces.StatusDAO;
import com.dtnsbike.dao.service.FavoritesService;
import com.dtnsbike.dao.service.TypesService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.OrderDetails;
import com.dtnsbike.entity.Orders;
import com.dtnsbike.entity.Types;
import com.dtnsbike.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;


@Controller
public class OrdersController {

    String path = "users/common/";
    @Autowired
    TypesService typ_service;
    @Autowired
    SessionService session;
    @Autowired
    OrderDetailsDAO orderdetaildao;
    @Autowired
    OrdersDAO orderdao;
    @Autowired
    StatusDAO statusdao;
    @Autowired
    HttpServletRequest req;
    @Autowired
    AccountsDAO accountsDAO;
    @Autowired
    FavoritesService fav_service;

//	Trang đơn hàng của bạn aka Quản lý đơn hàng
    @GetMapping("/orders.html")
    public String ordersPage(Model m) {     
        //tất cả đơn hàng
        Accounts account=session.get("account");
        m.addAttribute("order", orderdao.findByUserOrder(account));
        //danh sách đơn hàng chờ xác nhận
        List<Orders> order_xacnhan=orderdao.find_by_choxacnhan(account.getUsername());
        m.addAttribute("xacnhan", order_xacnhan);
        //danh sách đơn hàng chờ lấy hàng
        List<Orders> order_layhang=orderdao.find_by_cholayhang(account.getUsername());
        m.addAttribute("layhang", order_layhang);
        //danh sách đơn hàng đang giao
        List<Orders> order_danggiao=orderdao.find_by_danggiao(account.getUsername());
        m.addAttribute("danggiao", order_danggiao);
        //danh sách đơn hàng đã giao
        List<Orders> order_dagiao=orderdao.find_by_Dagiao(account.getUsername());
        m.addAttribute("dagiao", order_dagiao);
        //danh sách đơn hàng đã hủy
        List<Orders> order_dahuy=orderdao.find_by_huy(account.getUsername());
        m.addAttribute("dahuy", order_dahuy);
        
        return path + "orders/orders";
    }
//Hủy đơn hàng
    @GetMapping("/orders.html/huy/{id}")
        public String ordersCancel(Model m, @PathVariable("id") Integer id) {
        Accounts account=session.get("account");
        Orders order=orderdao.findById(id).get();
        order.setStatusId(statusdao.findById("dh").get());
        orderdao.save(order);
        return "redirect:/orders.html";
    }

//	Trang chi tiết đơn hàng 
    @RequestMapping("/order_details.html")
    public String ordersDetailPage(Model m, @RequestParam("id") String id) {
        Accounts account=session.get("account");
        if(!NumberUtils.isParsable(id)) {
        	 return "redirect:/pageNotFound.html";
        }
        List<OrderDetails> orderdetail=orderdetaildao.find_by_orderid(Integer.parseInt(id));
        if(orderdetail.size() ==0) {
        	 return "redirect:/pageNotFound.html";
        }
        m.addAttribute("dt", orderdetail);
        
        
        return path + "orders/order_details";
    }
    
    
    @ModelAttribute("types")
    List<Types> findAllType(){
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
