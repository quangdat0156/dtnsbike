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

import com.dtnsbike.dao.service.StatusService;
import com.dtnsbike.entity.Status;

@CrossOrigin("*")
@RestController
public class StatusRestController {

    @Autowired
    StatusService status;

//  Rest api danh sách
    @GetMapping("/rest/status")
    public ResponseEntity<List<Status>> getAll(Model m) {
        return ResponseEntity.ok(status.findAll());
    }

//  Rest api một đối tượng
    @GetMapping("/rest/status/{id}")
    public ResponseEntity<Status> getOne(@PathVariable("id") String id) {
        if (status.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status.findById(id));
    }

//  Rest api tạo mới đối tượng
    @PostMapping("/rest/status")
    public ResponseEntity<Status> post(@RequestBody Status data) {
        if (status.findById(data.getId()) != null) {
            return ResponseEntity.notFound().build();
        }
        status.add(data);
        return ResponseEntity.ok(status.findById(data.getId()));
    }

//  Rest api update object
    @PutMapping("/rest/status/{id}")
    public ResponseEntity<Status> put(@PathVariable("id") String id, @RequestBody Status data) {
        if (status.findById(data.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        status.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/status/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        if (status.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        status.delete(id);
        return ResponseEntity.ok().build();
    }

}
