package com.dtnsbike.dao.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Orders;
import com.dtnsbike.entity.Products;

public interface ProductsDAO extends JpaRepository<Products, Integer>{

    @Query("SELECT o FROM Products o WHERE o.discountid.value != null AND o.discountid.value > 0 And o.avaliable = true")
    List<Products> findAllSale();
      
    @Query("SELECT o FROM Products o WHERE o.avaliable = true ORDER BY o.id DESC")
    List<Products> findTop(Pageable pageable);
    
    @Query("SELECT o FROM Products o WHERE o.discountid.value != null AND o.discountid.value > 0 And o.avaliable = true ORDER BY o.price DESC")
    List<Products> findAllSale(Pageable pageable);
    
    Page<Products> findAllBynameLike(String keywords, Pageable pageable);
    
    @Query("SELECT o FROM Products o WHERE o.name LIKE ?1 AND o.catePro.id = ?2 And o.avaliable = true")
    Page<Products> findAllByCate(String keywords,Integer id, Pageable pageable);
    
    @Query("SELECT o FROM Products o WHERE o.id != ?1 AND o.catePro.id = ?2 And o.avaliable = true")
    Page<Products> findAllRecom(Integer proid,Integer id, Pageable pageable);
    
    @Query("SELECT o FROM Products o WHERE o.name LIKE ?1 AND o.brandPro.id = ?2 And o.avaliable = true")
    Page<Products> findAllByBrand(String keywords,Integer id, Pageable pageable);
    
    @Query("SELECT o.productid FROM  ProductDetails o WHERE o.productid.name LIKE ?1 AND o.colorid.id = ?2 And o.productid.avaliable = true")
    Page<Products> findAllByColor(String keywords,String id, Pageable pageable);
    
    @Query("SELECT o.productid FROM  ProductDetails o WHERE o.productid.name LIKE ?1 AND o.sizeid.id = ?2 And o.productid.avaliable = true")
    Page<Products> findAllBySize(String keywords,String id, Pageable pageable);
    
    @Query("select count(DISTINCT productid) from Products where avaliable = true")
    Integer countProduct();
    
//  Products Sale start sql
    @Query("SELECT o FROM Products o WHERE o.discountid.value != null AND o.discountid.value > 0 And o.avaliable = true AND o.name LIKE ?1")
    Page<Products> findAllSaleBynameLike(String keywords, Pageable pageable);
    
    @Query("SELECT o FROM Products o WHERE o.discountid.value != null AND o.discountid.value > 0 And o.avaliable = true AND o.name LIKE ?1 AND o.catePro.id = ?2")
    Page<Products> findAllByCate2(String keywords,Integer id, Pageable pageable);
    
    @Query("SELECT o FROM Products o WHERE o.discountid.value != null AND o.discountid.value > 0 And o.avaliable = true AND o.name LIKE ?1 AND o.brandPro.id = ?2")
    Page<Products> findAllByBrand2(String keywords,Integer id, Pageable pageable);
    
    @Query("SELECT o.productid FROM  ProductDetails o WHERE o.productid.discountid.value != null And o.productid.avaliable = true AND o.productid.discountid.value > 0 AND o.productid.name LIKE ?1 AND o.colorid.id = ?2")
    Page<Products> findAllByColor2(String keywords,String id, Pageable pageable);
    
    @Query("SELECT o.productid FROM  ProductDetails o WHERE o.productid.discountid.value != null And o.productid.avaliable = true AND o.productid.discountid.value > 0 AND o.productid.name LIKE ?1 AND o.sizeid.id = ?2")
    Page<Products> findAllBySize2(String keywords,String id, Pageable pageable);
}
