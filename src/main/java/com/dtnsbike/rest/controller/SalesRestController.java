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

import com.dtnsbike.dao.service.SalesService;
import com.dtnsbike.entity.Sales;

@CrossOrigin("*")
@RestController
public class SalesRestController {

    @Autowired
    SalesService sale;

//  Rest api danh sách
    @GetMapping("/rest/sales")
    public ResponseEntity<List<Sales>> getAll(Model m) {
        return ResponseEntity.ok(sale.findAll());
    }

//  Rest api một đối tượng
    @GetMapping("/rest/sales/{id}")
    public ResponseEntity<Sales> getOne(@PathVariable("id") String id) {
        if (sale.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sale.findById(id).get());
    }
//  Rest api check vali
    @GetMapping("/rest/sales/check/{id}")
    public boolean check(@PathVariable("id") String id) {
        return sale.checkOrder(id) !=null;
    }

//  Rest api tạo mới đối tượng
    @PostMapping("/rest/sales")
    public  ResponseEntity<Sales> post(@RequestBody Sales data) {
        sale.add(data);
        return ResponseEntity.ok(data);
    }
        
//    @PostMapping("/rest/sales")
//    public  Sales post(@RequestBody Sales data) {
//        sale.add(data);
//        return data;
//    }

    

//  Rest api update object
    @PutMapping("/rest/sales/{id}")
    public ResponseEntity<Sales> put(@PathVariable("id") String id, @RequestBody Sales data) {
        if (sale.findById(data.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        sale.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/sales/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        if (sale.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        sale.delete(id);
        return ResponseEntity.ok().build();
    }

}
