package com.dtnsbike.dao.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Authorities;

public interface AccountsDAO extends JpaRepository<Accounts, String> {

	@Query(value = "SELECT o FROM Accounts o WHERE o.username=:username and o.active=:active")
	Optional<Accounts> findByIdAndActiveTrue(String username, Boolean active);

	@Query(value = "select * from Accounts where email like ?1", nativeQuery = true)
	String findEmail(String email);

	@Query(value = "select * from Accounts where Phone like ?1", nativeQuery = true)
	String findPhone(String phone);


    List<Accounts> findByEmail(String email);

    
    @Query("select count(DISTINCT username) from Accounts ")
    Integer countAcc();


    @Query(value = "select * from Accounts where phone like ?1", nativeQuery = true)
    Accounts findByPhones(String phone);


	@Query(value = "select * from Accounts where email like ?1", nativeQuery = true)
	Accounts findByEmails(String email);



	@Query(value="SELECT TOP(3) * FROM Accounts ORDER BY username DESC ",nativeQuery = true)
	List<Accounts> getListTop();
	
	@Query("SELECT o.username FROM Authorities o WHERE o.username.username = ?1 AND o.roleId.id='admin' AND o.active = true")
	Accounts check(String username);
	
	@Query("SELECT o FROM Authorities o WHERE o.username.username = ?1 AND o.roleId.id='admin' AND o.active = true")
	List<Authorities> check2(String username);
	
	@Query("SELECT o FROM Authorities o WHERE o.username.username = ?1 AND o.roleId.id='staff' AND o.active = true")
	List<Authorities> check3(String username);
}
