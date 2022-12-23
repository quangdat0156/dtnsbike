package com.dtnsbike.controller.users;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.dao.service.FavoritesService;
import com.dtnsbike.dao.service.ProductsService;
import com.dtnsbike.dao.service.TypesService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Favorites;
import com.dtnsbike.entity.Products;
import com.dtnsbike.entity.Types;
import com.dtnsbike.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
public class FavoritesController {

    String path = "users/common/";
    @Autowired
    TypesService typ_service;
    @Autowired
    FavoritesService fav_service;
    @Autowired
    ProductsService pro_service;

    @Autowired
    SessionService session;
    @Autowired
    HttpServletRequest req;
    @Autowired
    AccountsDAO accountsDAO;

//	Trang sản phẩm yêu thích
    @RequestMapping("/favorite.html")
    public String favoritePage(Model m, @RequestParam("removeid") Optional<Integer> id) {

        Accounts ac = (Accounts) session.get("account");
        m.addAttribute("favs", fav_service.findAllUser(ac));

        if (id.isPresent()) {
            fav_service.delete(id.get());
            return "redirect:/favorite.html";
        } else {
            id.orElse(0);
        }

        return path + "favorites/favorite";
    }

    @RequestMapping("/favorite.html/remove/{id}")
    public String favoritePage2(Model m, @PathVariable("id") Integer id) {

        Accounts ac = (Accounts) session.get("account");
        m.addAttribute("favs", fav_service.findAllUser(ac));

        fav_service.delete(fav_service.findFav(ac, pro_service.findById(id)).get(0).getId());

        String uri = session.get("uri");

        if (uri != null) {
            if(uri.contains("detail")) {
                Integer idd = session.get("idd");
                return "redirect:" + uri.substring(uri.lastIndexOf("/"))+"?id="+idd;
            }else {
                return "redirect:" + uri.substring(uri.lastIndexOf("/"));
            }
        }

        return "redirect:/detail.html?id=" + id;
    }

    @RequestMapping("/favorite.html/add/{id}")
    public String addfavoritePage(@PathVariable("id") Integer id) {

        Accounts ac = (Accounts) session.get("account");
        Products pro = pro_service.findById(id);

        Favorites fav = new Favorites();
        fav.setUserFvr(ac);
        fav.setProductsId(pro);

        fav_service.add(fav);

        String uri = session.get("uri");
        System.out.println("Uri  "+ uri);
        if (uri != null) {
           if(uri.contains("detail")) {
               Integer idd = session.get("idd");
               return "redirect:" + uri.substring(uri.lastIndexOf("/"))+"?id="+idd;
           }else {
               return "redirect:" + uri.substring(uri.lastIndexOf("/"));
           }
        }

        return "redirect:/favorite.html";
    }

    @ModelAttribute("types")
    List<Types> findAllType() {
        return typ_service.findAllInProduct();
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
