package com.webelement.esgadminportal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_group")
public class GroupEntity {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "i_groupid")
	    private Integer groupId;

	    @Column(name = "s_name", length = 200)
	    private String name;

	    @Column(name = "d_date")
	    private LocalDate date;

	    @Column(name = "i_status")
	    private Short status;

	    @Column(name = "i_userid")
	    private Integer userId;

	    @Column(name = "ts_regdate")
	    private LocalDateTime regDate;

	    @Column(name = "ts_moddate")
	    private LocalDateTime modDate;
	    
	    
	    
	 // Getters and Setters

	    public Integer getGroupId() {
	        return groupId;
	    }

	    public void setGroupId(Integer groupId) {
	        this.groupId = groupId;
	    }

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public LocalDate getDate() {
	        return date;
	    }

	    public void setDate(LocalDate date) {
	        this.date = date;
	    }

	    public Short getStatus() {
	        return status;
	    }

	    public void setStatus(Short status) {
	        this.status = status;
	    }

	    public Integer getUserId() {
	        return userId;
	    }

	    public void setUserId(Integer userId) {
	        this.userId = userId;
	    }

	    public LocalDateTime getRegDate() {
	        return regDate;
	    }

	    public void setRegDate(LocalDateTime regDate) {
	        this.regDate = regDate;
	    }

	    public LocalDateTime getModDate() {
	        return modDate;
	    }

	    public void setModDate(LocalDateTime modDate) {
	        this.modDate = modDate;
	    }

}
