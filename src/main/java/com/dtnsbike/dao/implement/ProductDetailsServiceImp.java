package com.dtnsbike.dao.implement;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.ProductDetailsDAO;
import com.dtnsbike.dao.service.ProductDetailsService;
import com.dtnsbike.entity.ProductDetails;

@Service
public class ProductDetailsServiceImp implements ProductDetailsService {

	@Autowired
	ProductDetailsDAO daoPrD;

	@Override
	public List<ProductDetails> findAll() {
		return daoPrD.findAll();
	}

	@Override
	public ProductDetails findById(Integer id) {
		return daoPrD.findById(id).get();
	}

	@Override
	public void add(ProductDetails entity) {
		daoPrD.save(entity);
	}

	@Override
	public void update(ProductDetails entity) {
		daoPrD.save(entity);
	}

	@Override
	public void delete(Integer id) {
		daoPrD.deleteById(id);
	}

	@Override
	public List<ProductDetails> findByProId(Integer id) {
		return daoPrD.getProdetailsByProID(id);
	}

	@Override
	public Optional<String> getProDtlByProIdAddClrId(Integer ProductId, String colorId) {
		return daoPrD.getProDtlByProIdAddClrId(ProductId, colorId);
	}

	@Override
	public Optional<ProductDetails> getProDtlByProIdAddClrIdAndSze(Integer ProductId, String colorId, String sizeId) {
		if (!daoPrD.getProDtlByProIdAddClrIdAndSze(ProductId, colorId, sizeId).isPresent()) {
			return Optional.empty();
		}
		return daoPrD.getProDtlByProIdAddClrIdAndSze(ProductId, colorId, sizeId);
	}

	@Override
	public ProductDetails getProDltByProIdAndClrIdAndSzeId(Integer ProductId, String colorId, String sizeId) {

		return daoPrD.getProDltByProIdAndClrIdAndSzeId(ProductId, colorId, sizeId);
	}

}
