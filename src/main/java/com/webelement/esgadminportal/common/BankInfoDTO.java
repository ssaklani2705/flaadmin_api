package com.webelement.esgadminportal.common;

import java.util.List;

import javax.persistence.Transient;

import com.webelement.esgadminportal.entity.TransactionEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class BankInfoDTO {
	private Integer bankId;
	private String name;
	private Short status;
	private String bankType;
	private Integer associateId;
	private String associationName;

	public BankInfoDTO(Integer bankId, String name, Short status, String bankType) {
		super();
		this.bankId = bankId;
		this.name = name;
		this.status = status;
		this.bankType = bankType;
	}

	public BankInfoDTO(Integer bankId, String name, Short status, String bankType, Integer associateId) {
		super();
		this.bankId = bankId;
		this.name = name;
		this.status = status;
		this.bankType = bankType;
		this.associateId = associateId;
	}

	public BankInfoDTO(Integer bankId, String name) {
		super();
		this.bankId = bankId;
		this.name = name;
	}

	public BankInfoDTO(Integer bankId, String name, Short status,
            Integer associateId, String associationName) {
this.bankId = bankId;
this.name = name;
this.status = status;
this.associateId = associateId;
this.associationName = associationName;
}
	@Transient
	private List<TransactionEntity> transactionhistory;

}
