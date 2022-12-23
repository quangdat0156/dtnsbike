package com.dtnsbike.dao.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Blogs;

@Service
public interface BlogsService {
    
    List<Blogs> findAll();
    
    List<Blogs> findTop(Pageable pageable);
    
    List<Blogs> findTop2(Pageable pageable);
    
    Boolean checkBlog(String id);
    
    Optional<Blogs> findById(Integer id);
    
    void add(Blogs entity);
    
    void update(Blogs entity);
    
    void delete(Integer id);
    
    
}
