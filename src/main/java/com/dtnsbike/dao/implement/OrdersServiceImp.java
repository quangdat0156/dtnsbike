package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.OrdersDAO;
import com.dtnsbike.dao.service.OrdersService;
import com.dtnsbike.entity.Orders;
@Service
public class OrdersServiceImp implements OrdersService  {

    @Autowired
    OrdersDAO daoOrder;
    
    @Override
    public List<Orders> findAll() {
        return daoOrder.findAll();
    }
    
    @Override
    public List<Orders> allReorder() {
    	return daoOrder.allReCancel();
    }
    
    @Override
    public List<Orders> findWait() {
    	return daoOrder.allOrderWait();
    }
    
    @Override
    public List<Orders> findWait1() {
    	return daoOrder.allOrderWait1();
    }
    
    @Override
    public List<Orders> findShipp() {
    	return daoOrder.allOrderShipp();
    }
    
    @Override
    public List<Orders> findDone() {
    	return daoOrder.allOrderDone();
    }
    
    @Override
    public List<Orders> findCancel() {
    	return daoOrder.allOrderCancel();
    }
    
    @Override
    public Orders findById(Integer id) {
        return daoOrder.findById(id).get();
    }
    
    @Override
    public void add(Orders entity) {
        daoOrder.save(entity);
    }
    
    @Override
    public void update(Orders entity) {
        daoOrder.save(entity);
    }
    
    @Override
    public void delete(Integer id) {
        daoOrder.deleteById(id);
    }
    
}
