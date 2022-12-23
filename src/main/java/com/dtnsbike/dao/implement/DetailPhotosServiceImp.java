package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.DetailPhotosDAO;
import com.dtnsbike.dao.service.DetailPhotosService;
import com.dtnsbike.entity.DetailPhotos;
@Service
public class DetailPhotosServiceImp implements DetailPhotosService  {

    @Autowired
    DetailPhotosDAO daoDP;
    
    @Override
    public List<DetailPhotos> findAll() {
        return daoDP.findAll();
    }
    
    @Override
    public DetailPhotos findById(String id) {
        return daoDP.findById(id).get();
    }
    
    @Override
    public void add(DetailPhotos entity) {
        daoDP.save(entity);
    }
    
    @Override
    public void update(DetailPhotos entity) {
        daoDP.save(entity);
    }
    
    @Override
    public void delete(String id) {
        daoDP.deleteById(id);
    }

	@Override
	public List<String> findByProId(Integer id) {
		return daoDP.findByProId(id);
	}

	@Override
	public DetailPhotos findByProIdAndFileName(String proId, String filename) {
		return daoDP.findByProIdAndFileName(proId, filename);
	}
    
}
