package com.dtnsbike.dao.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dtnsbike.entity.Carts;

public interface CartsDAO extends JpaRepository<Carts, String>{
    @Query(value = "SELECT o FROM Carts o WHERE o.userCart.username=?1")
    List<Carts> findAllbyUsername(String username);
    
    @Query(value = "SELECT o FROM Carts o WHERE o.id=?1 and o.userCart.id=?2")
    Optional<Carts> findByIdandUser(String cartId,String username);
    
    @Query(value = "SELECT o FROM Carts o WHERE o.proCart.id=?1 and o.userCart.id=?2")
    Optional<Carts> findByProductDetailIdandUser(Integer proDetailId,String username);
}
