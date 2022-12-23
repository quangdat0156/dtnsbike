package com.dtnsbike.dao.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dtnsbike.entity.Authorities;

public interface AuthoritiesDAO extends JpaRepository<Authorities,Integer >{
    
    @Query(value = "select username from Authorities group by username", nativeQuery = true)
    List<String> getListUsername();
    
    @Query(value = "SELECT o FROM Authorities o WHERE o.username.username=:username")
    List<Authorities> getAuthoritiesByUsername(String username);
}

