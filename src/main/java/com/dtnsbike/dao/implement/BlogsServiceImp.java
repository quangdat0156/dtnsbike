package com.dtnsbike.dao.implement;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.BlogsDAO;
import com.dtnsbike.dao.service.BlogsService;
import com.dtnsbike.entity.Blogs;
@Service
public class BlogsServiceImp implements BlogsService  {

    @Autowired
    BlogsDAO daoBlog;
    
    @Override
    public List<Blogs> findAll() {
        return daoBlog.findAll();
    }
    @Override
    public List<Blogs> findTop(Pageable pageable) {
        return daoBlog.findAllTop(pageable);
    }

    @Override
    public List<Blogs> findTop2(Pageable pageable) {
        return daoBlog.findTop(pageable);
    }
    
    @Override
    public Optional<Blogs> findById(Integer id) {
        return daoBlog.findById(id);
    }
    
    @Override
    public Boolean checkBlog(String id) {
    	return !daoBlog.check(id).isEmpty();
    }
    
    @Override
    public void add(Blogs entity) {
        daoBlog.save(entity);
    }
    
    @Override
    public void update(Blogs entity) {
        daoBlog.save(entity);
    }
    
    @Override
    public void delete(Integer id) {
        daoBlog.deleteById(id);
    }
    
}
