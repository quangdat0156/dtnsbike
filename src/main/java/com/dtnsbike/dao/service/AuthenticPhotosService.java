package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.AuthenticPhotos;

@Service
public interface AuthenticPhotosService {

    List<AuthenticPhotos> findAll();

    AuthenticPhotos findById(String id);

    void add(AuthenticPhotos entity);

    void update(AuthenticPhotos entity);

    void delete(String id);

}
