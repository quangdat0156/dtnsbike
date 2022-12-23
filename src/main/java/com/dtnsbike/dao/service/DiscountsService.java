package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Discounts;

@Service
public interface DiscountsService {
    
    List<Discounts> findAll();
    
    Discounts findById(Integer id);
    
    void add(Discounts entity);
    
    void update(Discounts entity);
    
    void delete(Integer id);
    
    
}
