package com.dtnsbike.dao.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.dtnsbike.entity.Categories;

@Service
public interface CategoryService {
    
    List<Categories> findAll();
    
    List<Categories> findAll2();
    
    Categories check(Integer id);
    
    Categories findById(Integer id);
    
    Integer getCount(Integer id);
    
    void add(Categories entity);
    
    void update(Categories entity);
    
    void delete(Integer id);
    
    List<Categories> findByTypeId(Integer typeId);
    
}
