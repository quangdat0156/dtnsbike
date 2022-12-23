package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.ProductPriceHistories;

@Service
public interface ProductPriceHisService {
    
    List<ProductPriceHistories> findAll();
    
    ProductPriceHistories findById(Integer id);
    
    void add(ProductPriceHistories entity);
    
    void update(ProductPriceHistories entity);
    
    void delete(Integer id);
    
    
}
