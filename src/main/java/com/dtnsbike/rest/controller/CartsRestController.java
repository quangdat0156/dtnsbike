package com.dtnsbike.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dtnsbike.dao.service.CartsService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Carts;
import com.dtnsbike.service.SessionService;

@CrossOrigin("*")
@RestController
public class CartsRestController {

    @Autowired
    CartsService cart;
    @Autowired
    SessionService session;

//  Rest api danh sách
    @GetMapping("/rest/carts")
    public ResponseEntity<List<Carts>> getAll(Model m) {
        return ResponseEntity.ok(cart.findAll());
    }
    
//  Rest api danh sách
    @GetMapping("/rest/cartLsit")
    public ResponseEntity<List<Carts>> getAllByUser(Model m) {
        Accounts acc = session.get("account");
        return ResponseEntity.ok(cart.findAllbyUser(acc.getUsername()));
    }

//  Rest api một đối tượng
    @GetMapping("/rest/carts/{id}")
    public ResponseEntity<Carts> getOne(@PathVariable("id") String id) {
        if (cart.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cart.findById(id));
    }

//  Rest api tạo mới đối tượng
    @PostMapping("/rest/carts")
    public ResponseEntity<Carts> post(@RequestBody Carts data) {
        if (cart.findById(data.getId()) != null) {
            return ResponseEntity.notFound().build();
        }
        cart.add(data);
        return ResponseEntity.ok(cart.findById(data.getId()));
    }

//  Rest api update object
    @PutMapping("/rest/carts/{id}")
    public ResponseEntity<Carts> put(@PathVariable("id") String id, @RequestBody Carts data) {
        if (cart.findById(data.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        cart.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/carts/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        if (cart.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        cart.delete(id);
        return ResponseEntity.ok().build();
    }

}
