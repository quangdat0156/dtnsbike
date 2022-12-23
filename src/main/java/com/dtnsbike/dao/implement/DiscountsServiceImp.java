package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.DiscountsDAO;
import com.dtnsbike.dao.service.DiscountsService;
import com.dtnsbike.entity.Discounts;
@Service
public class DiscountsServiceImp implements DiscountsService  {

    @Autowired
    DiscountsDAO daoDis;
    
    @Override
    public List<Discounts> findAll() {
        return daoDis.findAll();
    }
    
    @Override
    public Discounts findById(Integer id) {
		if (!daoDis.findById(id).isPresent()) {
			return null;
        }return daoDis.findById(id).get();
    }
    
    @Override
    public void add(Discounts entity) {
        daoDis.save(entity);
    }
    
    @Override
    public void update(Discounts entity) {
        daoDis.save(entity);
    }
    
    @Override
    public void delete(Integer id) {
        daoDis.deleteById(id);
    }
    
}
