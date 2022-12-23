package com.dtnsbike.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Warranties")
public class Warranties implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "[warrantyid]")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "[username]")
    private Accounts userWar;

    @ManyToOne
    @JoinColumn(name = "[orderdetailid]")
    private OrderDetails orderdetailid;

    @Column(name = "[framenumber]")
    private String framenumber;

}
