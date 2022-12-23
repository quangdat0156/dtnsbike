package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Contacts;

@Service
public interface ContactsService {
    
    List<Contacts> findAll();
    
    Contacts findById(Integer id);
    
    void add(Contacts entity);
    
    void update(Contacts entity);
    
    void delete(Integer id);
    
    
}
