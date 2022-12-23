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

import com.dtnsbike.dao.service.OrderDetailsService;
import com.dtnsbike.entity.OrderDetails;

@CrossOrigin("*")
@RestController
public class OrderDetailsRestController {

	@Autowired
	OrderDetailsService orderDetail;

//  Rest api danh sách
	@GetMapping("/rest/orderDetails")
	public ResponseEntity<List<OrderDetails>> getAll(Model m) {
		return ResponseEntity.ok(orderDetail.findAll());
	}

	@GetMapping("/rest/orderdetails/prodetaiid/{id}")
	public ResponseEntity<List<OrderDetails>> getByProDetail(Model m, @PathVariable("id") Integer id) {
		if (orderDetail.findByProductDetails(id) == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderDetail.findByProductDetails(id));
	}

//  Rest api một đối tượng
	@GetMapping("/rest/orderDetails/{id}")
	public ResponseEntity<OrderDetails> getOne(@PathVariable("id") Integer id) {
		if (orderDetail.findById(id) == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderDetail.findById(id));
	}

//  Rest api tạo mới đối tượng
	@PostMapping("/rest/orderDetails")
	public ResponseEntity<OrderDetails> post(@RequestBody OrderDetails data) {
		if (orderDetail.findById(data.getOrdersId().getId()) != null) {
			return ResponseEntity.notFound().build();
		}
		orderDetail.add(data);
		return ResponseEntity.ok(orderDetail.findById(data.getOrdersId().getId()));
	}

//  Rest api update object
	@PutMapping("/rest/orderDetails/{id}")
	public ResponseEntity<OrderDetails> put(@PathVariable("id") String id, @RequestBody OrderDetails data) {
		if (orderDetail.findById(data.getOrdersId().getId()) == null) {
			return ResponseEntity.notFound().build();
		}
		orderDetail.update(data);
		return ResponseEntity.ok(data);
	}

//  Rest api xóa một đối tượng
	@DeleteMapping("/rest/orderDetails/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
		if (orderDetail.findById(id) == null) {
			return ResponseEntity.notFound().build();
		}
		orderDetail.delete(id);
		return ResponseEntity.ok().build();
	}

}
