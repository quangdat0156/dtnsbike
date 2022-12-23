package com.dtnsbike.dao.interfaces;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dtnsbike.entity.Authorities;
import com.dtnsbike.entity.Blogs;

public interface BlogsDAO extends JpaRepository<Blogs, Integer>{
    
    @Query("SELECT o FROM Blogs o WHERE o.status = true ORDER BY o.views DESC")
    List<Blogs> findAllTop(Pageable pageable);
    
    @Query("SELECT o FROM Blogs o WHERE o.status = true ORDER BY o.createdate DESC")
    List<Blogs> findTop(Pageable pageable);
    
    @Query("select count(DISTINCT blogid) from Blogs")
    Integer countBlog();
    
    @Query("SELECT o FROM Authorities o WHERE o.username.username = ?1 AND o.roleId.id = 'admin'")
    List<Authorities> check(String username);
    
}
