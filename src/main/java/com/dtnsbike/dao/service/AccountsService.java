package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Accounts;

@Service
public interface AccountsService {
    
    List<Accounts> findAll();
    
    Accounts findById(String id);
    
    void add(Accounts entity);
    
    void update(Accounts entity);
    
    void delete(String id);
    
    
}
