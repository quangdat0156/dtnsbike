package com.dtnsbike.dao.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dtnsbike.entity.Colors;

public interface ColorsDAO extends JpaRepository<Colors,String>{
    @Query("SELECT o FROM Colors o WHERE o.id IN (SELECT a.colorid.id FROM ProductDetails a WHERE a.productid IN "
            + "(SELECT b.id FROM Products b))")
    List<Colors> findAllInProduct();
    
    @Query("SELECT o.colorid FROM ProductDetails o WHERE o.colorid.id = ?1")
    Colors checkInProduct(String id);
    
    @Query(value = "select * from colors order by colorid desc", nativeQuery = true)
    List<Colors> findAll();
}
