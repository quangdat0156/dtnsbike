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

import com.dtnsbike.dao.service.SizesService;
import com.dtnsbike.entity.Sizes;

@CrossOrigin("*")
@RestController
public class SizesRestController {

    @Autowired
    SizesService size;

//  Rest api danh sách
    @GetMapping("/rest/sizes")
    public ResponseEntity<List<Sizes>> getAll(Model m) {
        return ResponseEntity.ok(size.findAll());
    }

//  Rest api một đối tượng
    @GetMapping("/rest/sizes/{id}")
    public ResponseEntity<Sizes> getOne(@PathVariable("id") String id) {
        if (size.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(size.findById(id).get());
    }

//  Rest api check vali
    @GetMapping("/rest/sizes/check/{id}")
    public Boolean check(@PathVariable("id") String id) {
    	return size.checkInProduct(id) != null;
    }
    
//  Rest api tạo mới đối tượng
    @PostMapping("/rest/sizes")
    public Sizes post(@RequestBody Sizes data) {
        size.add(data);
        return data;
    }

//  Rest api update object
    @PutMapping("/rest/sizes/{id}")
    public ResponseEntity<Sizes> put(@PathVariable("id") String id, @RequestBody Sizes data) {
        if (size.findById(data.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        size.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/sizes/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        if (size.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        size.delete(id);
        return ResponseEntity.ok().build();
    }

}
