package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Origins;

@Service
public interface OriginsService {
    
    List<Origins> findAll();
    
    Origins findById(Integer id);
    
    void add(Origins entity);
    
    void update(Origins entity);
    
    void delete(Integer id);
    
    
}
