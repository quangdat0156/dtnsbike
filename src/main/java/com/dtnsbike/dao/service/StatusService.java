package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Status;

@Service
public interface StatusService {
    
    List<Status> findAll();
    
    Status findById(String id);
    
    void add(Status entity);
    
    void update(Status entity);
    
    void delete(String id);
    
    
}
