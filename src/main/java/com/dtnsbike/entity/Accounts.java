package com.dtnsbike.entity;

import java.io.Serializable;
import java.util.Date;
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
@Table(name = "Accounts", uniqueConstraints = { @UniqueConstraint(columnNames = { "[email]" }),
        @UniqueConstraint(columnNames = { "[phone]" }) })
public class Accounts implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "[username]")
    private String username;

    @Column(name = "[password]")
    private String password;

    @Column(name = "[firstname]")
    private String firstname;
    
    @Column(name = "[middlename]")
    private String middlename;

    @Column(name = "[lastname]")
    private String lastname;

    @Column(name = "[email]")
    private String email;

    @Column(name = "[avatar]")
    private String photo;

    @Column(name = "[phone]")
    private String phone;

    @Column(name = "[gender]")
    private String gender;

    @Column(name = "[birthday]")
    @Temporal(TemporalType.DATE)
    private Date birthday;

    @Column(name = "[active]")
    private Boolean active = true;

    @JsonIgnore
    @OneToMany(mappedBy = "username", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Authorities> authorities;

    @JsonIgnore
    @OneToMany(mappedBy = "userCart")
    private List<Carts> cart;

    @JsonIgnore
    @OneToMany(mappedBy = "userFvr")
    private List<Favorites> favorites;

    @JsonIgnore
    @OneToMany(mappedBy = "userAr")
    private List<Contacts> address;

    @JsonIgnore
    @OneToMany(mappedBy = "userOrder")
    private List<Orders> order;

    @JsonIgnore
    @OneToMany(mappedBy = "userWar")
    private List<Warranties> warranties;

    @JsonIgnore
    @OneToMany(mappedBy = "accounts")
    private List<Blogs> blogs;
    
    @JsonIgnore
    @OneToMany(mappedBy = "userPro_by_His")
    private List<ProductPriceHistories> probyhis;

}