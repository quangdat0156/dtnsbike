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

import com.dtnsbike.dao.service.DiscountsService;
import com.dtnsbike.entity.Discounts;

@CrossOrigin("*")
@RestController
public class DiscountsRestController {

    @Autowired
    DiscountsService discount;

//  Rest api danh sách
    @GetMapping("/rest/discounts")
    public ResponseEntity<List<Discounts>> getAll(Model m) {
        return ResponseEntity.ok(discount.findAll());
    }

//  Rest api một đối tượng
    @GetMapping("/rest/discounts/{id}")
    public ResponseEntity<Discounts> getOne(@PathVariable("id") Integer id) {
        if (discount.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(discount.findById(id));
    }

//  Rest api tạo mới đối tượng
    @PostMapping("/rest/discounts")
    public ResponseEntity<Discounts> post(@RequestBody Discounts data) {
        if (discount.findById(data.getId()) != null) {
            return ResponseEntity.notFound().build();
        }
        discount.add(data);
        return ResponseEntity.ok(discount.findById(data.getId()));
    }

//  Rest api update object
    @PutMapping("/rest/discounts/{id}")
    public ResponseEntity<Discounts> put(@PathVariable("id") String id, @RequestBody Discounts data) {
        if (discount.findById(data.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        discount.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/discounts/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (discount.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        discount.delete(id);
        return ResponseEntity.ok().build();
    }

}
