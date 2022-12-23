package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Favorites;
import com.dtnsbike.entity.Products;

@Service
public interface FavoritesService {
    
    List<Favorites> findAll();
    
    List<Favorites> findAllUser(Accounts ac);
    
    List<Favorites> findFav(Accounts ac,Products pro);
    
    Boolean checkProFav(Accounts ac,Products pro);
    
    Favorites findById(Integer id);
    
    void add(Favorites entity);
    
    void update(Favorites entity);
    
    void delete(Integer id);
    
    
}
