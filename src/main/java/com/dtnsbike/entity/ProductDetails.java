package com.dtnsbike.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Productdetails", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "[productid]", "[colorid]", "[sizeid]" }) })
public class ProductDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "[productdetailid]")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "[productid]")
    private Products productid;

    @ManyToOne
    @JoinColumn(name = "[sizeid]")
    private Sizes sizeid;

    @ManyToOne
    @JoinColumn(name = "[colorid]")
    private Colors colorid;

    @Column(name = "[amount]")
    private Integer amount;

    @JsonIgnore
    @OneToMany(mappedBy = "productsId")
    List<OrderDetails> orderDetails;

}