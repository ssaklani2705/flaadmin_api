package com.webelement.esgadminportal.common;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Transient;

import com.webelement.esgadminportal.entity.TransactionEntity;

public class GroupInfoDTO {

    private Integer groupId;
    private String name;
    private Short status;
    private LocalDate date;   // d_date
    
	@Transient
	private List<TransactionEntity> transactionhistory;

    public GroupInfoDTO(Integer groupId, String name, Short status, LocalDate date) {
        this.groupId = groupId;
        this.name = name;
        this.status = status;
        this.date = date;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public Short getStatus() {
        return status;
    }

    public LocalDate getDate() {
        return date;
    }

	public List<TransactionEntity> getTransactionhistory() {
		return transactionhistory;
	}

	public void setTransactionhistory(List<TransactionEntity> transactionhistory) {
		this.transactionhistory = transactionhistory;
	}
    
    
    
}
