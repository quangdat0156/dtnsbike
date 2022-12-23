package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Authorities;

@Service
public interface AuthoritiesService {

    List<Authorities> findAll();

    Authorities findById(Integer id);

    void add(Authorities entity);

    void update(Authorities entity);

    void delete(Integer id);
    
    List<String> getUsername();
    
    List<Authorities> getAuthoritiesByUsername(String username);
}
