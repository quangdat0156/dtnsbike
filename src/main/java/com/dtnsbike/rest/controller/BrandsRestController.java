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
import com.dtnsbike.dao.service.BrandsService;
import com.dtnsbike.entity.Brands;

@CrossOrigin("*")
@RestController
public class BrandsRestController {

	@Autowired
	BrandsService brand;

//  Rest api danh sách
	@GetMapping("/rest/brands")
	public ResponseEntity<List<Brands>> getAll(Model m) {
		return ResponseEntity.ok(brand.findAll());
	}

//  Rest api một đối tượng
	@GetMapping("/rest/brands/{id}")
	public ResponseEntity<Brands> getOne(@PathVariable("id") Integer id) {
		if (brand.findById(id) == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(brand.findById(id));
	}

//	Check api
	@GetMapping("/rest/brands/check/{id}")
	public Boolean check(@PathVariable("id") Integer id) {
		return brand.check(id) != null;
	}

//  Rest api tạo mới đối tượng
	@PostMapping("/rest/brands")
	public Brands post(@RequestBody Brands data) {
		brand.add(data);
		return data;
	}

//  Rest api update object
	@PutMapping("/rest/brands/{id}")
	public ResponseEntity<Brands> put(@PathVariable("id") Integer id, @RequestBody Brands data) {
		if (brand.findById(data.getBrandid()) == null) {
			return ResponseEntity.notFound().build();
		}
		brand.update(data);
		return ResponseEntity.ok(data);
	}

//  Rest api xóa một đối tượng
	@DeleteMapping("/rest/brands/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
		if (brand.findById(id) == null) {
			return ResponseEntity.notFound().build();
		}
		brand.delete(id);
		return ResponseEntity.ok().build();
	}

}
