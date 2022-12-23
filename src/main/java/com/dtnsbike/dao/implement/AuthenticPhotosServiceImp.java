package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.AuthenticPhotosDAO;
import com.dtnsbike.dao.service.AuthenticPhotosService;
import com.dtnsbike.entity.AuthenticPhotos;
@Service
public class AuthenticPhotosServiceImp implements AuthenticPhotosService  {

    @Autowired
    AuthenticPhotosDAO daoAP;
    
    @Override
    public List<AuthenticPhotos> findAll() {
        return daoAP.findAll();
    }
    
    @Override
    public AuthenticPhotos findById(String id) {
        return daoAP.findById(id).get();
    }
    
    @Override
    public void add(AuthenticPhotos entity) {
        daoAP.save(entity);
    }
    
    @Override
    public void update(AuthenticPhotos entity) {
        daoAP.save(entity);
    }
    
    @Override
    public void delete(String id) {
        daoAP.deleteById(id);
    }
    
}
