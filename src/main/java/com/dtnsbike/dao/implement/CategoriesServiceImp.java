package com.dtnsbike.dao.implement;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dtnsbike.dao.interfaces.CategoriesDAO;
import com.dtnsbike.dao.service.CategoryService;
import com.dtnsbike.entity.Categories;

@Service
public class CategoriesServiceImp implements CategoryService  {

    @Autowired
    CategoriesDAO daoCate;
    
    @Override
    public List<Categories> findAll() {
        return daoCate.findAll();
    }
    
    @Override
    public Integer getCount(Integer id) {
    	return daoCate.getCount(id);
    }
    
    @Override
    public Categories check(Integer id) {
    	return daoCate.checkInProduct(id);
    }
    
    @Override
    public List<Categories> findAll2() {
        return daoCate.findAll2();
    }
    @Override
    public Categories findById(Integer id) {
        if(!daoCate.findById(id).isPresent()) {
        	return null;
        }
        return daoCate.findById(id).get();
    }
    
    @Override
    public void add(Categories entity) {
        daoCate.save(entity);
    }
    
    @Override
    public void update(Categories entity) {
        daoCate.save(entity);
    }
    
    @Override
    public void delete(Integer id) {
        daoCate.deleteById(id);
    }

	@Override
	public List<Categories> findByTypeId(Integer typeId) {
		if(daoCate.findByTypeId(typeId)==null) {
			return null;
		}
		return daoCate.findByTypeId(typeId);
	}
    
}
