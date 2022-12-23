package com.dtnsbike.dao.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dtnsbike.entity.DetailPhotos;

public interface DetailPhotosDAO extends JpaRepository<DetailPhotos, String>{
	
	@Query(value = "select photoName from DetailPhotos where productId = ?1",nativeQuery = true)
	List<String> findByProId(Integer proId);
	
	@Query(value = "select * from DetailPhotos where productid = ?1 and photoName = ?2",nativeQuery = true)
	DetailPhotos findByProIdAndFileName(String proid, String filename);
}
