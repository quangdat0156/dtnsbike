package com.dtnsbike.dao.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dtnsbike.entity.Sales;


public interface SalesDAO extends JpaRepository<Sales ,String> {
    @Query(value = "SELECT o FROM Sales o WHERE o.active=true")
    List<Sales> findAllActive();
    
    @Query("select count(DISTINCT saleid) from Sales")
    Integer countSale();
    
    @Query("select o.saleId from Orders o Where o.saleId.id = ?1 ")
    Sales checkinOrder(String id);
}
