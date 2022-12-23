package com.dtnsbike.dao.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dtnsbike.entity.Accounts;
import com.dtnsbike.entity.Orders;

public interface OrdersDAO extends JpaRepository<Orders, Integer> {
	@Query(value = "select * from Orders a where a.username=?1 order by a.orderid DESC", nativeQuery = true)
	List<Orders> findByUserOrder(Accounts userOrder);
	
	@Query(value = "select * from Orders a where a.statusid='dnh' and a.username=?1 order by a.orderid DESC", nativeQuery = true)
	List<Orders> find_by_danhan(String username);
	
	@Query(value = "select * from Orders a where a.statusid='cxn' and a.username=?1 order by a.orderid DESC", nativeQuery = true)
	List<Orders> find_by_choxacnhan(String username);

	@Query(value = "select * from Orders a where a.statusid='clh' and a.username=?1 order by a.orderid DESC", nativeQuery = true)
	List<Orders> find_by_cholayhang(String username);

	@Query(value = "select * from Orders a where a.statusid='dagi' and a.username=?1 order by a.orderid DESC", nativeQuery = true)
	List<Orders> find_by_Dagiao(String username);

	@Query(value = "select * from Orders a where a.statusid='dangi' and a.username=?1 order by a.orderid DESC", nativeQuery = true)
	List<Orders> find_by_danggiao(String username);

	@Query(value = "select * from Orders a where a.statusid='dh' and a.username=?1 order by a.orderid DESC", nativeQuery = true)
	List<Orders> find_by_huy(String username);

	@Query(value = "select * from Orders where statusid='dh'", nativeQuery = true)
	List<Orders> Order_All_huy();

	@Query("select count(DISTINCT orderid) from Orders")
	Integer countOrder();

	@Query("select count(*) from Orders where statusid='dh'")
	Integer countOrder_byHuy();

	@Query(value = "select top(3) * from Orders where statusId = 'cxn' order by purchaseDate DESC", nativeQuery = true)
	List<Orders> Order_top3_cxn();

	// All CXN
	@Query("SELECT o FROM Orders o WHERE o.statusId.id = 'cxn' ORDER BY o.id DESC")
	List<Orders> allOrderWait();

	// All CLH
	@Query("SELECT o FROM Orders o WHERE o.statusId.id = 'clh' ORDER BY o.id DESC")
	List<Orders> allOrderWait1();

	// All DANGI
	@Query("SELECT o FROM Orders o WHERE o.statusId.id = 'dangi' ORDER BY o.id DESC")
	List<Orders> allOrderShipp();

	// All DAGI
	@Query("SELECT o FROM Orders o WHERE o.statusId.id = 'dagi' ORDER BY o.id DESC")
	List<Orders> allOrderDone();

	// All DH
	@Query("SELECT o FROM Orders o WHERE o.statusId.id = 'dh' ORDER BY o.id DESC")
	List<Orders> allOrderCancel();

	// All DN
	@Query("SELECT o FROM Orders o WHERE o.statusId.id = 'dnh' ORDER BY o.id DESC")
	List<Orders> allReCancel();

	@Query(value = "select * from Orders where month(purchaseDate) = ?1 and statusId = 'dagi'", nativeQuery = true)
	List<Orders> report1(Integer month);

	@Query(value = "select * from Orders where month(purchaseDate) = ?1 and statusId != 'dagi'", nativeQuery = true)
	List<Orders> report2(Integer month);

	@Query(value = "select sum(b.price*b.amount) from Orders a inner join OrderDetails b on a.orderid = b.orderid"
			+ " where month(purchaseDate) = ?1 and statusId = 'dagi'", nativeQuery = true)
	Integer report3(Integer month);

	@Query(value = "select sum(b.price*b.amount) from Orders a inner join OrderDetails b on a.orderid = b.orderid"
			+ " where month(purchaseDate) = ?1 and statusId = 'dh'", nativeQuery = true)
	Integer report4(Integer month);
	
	@Override
	@Query("SELECT o FROM Orders o ORDER BY o.id DESC")
	List<Orders> findAll();
}


