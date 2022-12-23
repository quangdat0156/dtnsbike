package com.dtnsbike.dao.interfaces;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.dtnsbike.entity.Sizes;

public interface SizesDAO extends JpaRepository<Sizes ,String > {

    @Query("SELECT o FROM Sizes o WHERE o.id IN (SELECT a.sizeid.id FROM ProductDetails a WHERE a.productid IN "
            + "(SELECT b FROM Products b))")
    List<Sizes> findAllInProduct();
    
    @Query("SELECT o.sizeid FROM ProductDetails o WHERE o.sizeid.id = ?1")
    Sizes checkInProduct(String id);
    
    @Query(value = "select * from Sizes order by sizeId desc", nativeQuery = true)
    List<Sizes> findAll();
}
