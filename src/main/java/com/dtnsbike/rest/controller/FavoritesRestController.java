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

import com.dtnsbike.dao.service.FavoritesService;
import com.dtnsbike.entity.Favorites;

@CrossOrigin("*")
@RestController
public class FavoritesRestController {

    @Autowired
    FavoritesService favorite;

//  Rest api danh sách
    @GetMapping("/rest/favorites")
    public ResponseEntity<List<Favorites>> getAll(Model m) {
        return ResponseEntity.ok(favorite.findAll());
    }

//  Rest api một đối tượng
    @GetMapping("/rest/favorites/{id}")
    public ResponseEntity<Favorites> getOne(@PathVariable("id") Integer id) {
        if (favorite.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(favorite.findById(id));
    }

//  Rest api tạo mới đối tượng
    @PostMapping("/rest/favorites")
    public ResponseEntity<Favorites> post(@RequestBody Favorites data) {
        if (favorite.findById(data.getId()) != null) {
            return ResponseEntity.notFound().build();
        }
        favorite.add(data);
        return ResponseEntity.ok(favorite.findById(data.getId()));
    }

//  Rest api update object
    @PutMapping("/rest/favorites/{id}")
    public ResponseEntity<Favorites> put(@PathVariable("id") String id, @RequestBody Favorites data) {
        if (favorite.findById(data.getId()) == null) {
            return ResponseEntity.notFound().build();
        }
        favorite.update(data);
        return ResponseEntity.ok(data);
    }

//  Rest api xóa một đối tượng
    @DeleteMapping("/rest/favorites/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        if (favorite.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        favorite.delete(id);
        return ResponseEntity.ok().build();
    }

}
