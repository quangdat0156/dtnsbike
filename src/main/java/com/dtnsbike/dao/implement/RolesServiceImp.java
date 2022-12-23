package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.RolesDAO;
import com.dtnsbike.dao.service.RolesService;
import com.dtnsbike.entity.Roles;
@Service
public class RolesServiceImp implements RolesService  {

    @Autowired
    RolesDAO daoRole;
    
    @Override
    public List<Roles> findAll() {
        return daoRole.findAll();
    }
    
    @Override
    public Roles findById(String id) {
        return daoRole.findById(id).get();
    }
    
    @Override
    public void add(Roles entity) {
        daoRole.save(entity);
    }
    
    @Override
    public void update(Roles entity) {
        daoRole.save(entity);
    }
    
    @Override
    public void delete(String id) {
        daoRole.deleteById(id);
    }
    
}
