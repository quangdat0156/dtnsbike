package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.FavoritesDAO;
import com.dtnsbike.dao.service.FavoritesService;
import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Favorites;
import com.dtnsbike.entity.Products;
@Service
public class FavoritesServiceImp implements FavoritesService  {

    @Autowired
    FavoritesDAO daoFav;
    
    @Override
    public List<Favorites> findAll() {
        return daoFav.findAll();
    }
    
    @Override
    public List<Favorites> findAllUser(Accounts acc) {
        return daoFav.findByUserFvr(acc);
    }
    
    @Override
    public List<Favorites> findFav(Accounts ac, Products pro) {
        return daoFav.checkProFav(ac, pro);
    }
    
    @Override
    public Boolean checkProFav(Accounts ac, Products pro) {
        return !daoFav.checkProFav(ac, pro).isEmpty();
    }
    
    @Override
    public Favorites findById(Integer id) {
        return daoFav.findById(id).get();
    }
    
    @Override
    public void add(Favorites entity) {
        daoFav.save(entity);
    }
    
    @Override
    public void update(Favorites entity) {
        daoFav.save(entity);
    }
    
    @Override
    public void delete(Integer id) {
        daoFav.deleteById(id);
    }
    
}
