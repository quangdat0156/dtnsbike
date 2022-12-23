package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.BrandsDAO;
import com.dtnsbike.dao.service.BrandsService;
import com.dtnsbike.entity.Brands;
@Service
public class BrandsServiceImp implements BrandsService  {

    @Autowired
    BrandsDAO daoBrand;
    
    @Override
    public List<Brands> findAll() {
        return daoBrand.findAll();
    }
    @Override
    public List<Brands> findInProduct() {
        return daoBrand.findAllInProduct();
    }
    @Override
    public Brands findById(Integer id) {
        if(!daoBrand.findById(id).isPresent()) {
        	return null;
        }
        return daoBrand.findById(id).get();
    }
    
    @Override
    public void add(Brands entity) {
        daoBrand.save(entity);
    }
    
    @Override
    public void update(Brands entity) {
        daoBrand.save(entity);
    }
    
    @Override
    public void delete(Integer id) {
        daoBrand.deleteById(id);
    }
    
    @Override
    public Brands check(Integer id) {
    	return daoBrand.findByInPro(id);
    }
}
