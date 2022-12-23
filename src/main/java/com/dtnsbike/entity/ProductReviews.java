package com.dtnsbike.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Productreviews")
public class ProductReviews implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "[productrevewid]")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "[username]")
    private Accounts username;
    
    @ManyToOne
    @JoinColumn(name = "[productid]")
    private Products productid;
    
    @Column(name = "[comment]")
    private String comment;
    
    @Column(name="[date]")
    @Temporal(TemporalType.DATE)
    private Date date = new Date();
    
    @Column(name = "[stars]")
    private Integer stars;
        
    @JsonIgnore
    @OneToMany(mappedBy = "productreview")
    List<AuthenticPhotos> authenticphoto;
}
