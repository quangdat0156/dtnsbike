package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.TypesDAO;
import com.dtnsbike.dao.service.TypesService;
import com.dtnsbike.entity.Types;
@Service
public class TypesServiceImp implements TypesService  {

    @Autowired
    TypesDAO daoTyp;
    
    @Override
    public List<Types> findAll() {
        return daoTyp.findAll();
    }
    
    @Override
    public Types check(Integer id) {
    	return daoTyp.checkInCate(id);
    }
    
    @Override
    public List<Types> findAllInProduct() {
        return daoTyp.findAllInProduct();
    }
    @Override
    public Integer getCount(Types id) {
        return daoTyp.getCountPro(id);
    }
    
    @Override
    public Types findById(Integer id) {
        return daoTyp.findById(id).get();
    }
    
    @Override
    public void add(Types entity) {
        daoTyp.save(entity);
    }
    
    @Override
    public void update(Types entity) {
        daoTyp.save(entity);
    }
    
    @Override
    public void delete(Integer id) {
        daoTyp.deleteById(id);
    }
    
}
