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
@Table(name = "Carts", uniqueConstraints = { @UniqueConstraint(columnNames = { "[username]", "[productdetailid]" }) })
public class Carts implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "[cartid]")
    private String id;

    @ManyToOne
    @JoinColumn(name = "[username]")
    private Accounts userCart;

    @ManyToOne
    @JoinColumn(name = "[productdetailid]")
    private ProductDetails proCart;

    @Column(name = "[amount]")
    private Integer qty;

}
