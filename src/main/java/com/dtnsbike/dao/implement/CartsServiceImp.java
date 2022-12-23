package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.CartsDAO;
import com.dtnsbike.dao.service.CartsService;
import com.dtnsbike.entity.Carts;
@Service
public class CartsServiceImp implements CartsService  {

    @Autowired
    CartsDAO daoCart;
    
    @Override
    public List<Carts> findAll() {
        return daoCart.findAll();
    }
    
    @Override
    public Carts findById(String id) {
        if(daoCart.findById(id).isPresent()) {
            return daoCart.findById(id).get();
        } else {
            return null;
        }
    }
    @Override
    public Carts findProductDetailId(Integer id,String username) {
        if(daoCart.findByProductDetailIdandUser(id,username).isPresent()) {
            return daoCart.findByProductDetailIdandUser(id,username).get();
        } else {
            return null;
        }
    }
    @Override
    public List<Carts> findAllbyUser(String id) {
    	if(daoCart.findAllbyUsername(id)!=null) {
			return daoCart.findAllbyUsername(id);
    	}else {
        return  null;}
    }
    
    @Override
    public void add(Carts entity) {
        daoCart.save(entity);
    }
    
    @Override
    public void update(Carts entity) {
        daoCart.save(entity);
    }
    
    @Override
    public void delete(String id) {
        daoCart.deleteById(id);
    }

	@Override
	public Carts findIdbyUser(String id, String username) {
		 if(daoCart.findByIdandUser(id,username).isPresent()) {
	            return daoCart.findByIdandUser(id,username).get();
	        } else {
	            return null;
	        }
	}

    
}
