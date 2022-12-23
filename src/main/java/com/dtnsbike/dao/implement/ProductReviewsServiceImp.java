package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.ProductReviewsDAO;
import com.dtnsbike.dao.service.ProductReviewsService;
import com.dtnsbike.entity.ProductReviews;
@Service
public class ProductReviewsServiceImp implements ProductReviewsService  {

    @Autowired
    ProductReviewsDAO daoPR;
    
    @Override
    public List<ProductReviews> findAll() {
        return daoPR.findAll();
    }
    
    @Override
    public ProductReviews findById(Integer id) {
        return daoPR.findById(id).get();
    }
    
    @Override
    public void add(ProductReviews entity) {
        daoPR.save(entity);
    }
    
    @Override
    public void update(ProductReviews entity) {
        daoPR.save(entity);
    }
    
    @Override
    public void delete(Integer id) {
        daoPR.deleteById(id);
    }
    
}
