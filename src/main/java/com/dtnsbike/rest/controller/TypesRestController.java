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

import com.dtnsbike.dao.service.TypesService;
import com.dtnsbike.entity.Types;

@CrossOrigin("*")
@RestController
public class TypesRestController {

    @Autowired
    TypesService type;

//  Rest api danh sách
    @GetMapping("/rest/types")
    public ResponseEntity<List<Types>> getAll(Model m) {
        return ResponseEntity.ok(type.findAll());
    }
//  Check Rest api 
    @GetMapping("/rest/types/check/{id}")
    public Boolean getAll(@PathVariable("id") Integer id) {
        return type.check(id) != null;
    }

//  Rest api một đối tượng
    @GetMapping("/rest/types/{id}")
    public ResponseEntity<Types> getOne(@PathVariable("id") Integer id) {
        if (type.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(type.findById(id));
    }

//  Rest api tạo mới đối tượng
    @PostMapping("/rest/types")
    public Types post(@RequestBody Types data) {
        type.add(data);
        return data;
    }

//  Rest api update object
    @PutMapping("/rest/types/{id}")
    public ResponseEntity<Types> put(@PathVariable("id") String id, @RequestBody Types data) {
        if (type.findById(data.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        type.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/types/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (type.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        type.delete(id);
        return ResponseEntity.ok().build();
    }

}
