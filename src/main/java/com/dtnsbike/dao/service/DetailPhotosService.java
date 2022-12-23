package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.DetailPhotos;

@Service
public interface DetailPhotosService {
    
    List<DetailPhotos> findAll();
    
    DetailPhotos findById(String id);
    
    void add(DetailPhotos entity);
    
    void update(DetailPhotos entity);
    
    void delete(String id);
    
    List<String> findByProId(Integer id);
    
    DetailPhotos findByProIdAndFileName(String proId,String filename);
}
