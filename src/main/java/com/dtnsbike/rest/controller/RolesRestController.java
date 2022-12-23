package com.dtnsbike.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.dtnsbike.dao.service.RolesService;
import com.dtnsbike.entity.Roles;

@CrossOrigin("*")
@RestController
public class RolesRestController {

    @Autowired
    RolesService role;

//  Rest api danh sách
    @GetMapping("/rest/roles")
    public ResponseEntity<List<Roles>> getAll(Model m) {
        return ResponseEntity.ok(role.findAll());
    }

//  Rest api một đối tượng
    @GetMapping("/rest/roles/{id}")
    public ResponseEntity<Roles> getOne(@PathVariable("id") String id) {
        if (role.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(role.findById(id));
    }



}
