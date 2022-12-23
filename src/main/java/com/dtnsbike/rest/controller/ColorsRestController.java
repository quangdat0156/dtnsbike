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

import com.dtnsbike.dao.service.ColorsService;
import com.dtnsbike.entity.Colors;

@CrossOrigin("*")
@RestController
public class ColorsRestController {

    @Autowired
    ColorsService color;

//  Rest api danh sách
    @GetMapping("/rest/colors")
    public ResponseEntity<List<Colors>> getAll(Model m) {
        return ResponseEntity.ok(color.findAll());
    }
//  Check Rest api danh sách
    @GetMapping("/rest/colors/check/{id}")
    public Boolean checkProduct(@PathVariable("id") String id) {
        return color.check(id) != null;
    }

//  Rest api một đối tượng
    @GetMapping("/rest/colors/{id}")
    public ResponseEntity<Colors> getOne(@PathVariable("id") String id) {
        if (color.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(color.findById(id).get());
    }

//  Rest api tạo mới đối tượng
    @PostMapping("/rest/colors")
    public ResponseEntity<Colors> post(@RequestBody Colors data) {
        color.add(data);
        return ResponseEntity.ok(data);
    }

//  Rest api update object
    @PutMapping("/rest/colors/{id}")
    public ResponseEntity<Colors> put(@PathVariable("id") String id, @RequestBody Colors data) {
        if (color.findById(data.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        color.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/colors/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        if (color.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        color.delete(id);
        return ResponseEntity.ok().build();
    }

}
