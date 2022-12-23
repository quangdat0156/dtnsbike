package com.dtnsbike.dao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dtnsbike.entity.OrderDetails;

@Service
public interface OrderDetailsService {

	List<OrderDetails> findAll();

	List<OrderDetails> findOne(Integer id);
	
	List<OrderDetails> findByOrder(Integer id);

	Double getSum(Integer id);

	OrderDetails findById(Integer id);

	void add(OrderDetails entity);

	void update(OrderDetails entity);

	void delete(Integer id);

	List<OrderDetails> findByProductDetails(Integer proDetaiId);

}
