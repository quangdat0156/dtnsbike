package com.dtnsbike.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Sales")
public class Sales implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "[saleid]")
	@NotBlank(message = "Mã giảm giá không được bỏ trống")
	private String id;
	
	@Column(name = "[value]")
	@NotNull(message = "Giá trị không được bỏ trống")
//	@PositiveOrZero(message = "Chỉ nhận giá trị số dương")
	private Integer value;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "Vui lòng chọn ngày bắt đầu")
	@Column(name = "[startdate]")
	private Date startdate;
	
	@Column(name = "[enddate]")
	@NotNull(message = "Vui lòng chọn ngày kết thúc")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date enddate;
	
	@Column(name = "[amount]")
	@NotNull(message = "Số lượng không được bỏ trống")
//	@PositiveOrZero(message = "Chỉ nhận giá trị số dương")
	private Integer amount;
	
	@Column(name = "[createdate]")
	private Date createDate=new Date();

	@NotNull(message = "Vui lòng xét trạng thái")
	@Column(name = "[active]")
    private Boolean active;
	
	@Column(name = "[description]")
    private String description;
	
	@JsonIgnore
	@OneToMany(mappedBy = "saleId")
	List<Orders> saleOrders;

}