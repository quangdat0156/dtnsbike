package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Products;

@Service
public interface ProductsService {

    List<Products> findAll();

    Page<Products> findAll(String keyword, Pageable pageable);

    Page<Products> findAll(String keyword, Integer id, Pageable pageable);
    
    Page<Products> findAllRecom(Integer proid,Integer id,Pageable pageable);

    Page<Products> findAllBrand(String keyword, Integer id, Pageable pageable);

    Page<Products> findAllColor(String keyword, String id, Pageable pageable);

    Page<Products> findAllSize(String keyword, String id, Pageable pageable);

//  Sale Product SQL
    List<Products> findSale();

    Page<Products> findSale(String keyword, Pageable pageable);

    Page<Products> findAllSaleCate(String keyword, Integer id, Pageable pageable);

    Page<Products> findAllBrand2(String keyword, Integer id, Pageable pageable);

    Page<Products> findAllColor2(String keyword, String id, Pageable pageable);

    Page<Products> findAllSize2(String keyword, String id, Pageable pageable);

    List<Products> findSale(Pageable pageable);

//  Sale Product SQL

    List<Products> findTop(Pageable pageable);

    Products findById(Integer id);

    void add(Products entity);

    void update(Products entity);

    void delete(Integer id);

}
