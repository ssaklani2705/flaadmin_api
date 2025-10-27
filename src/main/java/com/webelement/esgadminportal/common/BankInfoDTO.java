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
	private Integer typeId;

	public BankInfoDTO(Integer bankId, String name, Short status, String bankType) {
		super();
		this.bankId = bankId;
		this.name = name;
		this.status = status;
		this.bankType = bankType;
	}

	public BankInfoDTO(Integer bankId, String name, Short status, String bankType, Integer typeId) {
		super();
		this.bankId = bankId;
		this.name = name;
		this.status = status;
		this.bankType = bankType;
		this.typeId = typeId;
	}

	public BankInfoDTO(Integer bankId, String name) {
		super();
		this.bankId = bankId;
		this.name = name;
	}

	@Transient
	private List<TransactionEntity> transactionhistory;

}
