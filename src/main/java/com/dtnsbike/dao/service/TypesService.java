package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Types;

@Service
public interface TypesService {
    
    List<Types> findAll();
    
    List<Types> findAllInProduct();
    
    Types check(Integer id);
    
    Integer getCount(Types id);
    
    Types findById(Integer id);
    
    void add(Types entity);
    
    void update(Types entity);
    
    void delete(Integer id);
    
    
}
