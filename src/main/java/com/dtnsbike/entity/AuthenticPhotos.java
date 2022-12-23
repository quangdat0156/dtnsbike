package com.dtnsbike.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Authenticphotos", uniqueConstraints = { @UniqueConstraint(columnNames = { "[photoname]" }),
        @UniqueConstraint(columnNames = { "[productreviewid]" }) })
public class AuthenticPhotos implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "[authenticphotoid]")
    String authenticphotoid;

    @Column(name = "[photoname]")
    String photoname;

    @ManyToOne
    @JoinColumn(name = "[productreviewid]")
    ProductReviews productreview;

}
