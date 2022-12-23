package com.dtnsbike.dao.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dtnsbike.entity.Status;


public interface StatusDAO extends JpaRepository<Status, String> {


}
