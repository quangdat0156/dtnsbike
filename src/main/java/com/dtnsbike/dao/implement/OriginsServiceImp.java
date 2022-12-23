package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.OriginsDAO;
import com.dtnsbike.dao.service.OriginsService;
import com.dtnsbike.entity.Origins;
@Service
public class OriginsServiceImp implements OriginsService  {

    @Autowired
    OriginsDAO daoOrigin;
    
    @Override
    public List<Origins> findAll() {
        return daoOrigin.findAll();
    }
    
    @Override
    public Origins findById(Integer id) {
        if(!daoOrigin.findById(id).isPresent()) {
        	return null;
        }else {
        	return daoOrigin.findById(id).get();
		}
    }
    
    @Override
    public void add(Origins entity) {
        daoOrigin.save(entity);
    }
    
    @Override
    public void update(Origins entity) {
        daoOrigin.save(entity);
    }
    
    @Override
    public void delete(Integer id) {
        daoOrigin.deleteById(id);
    }
    
}
