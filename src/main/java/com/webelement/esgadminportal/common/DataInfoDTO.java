package com.webelement.esgadminportal.common;

import java.math.BigDecimal;
import java.time.LocalDate;

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
	private BigDecimal totalAmount;
	private LocalDate dateOfOccurrence;
	private LocalDate dateOfDetection;
	private LocalDate dateOfClassification;
	private LocalDate dateReportedToRbi;
	private String howDetected;
	private Integer status;
	
	private String bankName;
}