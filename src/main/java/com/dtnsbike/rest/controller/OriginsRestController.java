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

import com.dtnsbike.dao.service.OriginsService;
import com.dtnsbike.entity.Origins;

@CrossOrigin("*")
@RestController
public class OriginsRestController {

    @Autowired
    OriginsService origin;

//  Rest api danh sách
    @GetMapping("/rest/origins")
    public ResponseEntity<List<Origins>> getAll(Model m) {
        return ResponseEntity.ok(origin.findAll());
    }

//  Rest api một đối tượng
    @GetMapping("/rest/origins/{id}")
    public ResponseEntity<Origins> getOne(@PathVariable("id") Integer id) {
        if (origin.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(origin.findById(id));
    }

//  Rest api tạo mới đối tượng
    @PostMapping("/rest/origins")
    public ResponseEntity<Origins> post(@RequestBody Origins data) {
        if (origin.findById(data.getId()) != null) {
            return ResponseEntity.notFound().build();
        }
        origin.add(data);
        return ResponseEntity.ok(origin.findById(data.getId()));
    }

//  Rest api update object
    @PutMapping("/rest/origins/{id}")
    public ResponseEntity<Origins> put(@PathVariable("id") String id, @RequestBody Origins data) {
        if (origin.findById(data.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        origin.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/origins/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (origin.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        origin.delete(id);
        return ResponseEntity.ok().build();
    }

}
