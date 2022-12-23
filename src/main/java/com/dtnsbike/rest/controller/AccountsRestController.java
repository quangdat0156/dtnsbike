package com.dtnsbike.rest.controller;

import java.util.List;
import javax.servlet.ServletContext;
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
import com.dtnsbike.dao.service.AccountsService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.service.SessionService;

@CrossOrigin("*")
@RestController
public class AccountsRestController {

    @Autowired
    AccountsService account;
    @Autowired
    ServletContext context;
    @Autowired
    SessionService session;

//  Rest api danh sách
    @GetMapping("/rest/accounts")
    public ResponseEntity<List<Accounts>> getAll(Model m) {
        return ResponseEntity.ok(account.findAll());
    }

//  Rest api một đối tượng
    @GetMapping("/rest/accounts/{id}")
    public ResponseEntity<Accounts> getOne(@PathVariable("id") String id) {
        if (account.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account.findById(id));
    }

//  Rest api tạo mới đối tượng
    @PostMapping("/rest/accounts")
    public ResponseEntity<Accounts> post(@RequestBody Accounts data) {
        account.add(data);
        return ResponseEntity.ok(account.findById(data.getUsername()));
    }

//  Rest api update object
    @PutMapping("/rest/accounts/{id}")
    public ResponseEntity<Accounts> put(@PathVariable("id") String id, @RequestBody Accounts data) {
        if (account.findById(data.getUsername()) == null) {
            return ResponseEntity.notFound().build();
        }
        account.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/accounts/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        if (account.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        account.delete(id);
        return ResponseEntity.ok().build();
    }

//  Rest api tạo mới đối tượng

}
