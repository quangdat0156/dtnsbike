package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.ProductsDAO;
import com.dtnsbike.dao.service.ProductsService;
import com.dtnsbike.entity.Products;
@Service
public class ProductsServiceImp implements ProductsService  {

    @Autowired
    ProductsDAO daoProduct;
    
    @Override
    public List<Products> findAll() {
        return daoProduct.findAll();
    }
    
    @Override
    public Page<Products> findAll(String keyword, Pageable pageable) {
        return daoProduct.findAllBynameLike(keyword, pageable);
    }
    
    @Override
    public Page<Products> findAll(String keyword, Integer id, Pageable pageable) {
        return daoProduct.findAllByCate(keyword, id, pageable);
    }
    
    @Override
    public Page<Products> findAllRecom(Integer proid, Integer id, Pageable pageable) {
        return daoProduct.findAllRecom(proid, id, pageable);
    }
    
    @Override
    public Page<Products> findAllBrand(String keyword, Integer id, Pageable pageable) {
        return daoProduct.findAllByBrand(keyword, id, pageable);
    }
    
    @Override
    public Page<Products> findAllColor(String keyword, String id, Pageable pageable) {
        return daoProduct.findAllByColor(keyword, id, pageable);
    }
    
    @Override
    public Page<Products> findAllSize(String keyword, String id, Pageable pageable) {
        return daoProduct.findAllBySize(keyword, id, pageable);
    }
//  Sale Products DAO
    @Override
    public List<Products> findSale() {
        return daoProduct.findAllSale();
    }
    
    @Override
    public Page<Products> findSale(String keyword, Pageable pageable) {
        return daoProduct.findAllSaleBynameLike(keyword, pageable);
    }
    
    @Override
    public Page<Products> findAllBrand2(String keyword, Integer id, Pageable pageable) {
        return daoProduct.findAllByBrand2(keyword, id, pageable);
    }
    
    @Override
    public Page<Products> findAllSaleCate(String keyword, Integer id, Pageable pageable) {
        return daoProduct.findAllByCate2(keyword, id, pageable);
    }
    
    @Override
    public Page<Products> findAllColor2(String keyword, String id, Pageable pageable) {
        return daoProduct.findAllByColor2(keyword, id, pageable);
    }
    
    @Override
    public Page<Products> findAllSize2(String keyword, String id, Pageable pageable) {
        return daoProduct.findAllBySize2(keyword, id, pageable);
    }
    
    @Override
    public List<Products> findSale(Pageable pageable) {
        return daoProduct.findAllSale(pageable);
    }
//  Sale Products DAO
    @Override
    public List<Products> findTop(Pageable pageable) {
        return daoProduct.findTop(pageable);
    }
    
    @Override
    public Products findById(Integer id) {
    	if(!daoProduct.findById(id).isPresent()) {
    		return null;
    	}
        return daoProduct.findById(id).get();
    }
    
    @Override
    public void add(Products entity) {
        daoProduct.save(entity);
    }
    
    @Override
    public void update(Products entity) {
        daoProduct.save(entity);
    }
    
    @Override
    public void delete(Integer id) {
        daoProduct.deleteById(id);
    }
    
}
