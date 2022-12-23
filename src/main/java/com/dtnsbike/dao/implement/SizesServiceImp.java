package com.dtnsbike.dao.implement;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.SizesDAO;
import com.dtnsbike.dao.service.SizesService;
import com.dtnsbike.entity.Sizes;

@Service
public class SizesServiceImp implements SizesService {

    @Autowired
    SizesDAO daoSize;

    @Override
    public List<Sizes> findAll() {
        return daoSize.findAll();
    }
    
    @Override
    public Sizes checkInProduct(String id) {
    	return daoSize.checkInProduct(id);
    }
    
    @Override
    public List<Sizes> findInProduct() {
        return daoSize.findAllInProduct();
    }

    @Override
    public Optional<Sizes> findById(String id) {
        return daoSize.findById(id);
    }

    @Override
    public void add(Sizes entity) {
        daoSize.save(entity);
    }

    @Override
    public void update(Sizes entity) {
        daoSize.save(entity);
    }

    @Override
    public void delete(String id) {
        daoSize.deleteById(id);
    }

}
