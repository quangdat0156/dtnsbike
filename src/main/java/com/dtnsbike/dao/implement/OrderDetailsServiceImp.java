package com.dtnsbike.dao.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtnsbike.dao.interfaces.OrderDetailsDAO;
import com.dtnsbike.dao.service.OrderDetailsService;
import com.dtnsbike.entity.OrderDetails;

@Service
public class OrderDetailsServiceImp implements OrderDetailsService {

	@Autowired
	OrderDetailsDAO daoOD;

	@Override
	public List<OrderDetails> findAll() {
		return daoOD.findAll();
	}

	@Override
	public List<OrderDetails> findByOrder(Integer id) {
		return daoOD.find_by_orderid2(id);
	}
	
	@Override
	public Double getSum(Integer id) {
		return daoOD.getSum(id);
	}

	@Override
	public List<OrderDetails> findOne(Integer id) {
		return daoOD.find_by_orderid2(id);
	}

	@Override
	public OrderDetails findById(Integer id) {
		return daoOD.findById(id).get();
	}

	@Override
	public void add(OrderDetails entity) {
		daoOD.save(entity);
	}

	@Override
	public void update(OrderDetails entity) {
		daoOD.save(entity);
	}

	@Override
	public void delete(Integer id) {
		daoOD.deleteById(id);
	}

	@Override
	public List<OrderDetails> findByProductDetails(Integer proDetaiId) {
		return daoOD.findByProductDetails(proDetaiId);
	}

}
