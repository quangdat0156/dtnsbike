package com.dtnsbike.dao.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dtnsbike.entity.Contacts;


public interface ContactsDAO extends JpaRepository<Contacts, Integer>{
	@Query("SELECT o FROM Contacts o where o.userAr.username = ?1")
	List<Contacts> findAllByUserAr(String userAr);
	@Query("SELECT o FROM Contacts o where o.id=?1 and o.userAr.username = ?2")
	Optional<Contacts> findContactIdByUserAr(Integer id,String userAr);
}
