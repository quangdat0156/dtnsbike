package com.dtnsbike.dao.implement;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.ColorsDAO;
import com.dtnsbike.dao.service.ColorsService;
import com.dtnsbike.entity.Colors;
@Service
public class ColorsServiceImp implements ColorsService  {

    @Autowired
    ColorsDAO daoCol;
    
    @Override
    public List<Colors> findAll() {
        return daoCol.findAll();
    }
    
    @Override
    public Colors check(String id) {
    	return daoCol.checkInProduct(id);
    }
    
    @Override
    public List<Colors> findInProduct() {
        return daoCol.findAllInProduct();
    }
    @Override
    public Optional<Colors> findById(String id) {
        return daoCol.findById(id);
    }
    
    @Override
    public void add(Colors entity) {
        daoCol.save(entity);
    }
    
    @Override
    public void update(Colors entity) {
        daoCol.save(entity);
    }
    
    @Override
    public void delete(String id) {
        daoCol.deleteById(id);
    }
    
}
