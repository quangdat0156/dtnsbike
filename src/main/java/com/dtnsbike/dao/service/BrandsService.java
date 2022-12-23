package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Brands;

@Service
public interface BrandsService {
    
    List<Brands> findAll();
    
    List<Brands> findInProduct();
    
    Brands check(Integer id);
    
    Brands findById(Integer id);
    
    void add(Brands entity);
    
    void update(Brands entity);
    
    void delete(Integer id);
    
    
}
