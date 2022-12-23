package com.dtnsbike.dao.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.dtnsbike.entity.Warranties;
public interface WarrantiesDAO extends JpaRepository<Warranties, Integer>{

	@Query("SELECT o FROM Warranties o WHERE o.orderdetailid.id = ?1")
	Warranties findByOrid(Integer id);
	
	
	@Query("SELECT o FROM Warranties o WHERE o.framenumber = ?1")
	Warranties findByFramenumber(String id);
}
