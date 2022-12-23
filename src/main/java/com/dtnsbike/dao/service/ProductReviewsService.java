package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.ProductReviews;

@Service
public interface ProductReviewsService {
    
    List<ProductReviews> findAll();
    
    ProductReviews findById(Integer id);
    
    void add(ProductReviews entity);
    
    void update(ProductReviews entity);
    
    void delete(Integer id);
    
    
}
