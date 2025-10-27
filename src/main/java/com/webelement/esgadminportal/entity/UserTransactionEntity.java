package com.webelement.esgadminportal.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_user_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "i_transactionid")
    @JsonIgnore
    private Integer transactionId;

    @Column(name = "i_moduleid")
    @JsonIgnore
    private Integer moduleId;

    @Column(name = "i_recordid")
    @JsonIgnore
    private Integer recordId;

    @Column(name = "i_userid")
    @JsonIgnore
    private Integer userId;

    @Column(name = "s_ipaddress")
    @JsonIgnore
    private String ipAddress;

    @Column(name = "s_localip")
    @JsonIgnore
    private String localIp;

    @Column(name = "s_action")
    private String action;

    @Column(name = "ts_regdate")
    @JsonIgnore
    private Timestamp regDate;

    @Column(name = "s_flag")
    @JsonIgnore
    private String flag;

    @Column(name = "i_frontuserid")
    @JsonIgnore
    private Integer frontUserId;
    
    @Transient
    private String entryDate;
    
    @Transient
    private String name;
}