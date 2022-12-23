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

import com.dtnsbike.dao.service.ProductPriceHisService;
import com.dtnsbike.entity.ProductPriceHistories;

@CrossOrigin("*")
@RestController
public class ProductPriceHisRestController {

    @Autowired
    ProductPriceHisService productPriceHistory;

//  Rest api danh sách
    @GetMapping("/rest/productPriceHistories")
    public ResponseEntity<List<ProductPriceHistories>> getAll(Model m) {
        return ResponseEntity.ok(productPriceHistory.findAll());
    }

//  Rest api một đối tượng
    @GetMapping("/rest/productPriceHistories/{id}")
    public ResponseEntity<ProductPriceHistories> getOne(@PathVariable("id") Integer id) {
        if (productPriceHistory.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productPriceHistory.findById(id));
    }

//  Rest api tạo mới đối tượng
    @PostMapping("/rest/productPriceHistories")
    public ResponseEntity<ProductPriceHistories> post(@RequestBody ProductPriceHistories data) {
        if (productPriceHistory.findById(data.getId()) != null) {
            return ResponseEntity.notFound().build();
        }
        productPriceHistory.add(data);
        return ResponseEntity.ok(productPriceHistory.findById(data.getId()));
    }

//  Rest api update object
    @PutMapping("/rest/productPriceHistories/{id}")
    public ResponseEntity<ProductPriceHistories> put(@PathVariable("id") String id,
            @RequestBody ProductPriceHistories data) {
        if (productPriceHistory.findById(data.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        productPriceHistory.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/productPriceHistories/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (productPriceHistory.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        productPriceHistory.delete(id);
        return ResponseEntity.ok().build();
    }

}
