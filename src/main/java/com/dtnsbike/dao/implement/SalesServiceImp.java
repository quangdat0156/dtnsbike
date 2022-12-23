package com.dtnsbike.dao.implement;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.SalesDAO;
import com.dtnsbike.dao.service.SalesService;
import com.dtnsbike.entity.Sales;


@Service
public class SalesServiceImp implements SalesService {

    @Autowired
    SalesDAO daoSale;
    
    @Override
    public List<Sales> findAll() {
        return daoSale.findAll();
    }
    
    @Override
    public Optional<Sales> findById(String id) {
        return daoSale.findById(id);
    }
    
    @Override
    public Sales checkOrder(String id) {
    	// TODO Auto-generated method stub
    	return daoSale.checkinOrder(id);
    }
    
    @Override
    public void add(Sales entity) {
        daoSale.save(entity);
    }
    
    @Override
    public void update(Sales entity) {
        daoSale.save(entity);
    }
    
    @Override
    public void delete(String id) {
        daoSale.deleteById(id);
    }
    

}
