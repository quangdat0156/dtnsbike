package com.dtnsbike.dao.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Sizes;

@Service
public interface SizesService {
    
    List<Sizes> findAll();
    
    List<Sizes> findInProduct();
    
    Sizes checkInProduct(String id);
    
    Optional<Sizes> findById(String id);
    
    void add(Sizes entity);
    
    void update(Sizes entity);
    
    void delete(String id);
    
    
}
