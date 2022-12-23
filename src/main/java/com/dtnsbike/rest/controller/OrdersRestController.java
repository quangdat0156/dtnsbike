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

import com.dtnsbike.dao.service.OrdersService;
import com.dtnsbike.entity.Orders;

@CrossOrigin("*")
@RestController
public class OrdersRestController {

	@Autowired
	OrdersService order;

//  Rest api danh sách
	@GetMapping("/rest/orders")
	public ResponseEntity<List<Orders>> getAll(Model m) {
		return ResponseEntity.ok(order.findAll());
	}

//  Rest api danh sách
	@GetMapping("/rest/orderss/{idd}")
	public ResponseEntity<List<Orders>> getAllOrder(Model m, @PathVariable("idd") String id) {

		if (id.equals("cxn")) {
			return ResponseEntity.ok(order.findWait());
		} else if (id.equals("clh")) {
			return ResponseEntity.ok(order.findWait1());
		} else if (id.equals("dangi")) {
			return ResponseEntity.ok(order.findShipp());
		} else if (id.equals("dagi")) {
			return ResponseEntity.ok(order.findDone());
		} else if (id.equals("dnh")) {
			return ResponseEntity.ok(order.allReorder());
		} else if (id.equals("dh")) {
			return ResponseEntity.ok(order.findCancel());
		}
		return ResponseEntity.ok(order.findAll());
	}

//  Rest api một đối tượng
	@GetMapping("/rest/orders/{id}")
	public ResponseEntity<Orders> getOne(@PathVariable("id") Integer id) {
		if (order.findById(id) == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(order.findById(id));
	}

//  Rest api tạo mới đối tượng
	@PostMapping("/rest/orders")
	public ResponseEntity<Orders> post(@RequestBody Orders data) {
		if (order.findById(data.getId()) != null) {
			return ResponseEntity.notFound().build();
		}
		order.add(data);
		return ResponseEntity.ok(order.findById(data.getId()));
	}

//  Rest api update object
	@PutMapping("/rest/orders/{id}")
	public ResponseEntity<Orders> put(@PathVariable("id") String id, @RequestBody Orders data) {
		if (order.findById(data.getId()) == null) {
			return ResponseEntity.notFound().build();
		}
		order.update(data);
		return ResponseEntity.ok(data);
	}

//  Rest api xóa một đối tượng
	@DeleteMapping("/rest/orders/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
		if (order.findById(id) == null) {
			return ResponseEntity.notFound().build();
		}
		order.delete(id);
		return ResponseEntity.ok().build();
	}

}
