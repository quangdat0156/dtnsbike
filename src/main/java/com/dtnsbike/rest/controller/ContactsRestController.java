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

import com.dtnsbike.dao.service.ContactsService;
import com.dtnsbike.entity.Contacts;

@CrossOrigin("*")
@RestController
public class ContactsRestController {

    @Autowired
    ContactsService contact;

//  Rest api danh sách
    @GetMapping("/rest/contacts")
    public ResponseEntity<List<Contacts>> getAll(Model m) {
        return ResponseEntity.ok(contact.findAll());
    }

//  Rest api một đối tượng
    @GetMapping("/rest/contacts/{id}")
    public ResponseEntity<Contacts> getOne(@PathVariable("id") Integer id) {
        if (contact.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(contact.findById(id));
    }

//  Rest api tạo mới đối tượng
    @PostMapping("/rest/contacts")
    public ResponseEntity<Contacts> post(@RequestBody Contacts data) {
        if (contact.findById(data.getId()) != null) {
            return ResponseEntity.notFound().build();
        }
        contact.add(data);
        return ResponseEntity.ok(contact.findById(data.getId()));
    }

//  Rest api update object
    @PutMapping("/rest/contacts/{id}")
    public ResponseEntity<Contacts> put(@PathVariable("id") String id, @RequestBody Contacts data) {
        if (contact.findById(data.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        contact.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/contacts/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (contact.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        contact.delete(id);
        return ResponseEntity.ok().build();
    }

}
