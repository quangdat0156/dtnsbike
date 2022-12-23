package com.dtnsbike.dao.interfaces;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.dtnsbike.entity.Types;

public interface TypesDAO extends JpaRepository<Types, Integer> {

	@Query("SELECT o FROM Types o WHERE o.id IN (SELECT b.catePro.typeId.id FROM Products b )")
	List<Types> findAllInProduct();

	@Query("SELECT count(o) FROM Products o WHERE o.catePro.typeId = ?1 and o.avaliable = true")
	Integer getCountPro(Types id);

	@Query("SELECT o.typeId FROM Categories o WHERE o.typeId.id = ?1")
	Types checkInCate(Integer id);

}
