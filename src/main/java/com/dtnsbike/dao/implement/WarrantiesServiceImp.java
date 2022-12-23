package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.WarrantiesDAO;
import com.dtnsbike.dao.service.WarrantiesService;
import com.dtnsbike.entity.Warranties;
@Service
public class WarrantiesServiceImp implements WarrantiesService  {

    @Autowired
    WarrantiesDAO daoWarr;
    
    @Override
    public List<Warranties> findAll() {
        return daoWarr.findAll();
    }
    
    @Override
    public Warranties findFrameNumber(String id) {
    	return daoWarr.findByFramenumber(id);
    }
    
    @Override
    public Warranties findOrId(Integer id) {
    	return daoWarr.findByOrid(id);
    }
    
    @Override
    public Warranties findById(Integer id) {
        return daoWarr.findById(id).get();
    }
    
    @Override
    public void add(Warranties entity) {
        daoWarr.save(entity);
    }
    
    @Override
    public void update(Warranties entity) {
        daoWarr.save(entity);
    }
    
    @Override
    public void delete(Integer id) {
        daoWarr.deleteById(id);
    }
    
}
