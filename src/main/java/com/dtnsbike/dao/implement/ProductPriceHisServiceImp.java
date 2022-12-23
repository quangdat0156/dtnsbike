package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.ProductPriceHistoriesDAO;
import com.dtnsbike.dao.service.ProductPriceHisService;
import com.dtnsbike.entity.ProductPriceHistories;
@Service
public class ProductPriceHisServiceImp implements ProductPriceHisService  {

    @Autowired
    ProductPriceHistoriesDAO daoAcc;
    
    @Override
    public List<ProductPriceHistories> findAll() {
        return daoAcc.findAll();
    }
    
    @Override
    public ProductPriceHistories findById(Integer id) {
        return daoAcc.findById(id).get();
    }
    
    @Override
    public void add(ProductPriceHistories entity) {
        daoAcc.save(entity);
    }
    
    @Override
    public void update(ProductPriceHistories entity) {
        daoAcc.save(entity);
    }
    
    @Override
    public void delete(Integer id) {
        daoAcc.deleteById(id);
    }
    
}
