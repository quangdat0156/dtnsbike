package com.dtnsbike.dao.interfaces;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.dtnsbike.entity.OrderDetails;
import com.dtnsbike.entity.ReportPro;

public interface OrderDetailsDAO extends JpaRepository<OrderDetails, Integer> {

	@Query(value = "select * from OrderDetails a where a.OrderId = ?1", nativeQuery = true)
	List<OrderDetails> find_by_orderid(Integer orderid);

	// sản phẩm bán chạy
	@Query(value = "select new ReportPro(c.productsId.productid.id,c.productsId.productid.name+' (KT: '+c.productsId.sizeid.id+', MS: '+c.productsId.colorid.name+') ',"
			+ "((c.price+c.price*(c.productsId.productid.vat/100)-c.price*(c.productsId.productid.discountid.value/100))*c.amount)-((c.price+c.price*(c.productsId.productid.vat/100)-c.price*(c.productsId.productid.discountid.value/100))*c.amount)*(c.ordersId.salevalue/100),c.productsId.productid.catePro.name,count(*)) from OrderDetails c  "
			+"where c.ordersId.statusId.id = 'dagi' or c.ordersId.statusId.id='dnh'"
			+ "group by c.productsId.productid.id,c.productsId.sizeid.id,c.amount,c.productsId.colorid.name, c.productsId.productid.name,c.productsId.productid.vat, c.price,c.productsId.productid.discountid.value, c.productsId.productid.catePro.name,c.ordersId.salevalue order by count(c) DESC")
	List<ReportPro> findTop3Pro();

	// Thống kê theo khách hàng.
	@Query(value = "select a.username,a.fullName,a.[address],a.phone\r\n"
			+ ",sum((b.price+b.price*(b.vat/100)-b.price*(b.discount/100))*\r\n"
			+ "b.amount - (b.price+b.price*b.vat-b.price*(b.discount/100))*b.amount*(a.saleValue/100)) as N'Thành tiền',\r\n"
			+ "count(a.orderId) as 'Tổng đơn hàng'\r\n" + "from Orders a\r\n"
			+ "inner join OrderDetails b on a.orderId = b.orderId\r\n"
			+ "where a.statusId = 'dagi' or a.statusId = 'dnh' \r\n"
			+ "group by a.username,a.fullName,a.[address],a.phone", nativeQuery = true)
	List<Object[]> report1();

	// Thống kê theo sản phẩm
	@Query(value = "select b.productDetailId,(e.[name]+N' ( Kích thước: '+b.size+N', Màu sắc: '+f.[name]+')') as 'Thông tin sản phẩm',sum(b.amount) as 'Số lượng'\r\n"
			+ ",sum((b.price+b.price*(b.vat/100)-b.price*(b.discount/100))*b.amount-(b.price+b.price*(b.vat/100)-b.price*(b.discount/100))*b.amount*(a.saleValue/100)) as N'Thành tiền' \r\n"
			+ "from Orders a\r\n" + "inner join OrderDetails b on a.orderId = b.orderId\r\n"
			+ "inner join ProductDetails c on b.productDetailId = c.productDetailId\r\n"
			+ "inner join Colors f on f.colorId = c.colorId\r\n"
			+ "inner join Products e on e.productId = c.productId\r\n"
			+ "where a.statusId = 'dagi' or a.statusId = 'dnh'\r\n"
			+ "group by b.productDetailId,e.[name],b.size,f.[name]", nativeQuery = true)
	List<Object[]> report2();

	// Thống kê theo trạng thái đơn hàng
	@Query(value = "select a.statusId,b.[name],sum(c.price * c.amount) as N'Thành tiền1',sum((c.price+c.price*(c.vat/100)-c.price*(c.discount/100))*\r\n"
			+ "c.amount - (c.price+c.price*(c.vat/100)-c.price*(c.discount/100))*c.amount*(a.saleValue/100)) as N'Thành tiền' from Orders a\r\n"
			+ "inner join [Status] b on a.statusId = b.statusId\r\n"
			+ "inner join OrderDetails c on c.orderId = a.orderId\r\n" + "where a.statusId != 'dh'"
			+ "group by a.statusId,b.[name]", nativeQuery = true)
	List<Object[]> report3();

	@Query("SELECT sum((o.price+(o.price*o.vat/100)-o.price*(o.discount/100))*o.amount-(o.price+(o.price*o.vat/100)-o.price*(o.discount/100))*o.amount*(o.ordersId.salevalue/100)) FROM OrderDetails o WHERE o.ordersId.id = ?1")
	Double getSum(Integer id);

	@Query("SELECT sum((o.price+(o.price*o.vat/100)-o.price*(o.discount/100))*o.amount-(o.price+(o.price*o.vat/100)-o.price*(o.discount/100))*o.amount*(o.ordersId.salevalue/100)) FROM OrderDetails o WHERE o.ordersId.statusId.id = 'dagi'")
	Double getSum2();

	@Query("SELECT a FROM OrderDetails a WHERE a.ordersId.id = ?1")
	List<OrderDetails> find_by_orderid2(Integer orderid);

	@Query(value = "select * from orderdetails where productdetailid = ?1", nativeQuery = true)
	List<OrderDetails> findByProductDetails(Integer proDetaiId);
}
