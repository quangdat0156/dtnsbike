package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.ContactsDAO;
import com.dtnsbike.dao.service.ContactsService;
import com.dtnsbike.entity.Contacts;
@Service
public class ContactsServiceImp implements ContactsService  {

    @Autowired
    ContactsDAO daoCont;
    
    @Override
    public List<Contacts> findAll() {
        return daoCont.findAll();
    }
    
    @Override
    public Contacts findById(Integer id) {
        return daoCont.findById(id).get();
    }
    
    @Override
    public void add(Contacts entity) {
        daoCont.save(entity);
    }
    
    @Override
    public void update(Contacts entity) {
        daoCont.save(entity);
    }
    
    @Override
    public void delete(Integer id) {
        daoCont.deleteById(id);
    }
    
}
