package com.dtnsbike.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Sizes")
public class Sizes implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "[sizeid]")
    @NotBlank(message = "Mã kích thước không được bỏ trống !")
    private String id;
    
    @Column(name = "[description]")
    @NotBlank(message = "Tên kích thước không được bỏ trống !")
    private String name;
    
    @JsonIgnore
    @OneToMany(mappedBy = "sizeid")
    List<ProductDetails> productdetail;
}
