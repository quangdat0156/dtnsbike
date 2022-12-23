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

import com.dtnsbike.dao.service.CategoryService;
import com.dtnsbike.entity.Categories;

@CrossOrigin("*")
@RestController
public class CategoriesRestController {

	@Autowired
	CategoryService category;

//  Rest api danh sách
	@GetMapping("/rest/categories")
	public ResponseEntity<List<Categories>> getAll(Model m) {
		return ResponseEntity.ok(category.findAll());
	}

//  Rest api một đối tượng
	@GetMapping("/rest/categories/{id}")
	public ResponseEntity<Categories> getOne(@PathVariable("id") Integer id) {
		if (category.findById(id) == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(category.findById(id));
	}

//  Check Rest api một đối tượng
	@GetMapping("/rest/categories/check/{id}")
	public Boolean checkIn(@PathVariable("id") Integer id) {
		return category.check(id) != null;
	}

	@GetMapping("/rest/categories/types/{id}")
	public ResponseEntity<List<Categories>> getCateByType(@PathVariable("id") Integer id) {
		if (category.findByTypeId(id) == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(category.findByTypeId(id));
	}

//  Rest api tạo mới đối tượng
	@PostMapping("/rest/categories")
	public ResponseEntity<Categories> post(@RequestBody Categories data) {
		category.add(data);
		return ResponseEntity.ok(data);
	}

//  Rest api update object
	@PutMapping("/rest/categories/{id}")
	public ResponseEntity<Categories> put(@PathVariable("id") String id, @RequestBody Categories data) {
		if (category.findById(data.getId()) == null) {
			return ResponseEntity.notFound().build();
		}
		category.update(data);
		return ResponseEntity.ok(data);
	}

//  Rest api xóa một đối tượng
	@DeleteMapping("/rest/categories/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
		if (category.findById(id) == null) {
			return ResponseEntity.notFound().build();
		}
		category.delete(id);
		return ResponseEntity.ok().build();
	}

}
