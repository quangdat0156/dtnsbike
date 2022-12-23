package com.dtnsbike.dao.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dtnsbike.entity.Brands;

public interface BrandsDAO extends JpaRepository<Brands, Integer>{
    @Query("SELECT o FROM Brands o WHERE o IN (SELECT a.brandPro FROM Products a)")
    List<Brands> findAllInProduct();
    
    @Query("SELECT a.brandPro FROM Products a WHERE a.brandPro.brandid = ?1")
    Brands findByInPro(Integer brandid);
}
