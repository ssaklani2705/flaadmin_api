package com.webelement.esgadminportal.common;

import java.util.List;

import com.webelement.esgadminportal.entity.TransactionEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommitteeDTO {
    private Integer committeeId;
    private Integer bankId;
    private String name;
    private String designation;
    private String email;
    private Integer sequence;
    private Short status;
    private String bankName;
    

    // optional: transaction logs
    private List<TransactionEntity> transactionHistory;

    // constructor used in JPQL query (no transactionHistory)
	public CommitteeDTO(Integer committeeId, Integer bankId, String name, String designation, String email,
			Integer sequence, Short status, String bankName) {
		super();
		this.committeeId = committeeId;
		this.bankId = bankId;
		this.name = name;
		this.designation = designation;
		this.email = email;
		this.sequence = sequence;
		this.status = status;
		this.bankName = bankName;
	}

}
