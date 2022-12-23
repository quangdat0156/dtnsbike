package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Warranties;

@Service
public interface WarrantiesService {
    
    List<Warranties> findAll();
    
    Warranties findById(Integer id);
    
    Warranties findFrameNumber(String id);
    
    Warranties findOrId(Integer id);
    
    void add(Warranties entity);
    
    void update(Warranties entity);
    
    void delete(Integer id);
    
    
}
