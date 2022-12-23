package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.AccountsDAO;
import com.dtnsbike.dao.service.AccountsService;
import com.dtnsbike.entity.Accounts;
@Service
public class AccountsServiceImp implements AccountsService  {

    @Autowired
    AccountsDAO daoAcc;
    
    @Override
    public List<Accounts> findAll() {
        return daoAcc.findAll();
    }
    
    @Override
    public Accounts findById(String id) {
        return daoAcc.findById(id).get();
    }
    
    @Override
    public void add(Accounts entity) {
        daoAcc.save(entity);
    }
    
    @Override
    public void update(Accounts entity) {
        daoAcc.save(entity);
    }
    
    @Override
    public void delete(String id) {
        daoAcc.deleteById(id);
    }
    
}
