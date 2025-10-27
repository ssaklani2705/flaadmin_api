package com.webelement.esgadminportal.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_committee")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommitteeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "i_committeeid")
    private Integer committeeId;

    @Column(name = "i_bankid")
    private Integer bankId;

    @Column(name = "s_name", length = 100)
    private String name;

    @Column(name = "s_designation", length = 100)
    private String designation;

    @Column(name = "s_email", length = 100)
    private String email;

    @Column(name = "i_sequence")
    private Integer sequence;

    @Column(name = "i_status")
    private Short status;

    @Column(name = "i_userid")
    private Integer userId;

    @Column(name = "ts_regdate")
    private LocalDateTime regDate;

    @Column(name = "ts_moddate")
    private LocalDateTime modDate;
	
}
