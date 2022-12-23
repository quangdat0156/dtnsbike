package com.dtnsbike.dao.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.ProductDetails;

@Service
public interface ProductDetailsService {

	List<ProductDetails> findAll();

	ProductDetails findById(Integer id);

	List<ProductDetails> findByProId(Integer id);

	void add(ProductDetails entity);

	void update(ProductDetails entity);

	void delete(Integer id);

	Optional<String> getProDtlByProIdAddClrId(Integer ProductId, String colorId);

	Optional<ProductDetails> getProDtlByProIdAddClrIdAndSze(Integer ProductId, String colorId, String sizeId);

	ProductDetails getProDltByProIdAndClrIdAndSzeId(Integer ProductId, String colorId, String sizeId);
}
