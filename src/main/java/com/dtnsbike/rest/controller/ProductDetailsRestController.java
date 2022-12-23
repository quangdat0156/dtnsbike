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
import com.dtnsbike.dao.service.ProductDetailsService;
import com.dtnsbike.entity.ProductDetails;

@CrossOrigin("*")
@RestController
public class ProductDetailsRestController {

	@Autowired
	ProductDetailsService productDetail;

//  Rest api danh sách
	@GetMapping("/rest/productDetails")
	public ResponseEntity<List<ProductDetails>> getAll(Model m) {
		return ResponseEntity.ok(productDetail.findAll());
	}

//  Rest api một đối tượng
	@GetMapping("/rest/productDetails/{id}")
	public ResponseEntity<ProductDetails> getOne(@PathVariable("id") Integer id) {
		if (productDetail.findById(id) == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(productDetail.findById(id));
	}

	@GetMapping("/rest/productdetails/pro/{id}")
	public ResponseEntity<List<ProductDetails>> getListPro(@PathVariable("id") Integer id) {
		if (productDetail.findByProId(id) == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(productDetail.findByProId(id));
	}

	@GetMapping("/rest/productdetails/proandclr/{idPro}/{idClr}")
	public ResponseEntity<Boolean> getListProByProIdAndClrId(@PathVariable("idPro") Integer idPro,
			@PathVariable("idClr") String idClr) {
		if (!productDetail.getProDtlByProIdAddClrId(idPro, idClr).isPresent()) {
			return ResponseEntity.ok(false);
		}
		return ResponseEntity.ok(true);
	}

	@GetMapping("/rest/productdetails/proandclrandsze/{idPro}/{idClr}/{idSze}")
	public ResponseEntity<ProductDetails> getListProByProIdAndClrId(@PathVariable("idPro") Integer idPro,
			@PathVariable("idClr") String idClr, @PathVariable("idSze") String idSze) {
		if (!productDetail.getProDtlByProIdAddClrIdAndSze(idPro, idClr, idSze).isPresent()) {
			return ResponseEntity.ok(new ProductDetails());
		}
		return ResponseEntity.ok(productDetail.getProDtlByProIdAddClrIdAndSze(idPro, idClr, idSze).get());
	}

	@GetMapping("/rest/productdetails/proandclrandszes/{idPro}/{idClr}/{idSze}")
	public ResponseEntity<ProductDetails> getListProByProIdAndClrIds(@PathVariable("idPro") Integer idPro,
			@PathVariable("idClr") String idClr, @PathVariable("idSze") String idSze) {
		if (productDetail.getProDltByProIdAndClrIdAndSzeId(idPro, idClr, idSze) == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(productDetail.getProDltByProIdAndClrIdAndSzeId(idPro, idClr, idSze));
	}

//  Rest api tạo mới đối tượng
	@PostMapping("/rest/productDetails")
	public ResponseEntity<ProductDetails> post(@RequestBody ProductDetails data) {
		productDetail.add(data);
		return ResponseEntity.ok(data);
	}

//  Rest api update object
	@PutMapping("/rest/productDetails/{id}")
	public ResponseEntity<ProductDetails> put(@PathVariable("id") String id, @RequestBody ProductDetails data) {
		if (productDetail.findById(data.getId()) == null) {
			return ResponseEntity.notFound().build();
		}
		productDetail.update(data);
		return ResponseEntity.ok(data);
	}

//  Rest api xóa một đối tượng
	@DeleteMapping("/rest/productDetails/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
		if (productDetail.findById(id) == null) {
			return ResponseEntity.notFound().build();
		}
		productDetail.delete(id);
		return ResponseEntity.ok().build();
	}

}
