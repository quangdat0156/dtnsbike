package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Roles;

@Service
public interface RolesService {
    
    List<Roles> findAll();
    
    Roles findById(String id);
    
    void add(Roles entity);
    
    void update(Roles entity);
    
    void delete(String id);
    
    
}
