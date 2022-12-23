package com.dtnsbike.dao.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dtnsbike.entity.ProductReviews;

public interface ProductReviewsDAO extends JpaRepository<ProductReviews, Integer> {
	@Query("Select o from ProductReviews o where o.productid.id=?1")
	List<ProductReviews> findAllByPro(Integer proID);
	
	@Query("Select o from ProductReviews o where o.username.username=?1")
	List<ProductReviews> findAllByUser(String username);
}
