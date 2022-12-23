package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.Orders;

@Service
public interface OrdersService {

	List<Orders> findAll();

	List<Orders> findWait();

	List<Orders> findWait1();
	
	List<Orders> findShipp();

	List<Orders> findDone();

	List<Orders> findCancel();
	
	List<Orders> allReorder();

	Orders findById(Integer id);

	void add(Orders entity);

	void update(Orders entity);

	void delete(Integer id);

}
