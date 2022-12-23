package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.AuthoritiesDAO;
import com.dtnsbike.dao.service.AuthoritiesService;
import com.dtnsbike.entity.Authorities;

@Service
public class AuthoritiesServiceImp implements AuthoritiesService {

	@Autowired
	AuthoritiesDAO daoAuth;

	@Override
	public List<Authorities> findAll() {
		return daoAuth.findAll();
	}

	@Override
	public Authorities findById(Integer id) {
		return daoAuth.findById(id).get();
	}

	@Override
	public void add(Authorities entity) {
		daoAuth.save(entity);
	}

	@Override
	public void update(Authorities entity) {
		daoAuth.save(entity);
	}

	@Override
	public void delete(Integer id) {
		daoAuth.deleteById(id);
	}

	@Override
	public List<String> getUsername() {
		return daoAuth.getListUsername();
	}

	@Override
	public List<Authorities> getAuthoritiesByUsername(String username) {
		return daoAuth.getAuthoritiesByUsername(username);
	}

}
