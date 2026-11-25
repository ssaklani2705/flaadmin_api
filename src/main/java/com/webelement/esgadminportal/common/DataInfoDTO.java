package com.webelement.esgadminportal.common;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataInfoDTO {

	private Integer dataId;
	private String entityCode;
	private String state;
	private String branchCategory;
	private String perpetratorStatus;
	private String accountType;
	private String industrySector;
	private String areaOfOperation;
	private String fraudType;
	@JsonSerialize(using = IndianAmountSerializer.class)
	private BigDecimal totalAmount;
	private LocalDate dateOfOccurrence;
	private LocalDate dateOfDetection;
	private LocalDate dateOfClassification;
	private LocalDate dateReportedToRbi;
	private String howDetected;
	private Integer status;
	
	private String bankName;

	public DataInfoDTO(Integer dataId, String entityCode, String state, String branchCategory, String perpetratorStatus,
			String accountType, String industrySector, String areaOfOperation, String fraudType, BigDecimal totalAmount,
			LocalDate dateOfOccurrence, LocalDate dateOfDetection, LocalDate dateOfClassification,
			LocalDate dateReportedToRbi, String howDetected, Integer status) {
		this.dataId = dataId;
		this.entityCode = entityCode;
		this.state = state;
		this.branchCategory = branchCategory;
		this.perpetratorStatus = perpetratorStatus;
		this.accountType = accountType;
		this.industrySector = industrySector;
		this.areaOfOperation = areaOfOperation;
		this.fraudType = fraudType;
		this.totalAmount = totalAmount;
		this.dateOfOccurrence = dateOfOccurrence;
		this.dateOfDetection = dateOfDetection;
		this.dateOfClassification = dateOfClassification;
		this.dateReportedToRbi = dateReportedToRbi;
		this.howDetected = howDetected;
		this.status = status;
	}
	
	
}