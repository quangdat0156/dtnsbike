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

import com.dtnsbike.dao.service.AuthoritiesService;
import com.dtnsbike.entity.Authorities;

@CrossOrigin("*")
@RestController
public class AuthRestController {

    @Autowired
    AuthoritiesService auth;

//  Rest api danh sách
    @GetMapping("/rest/auths")
    public ResponseEntity<List<Authorities>> getAll(Model m) {
        return ResponseEntity.ok(auth.findAll());
    }

//  Rest api danh sách
    @GetMapping("/rest/auths/user")
    public ResponseEntity<List<String>> getUsername(Model m) {
        return ResponseEntity.ok(auth.getUsername());
    }

//  Rest api một đối tượng
    @GetMapping("/rest/auths/{id}")
    public ResponseEntity<Authorities> getOne(@PathVariable("id") Integer id) {
        if (auth.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(auth.findById(id));
    }

//  Rest api tạo mới đối tượng
    @PostMapping("/rest/auths")
    public ResponseEntity<Authorities> post(@RequestBody Authorities data) {
        auth.add(data);
        return ResponseEntity.ok(data);
    }

//  Rest api update object
    @PutMapping("/rest/auths/{id}")
    public ResponseEntity<Authorities> put(@PathVariable("id") String id, @RequestBody Authorities data) {
        if (auth.findById(data.getAuthoritiesid()) == null) {
            return ResponseEntity.notFound().build();
        }
        auth.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/auths/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (auth.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        auth.delete(id);
        return ResponseEntity.ok().build();
    }

//  Rest api danh sách
    @GetMapping("/rest/auths/user/{id}")
    public ResponseEntity<List<Authorities>> getUsername(@PathVariable("id") String id) {
        return ResponseEntity.ok(auth.getAuthoritiesByUsername(id));
    }

}
