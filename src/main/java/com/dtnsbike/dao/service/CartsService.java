package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Carts;

@Service
public interface CartsService {
    
    List<Carts> findAll();
    
    Carts findById(String id);
    
    List<Carts> findAllbyUser(String id);
    
    void add(Carts entity);
    
    void update(Carts entity);
    
    void delete(String id);

	Carts findProductDetailId(Integer id, String username);
	
	Carts findIdbyUser(String id, String username);
    
    
}
