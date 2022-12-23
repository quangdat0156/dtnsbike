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

import com.dtnsbike.dao.service.ProductReviewsService;
import com.dtnsbike.entity.ProductReviews;

@CrossOrigin("*")
@RestController
public class ProductReviewsRestController {

    @Autowired
    ProductReviewsService productReview;

//  Rest api danh sách
    @GetMapping("/rest/productReviews")
    public ResponseEntity<List<ProductReviews>> getAll(Model m) {
        return ResponseEntity.ok(productReview.findAll());
    }

//  Rest api một đối tượng
    @GetMapping("/rest/productReviews/{id}")
    public ResponseEntity<ProductReviews> getOne(@PathVariable("id") Integer id) {
        if (productReview.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productReview.findById(id));
    }

//  Rest api tạo mới đối tượng
    @PostMapping("/rest/productReviews")
    public ResponseEntity<ProductReviews> post(@RequestBody ProductReviews data) {
        if (productReview.findById(data.getId()) != null) {
            return ResponseEntity.notFound().build();
        }
        productReview.add(data);
        return ResponseEntity.ok(productReview.findById(data.getId()));
    }

//  Rest api update object
    @PutMapping("/rest/productReviews/{id}")
    public ResponseEntity<ProductReviews> put(@PathVariable("id") String id, @RequestBody ProductReviews data) {
        if (productReview.findById(data.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        productReview.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/productReviews/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (productReview.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        productReview.delete(id);
        return ResponseEntity.ok().build();
    }

}
