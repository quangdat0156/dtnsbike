package com.dtnsbike.dao.interfaces;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dtnsbike.entity.OrderDetails;
import com.dtnsbike.entity.ProductDetails;
import com.dtnsbike.model.ColorDetailModel;
import com.dtnsbike.model.SizeDetailModel;

public interface ProductDetailsDAO extends JpaRepository<ProductDetails, Integer> {

	@Query(value = "Select o from ProductDetails o where o.productid.id=?1 and o.colorid.id=?2 and o.sizeid.id=?3")
	ProductDetails getProDltByProIdAndClrIdAndSzeId(Integer ProductId, String colorId, String sizeId);
	
    @Query(value = "select a.orderDetails from ProductDetails a where a.productid.name like ?1 ")
	List<OrderDetails> findByKey(String key);

	
	@Query(value = "Select new ColorDetailModel(o.colorid.id,o.colorid.name,count(o.colorid.id)) from ProductDetails o where o.productid.id=?1 and o.amount>0  Group By o.colorid.id,o.colorid.name")
	Optional<List<ColorDetailModel>> getColorPD(Integer ProductId);

	@Query(value = "Select new SizeDetailModel(o.sizeid.id,o.sizeid.name,count(o.sizeid.id)) from ProductDetails o where o.productid.id=?1 and o.amount>0 Group By o.sizeid.id,o.sizeid.name")
	Optional<List<SizeDetailModel>> getSizePD(Integer ProductId);

	@Query(value = "Select o from ProductDetails o where o.productid.id=?1 and o.productid.avaliable = true")
	Optional<List<ProductDetails>> getListProDetails(Integer ProductId);

	// số lượng sản phẩm sắp hết hàng
	@Query("select count(*) from ProductDetails where amount <= 20")
	Integer countProduct_Saphethang();

	// sản phẩm đã hết hàng
	@Query(value = "select * from ProductDetails where amount = 0", nativeQuery = true)
	List<ProductDetails> Prod_hethang();

	@Query(value = "select * from ProductDetails where productid = ?1", nativeQuery = true)
	List<ProductDetails> getProdetailsByProID(Integer id);

	@Query(value = "select colorId from productdetails where productId = ?1 and colorId = ?2 group by colorid", nativeQuery = true)
	Optional<String> getProDtlByProIdAddClrId(Integer ProductId, String colorId);
	
	@Query(value = "Select o from ProductDetails o where o.productid.id=?1 and o.colorid.id=?2 and o.sizeid.id=?3")
	Optional<ProductDetails> getProDtlByProIdAddClrIdAndSze(Integer ProductId, String colorId, String sizeId);

}
