package com.dtnsbike.dao.interfaces;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.dtnsbike.entity.Categories;


public interface CategoriesDAO extends JpaRepository<Categories, Integer>{

    @Query("SELECT o FROM Categories o WHERE o IN (SELECT a.catePro FROM Products a)")
    List<Categories> findAll2();
    
    @Query("SELECT o.catePro FROM Products o WHERE o.catePro.id = ?1")
    Categories checkInProduct(Integer id);
    
    @Query(value = "select * from categories where typeid =?1", nativeQuery = true)
    List<Categories> findByTypeId(Integer typeId);
    
    @Query("SELECT count(o) FROM Products o WHERE o.avaliable = true AND o.catePro.id = ?1")
    Integer getCount(Integer id);
}
