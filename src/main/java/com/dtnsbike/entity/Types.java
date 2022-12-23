package com.dtnsbike.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "Types")
public class Types implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "[typeid]")
    private Integer id;

    @Column(name = "[name]")
    @NotBlank(message = "Tên danh mục không được bỏ trống !")
    private String name;
    
    @Column(name = "[description]")
    private String description;
    
    @JsonIgnore
    @OneToMany(mappedBy = "typeId")
    List<Categories> categorie;
}
