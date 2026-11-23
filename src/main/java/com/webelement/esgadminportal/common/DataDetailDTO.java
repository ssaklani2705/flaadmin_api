package com.webelement.esgadminportal.common;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.webelement.esgadminportal.entity.DataEntity;

import lombok.Data;

@Data
public class DataDetailDTO {

	private Integer dataId;
	private String entityCode;
	private String state;
	private String branchCategory;
	private String perpetratorStatus;
	private String accountType;
	private String accountTypeOther;
	private String industrySector;
	private String borrowalAccount;
	private String bankingArrangement;
	private String areaOfOperation;
	private String areaOther;
	private String fraudClassification;
	private String classificationOther;
	private String fraudType;
	private String fraudTypeOther;
	private BigDecimal totalAmount;
	private LocalDate dateOfOccurrence;
	private LocalDate dateOfDetection;
	private LocalDate dateOfClassification;
	private LocalDate dateReportedToRbi;
	private String delayReason;
	private LocalDate dateOfFullPayment;
	private String howDetected;
	private String otherSources;
	private String committedBy;
	private String controlOfficeDetected;
	private String internalAuditConducted;
	private String whyNotDetected;
	private String actionTaken;
	private String leaComplaint;
	private String stepsTaken;
	private BigDecimal amountRecovered;
	private BigDecimal lossToBank;
	private BigDecimal provisionHeld;
	private BigDecimal amountWrittenOff;
	private String compromiseDone;
	private BigDecimal compromiseAmount;
	private Integer status;

	public static DataDetailDTO from(DataEntity e) {
		DataDetailDTO d = new DataDetailDTO();
		d.dataId = e.getId();
		d.entityCode = e.getCode();
		d.state = e.getState();
		d.branchCategory = e.getCatOfBranch();
		d.perpetratorStatus = e.getStatusOfPerpetrator();
		d.accountType = e.getTypeOfAccount();
		d.accountTypeOther = e.getTypeOfAccOther();
		d.industrySector = e.getSecOfAcc();
		d.borrowalAccount = e.getBorrowalAcc();
		d.bankingArrangement = e.getBankingArrangement();
		d.areaOfOperation = e.getAreaOfOperation();
		d.areaOther = e.getAreaOther();
		d.fraudClassification = e.getClassificationOfFraud();
		d.classificationOther = e.getClassificationOther();
		d.fraudType = e.getTypeOfFraud();
		d.fraudTypeOther = e.getFraudTypeOther();
		d.totalAmount = e.getTotalAmount();
		d.dateOfOccurrence = e.getOccurrence();
		d.dateOfDetection = e.getDetection();
		d.dateOfClassification = e.getClassification();
		d.dateReportedToRbi = e.getReportedToRbi();
		d.delayReason = e.getDelayReason();
		d.dateOfFullPayment = e.getFullPaymentSettlement();
		d.howDetected = e.getDetectionMethod();
		d.otherSources = e.getDetectionOtherSource();
		d.committedBy = e.getFraudCommittedBy();
		d.controlOfficeDetected = e.getControllingOfficeDetect();
		d.internalAuditConducted = e.getInternalAuditConducted();
		d.whyNotDetected = e.getAuditDetectionReason();
		d.actionTaken = e.getAuditActionTaken();
		d.leaComplaint = e.getLeaComplaint();
		d.stepsTaken = e.getPreventiveSteps();
		d.amountRecovered = e.getAmountRecovered();
		d.lossToBank = e.getLossToBank();
		d.provisionHeld = e.getProvisionHeld();
		d.amountWrittenOff = e.getAmountWrittenOff();
		d.compromiseDone = e.getCompromiseSettlement();
		d.compromiseAmount = e.getCompromiseAmount();
		d.status = e.getStatus();
		return d;
	}
}