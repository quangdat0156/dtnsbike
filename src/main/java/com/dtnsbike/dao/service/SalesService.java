package com.dtnsbike.dao.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Sales;

@Service
public interface SalesService {
    
    List<Sales> findAll();
    
    Optional<Sales> findById(String id);
    
    Sales checkOrder(String id);
    
    void add(Sales entity);
    
    void update(Sales entity);
    
    void delete(String id);
    
    
}
