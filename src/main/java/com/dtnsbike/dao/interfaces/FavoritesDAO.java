package com.dtnsbike.dao.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Favorites;
import com.dtnsbike.entity.Products;


public interface FavoritesDAO extends JpaRepository<Favorites, Integer>{

    List<Favorites> findByUserFvr(Accounts userFvr);
    
    @Query("SELECT o FROM Favorites o WHERE o.userFvr = ?1 and o.productsId = ?2")
    List<Favorites> checkProFav(Accounts ac,Products id);
    
    
    
}
