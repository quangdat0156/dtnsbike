package com.dtnsbike.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Blogs")
public class Blogs implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "[blogid]")
	@NotNull(message = "Mã bài viết không được bỏ trống !")
	@Range(min = 1,max = 9999,message = "Mã bài viết không hợp lệ!")
	Integer blogid;

	@ManyToOne
	@JoinColumn(name = "[username]")
	private Accounts accounts;

	@Column(name = "[title]")
	@NotBlank(message = "Tiêu đề không được bỏ trống !")
	String title;

	@Column(name = "[description]")
	@NotBlank(message = "Mô tả không được bỏ trống !")
	String description;

	@Column(name = "[contents]")
	@NotBlank(message = "Nội dung không được bỏ trống !")
	String content;

	@Column(name = "[images]")
	String images;

	@Column(name = "[createdate]")
	@Temporal(TemporalType.DATE)
	Date createdate = new Date();

	@Column(name = "[views]")
	Integer views = 0;

	@Column(name = "[status]")
	@NotNull(message = "Vui lòng chọn chế độ hiển thị !")
	Boolean status;
}
