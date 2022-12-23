package com.dtnsbike.dao.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Colors;

@Service
public interface ColorsService {

	List<Colors> findAll();

	List<Colors> findInProduct();

	Colors check(String id);

	Optional<Colors> findById(String id);

	void add(Colors entity);

	void update(Colors entity);

	void delete(String id);

}
