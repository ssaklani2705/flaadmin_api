package com.webelement.esgadminportal.entity;

import java.sql.Timestamp;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "i_typeid")
    private Integer typeId;

    @Column(name = "s_name", length = 200)
    private String name;

    @Column(name = "i_status")
    private Short status;

    @Column(name = "ts_regdate")
    private Timestamp regDate;
}
