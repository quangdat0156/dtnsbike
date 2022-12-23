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

import com.dtnsbike.dao.service.WarrantiesService;
import com.dtnsbike.entity.Warranties;

@CrossOrigin("*")
@RestController
public class WarrantiesRestController {

    @Autowired
    WarrantiesService warrantie;

//  Rest api danh sách
    @GetMapping("/rest/warranties")
    public ResponseEntity<List<Warranties>> getAll(Model m) {
        return ResponseEntity.ok(warrantie.findAll());
    }

//  Rest api một đối tượng
    @GetMapping("/rest/warranties/{id}")
    public ResponseEntity<Warranties> getOne(@PathVariable("id") Integer id) {
        if (warrantie.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(warrantie.findById(id));
    }

//  Rest api tạo mới đối tượng
    @PostMapping("/rest/warranties")
    public ResponseEntity<Warranties> post(@RequestBody Warranties data) {
        if (warrantie.findById(data.getId()) != null) {
            return ResponseEntity.notFound().build();
        }
        warrantie.add(data);
        return ResponseEntity.ok(warrantie.findById(data.getId()));
    }

//  Rest api update object
    @PutMapping("/rest/warranties/{id}")
    public ResponseEntity<Warranties> put(@PathVariable("id") String id, @RequestBody Warranties data) {
        if (warrantie.findById(data.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        warrantie.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/warranties/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (warrantie.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        warrantie.delete(id);
        return ResponseEntity.ok().build();
    }

}
