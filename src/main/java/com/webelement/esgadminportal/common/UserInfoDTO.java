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
public class UserInfoDTO {

	private Integer bankUserId;
	private String name;
	private String contactNo;
	private String email;
	private Short status;
	private String bankName;
	private String bankType;
	private Integer bankId;

	public UserInfoDTO(Integer bankUserId, String name, String contactNo, String email, Short status, String bankName) {
		super();
		this.bankUserId = bankUserId;
		this.name = name;
		this.contactNo = contactNo;
		this.email = email;
		this.status = status;
		this.bankName = bankName;
	}

	public UserInfoDTO(Integer bankUserId, String name, String contactNo, String email, Short status, String bankName,
			String bankType) {
		super();
		this.bankUserId = bankUserId;
		this.name = name;
		this.contactNo = contactNo;
		this.email = email;
		this.status = status;
		this.bankName = bankName;
		this.bankType = bankType;
	}

	public UserInfoDTO(Integer bankUserId, String name, String contactNo, String email, Short status, String bankName,
			String bankType, Integer bankId) {
		super();
		this.bankUserId = bankUserId;
		this.name = name;
		this.contactNo = contactNo;
		this.email = email;
		this.status = status;
		this.bankName = bankName;
		this.bankType = bankType;
		this.bankId = bankId;
	}

	@Transient
	private List<TransactionEntity> transactionhistory;

}
