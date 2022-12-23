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

import com.dtnsbike.dao.service.BlogsService;
import com.dtnsbike.entity.Blogs;

@CrossOrigin("*")
@RestController
public class BlogsRestController {

    @Autowired
    BlogsService blog;

//  Rest api danh sách
    @GetMapping("/rest/blogs")
    public ResponseEntity<List<Blogs>> getAll(Model m) {
        return ResponseEntity.ok(blog.findAll());
    }

//  Rest api một đối tượng
    @GetMapping("/rest/blogs/{id}")
    public ResponseEntity<Blogs> getOne(@PathVariable("id") Integer id) {
        if (blog.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(blog.findById(id).get());
    }
//  Rest check api một đối tượng
    @GetMapping("/rest/blogs/check/{userid}")
    public Boolean check(@PathVariable("userid") String userid) {
        return blog.checkBlog(userid);
    }

//  Rest api tạo mới đối tượng
    @PostMapping("/rest/blogs")
    public ResponseEntity<Blogs> post(@RequestBody Blogs data) {
        blog.add(data);
        return ResponseEntity.ok(data);
    }

//  Rest api update object
    @PutMapping("/rest/blogs/{id}")
    public ResponseEntity<Blogs> put(@PathVariable("id") String id, @RequestBody Blogs data) {
        if (blog.findById(data.getBlogid()) == null) {
            return ResponseEntity.notFound().build();
        }
        blog.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/blogs/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (blog.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        blog.delete(id);
        return ResponseEntity.ok().build();
    }

}
