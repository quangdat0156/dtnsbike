package com.dtnsbike.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import com.dtnsbike.dao.service.DetailPhotosService;
import com.dtnsbike.entity.DetailPhotos;

@CrossOrigin("*")
@RestController
public class DetailPhotosRestController {

	@Autowired
	DetailPhotosService detailPhoto;

//  Rest api danh sách
	@GetMapping("/rest/detailPhotos")
	public ResponseEntity<List<DetailPhotos>> getAll(Model m) {
		return ResponseEntity.ok(detailPhoto.findAll());
	}

//  Rest api danh sách
	@GetMapping("/rest/detail/product/photos")
	public ResponseEntity<DetailPhotos> getByProAndFilename(Model m, HttpServletRequest request) {
		if (detailPhoto.findByProIdAndFileName(request.getParameter("product"), request.getParameter("filename")) == null) {
			return ResponseEntity.ok(null);
		}
		return ResponseEntity.ok(detailPhoto.findByProIdAndFileName(request.getParameter("product"), request.getParameter("filename")));
	}

//  Rest api một đối tượng
	@GetMapping("/rest/photos/pro/{id}")
	public ResponseEntity<List<String>> getOne(@PathVariable("id") Integer id) {
		if (detailPhoto.findByProId(id) == null) {
			ResponseEntity.ok(null);
		}
		return ResponseEntity.ok(detailPhoto.findByProId(id));
	}

//  Rest api tạo mới đối tượng
	@PostMapping("/rest/detailPhotos")
	public ResponseEntity<DetailPhotos> post(@RequestBody DetailPhotos data) {
		detailPhoto.add(data);
		return ResponseEntity.ok(detailPhoto.findById(data.getId()));
	}

//  Rest api update object
	@PutMapping("/rest/detailPhotos/{id}")
	public ResponseEntity<DetailPhotos> put(@PathVariable("id") String id, @RequestBody DetailPhotos data) {
		if (detailPhoto.findById(data.getId()) == null) {
			return ResponseEntity.notFound().build();
		}
		detailPhoto.update(data);
		return ResponseEntity.ok(data);
	}

//  Rest api xóa một đối tượng
	@DeleteMapping("/rest/detailphotos/delete/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") String id) {
		if (detailPhoto.findById(id) == null) {
			return ResponseEntity.notFound().build();
		}
		detailPhoto.delete(id);
		return ResponseEntity.ok().build();
	}

}
