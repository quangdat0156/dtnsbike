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

import com.dtnsbike.dao.service.ProductsService;
import com.dtnsbike.entity.Products;

@CrossOrigin("*")
@RestController
public class ProductsRestController {

    @Autowired
    ProductsService product;

//  Rest api danh sách
    @GetMapping("/rest/products")
    public ResponseEntity<List<Products>> getAll(Model m) {
        return ResponseEntity.ok(product.findAll());
    }

//  Rest api một đối tượng
    @GetMapping("/rest/products/{id}")
    public ResponseEntity<Products> getOne(@PathVariable("id") Integer id) {
        if (product.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product.findById(id));
    }

//  Rest api tạo mới đối tượng
    @PostMapping("/rest/products")
    public ResponseEntity<Products> post(@RequestBody Products data) {
        product.add(data);
        return ResponseEntity.ok(product.findById(data.getId()));
    }

//  Rest api update object
    @PutMapping("/rest/products/{id}")
    public ResponseEntity<Products> put(@PathVariable("id") String id, @RequestBody Products data) {
        if (product.findById(data.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        product.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/products/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (product.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        product.delete(id);
        return ResponseEntity.ok().build();
    }

}
