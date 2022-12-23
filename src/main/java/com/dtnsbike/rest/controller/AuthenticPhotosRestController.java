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

import com.dtnsbike.dao.service.AuthenticPhotosService;
import com.dtnsbike.entity.AuthenticPhotos;

@CrossOrigin("*")
@RestController
public class AuthenticPhotosRestController {

    @Autowired
    AuthenticPhotosService authenP;

//  Rest api danh sách
    @GetMapping("/rest/authenticPhotos")
    public ResponseEntity<List<AuthenticPhotos>> getAll(Model m) {
        return ResponseEntity.ok(authenP.findAll());
    }

//  Rest api một đối tượng
    @GetMapping("/rest/authenticPhotos/{id}")
    public ResponseEntity<AuthenticPhotos> getOne(@PathVariable("id") String id) {
        if (authenP.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(authenP.findById(id));
    }

//  Rest api tạo mới đối tượng
    @PostMapping("/rest/authenticPhotos")
    public ResponseEntity<AuthenticPhotos> post(@RequestBody AuthenticPhotos data) {
        if (authenP.findById(data.getAuthenticphotoid()) != null) {
            return ResponseEntity.notFound().build();
        }
        authenP.add(data);
        return ResponseEntity.ok(authenP.findById(data.getAuthenticphotoid()));
    }

//  Rest api update object
    @PutMapping("/rest/authenticPhotos/{id}")
    public ResponseEntity<AuthenticPhotos> put(@PathVariable("id") String id, @RequestBody AuthenticPhotos data) {
        if (authenP.findById(data.getAuthenticphotoid()) == null) {
            return ResponseEntity.notFound().build();
        }
        authenP.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/authenticPhotos/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        if (authenP.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        authenP.delete(id);
        return ResponseEntity.ok().build();
    }

}
