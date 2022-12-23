package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.StatusDAO;
import com.dtnsbike.dao.service.StatusService;
import com.dtnsbike.entity.Status;
@Service
public class StatusServiceImp implements StatusService  {

    @Autowired
    StatusDAO daoStat;
    
    @Override
    public List<Status> findAll() {
        return daoStat.findAll();
    }
    
    @Override
    public Status findById(String id) {
        return daoStat.findById(id).get();
    }
    
    @Override
    public void add(Status entity) {
        daoStat.save(entity);
    }
    
    @Override
    public void update(Status entity) {
        daoStat.save(entity);
    }
    
    @Override
    public void delete(String id) {
        daoStat.deleteById(id);
    }
    
}
