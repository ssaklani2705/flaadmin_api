
package com.webelement.esgadminportal.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_data")
public class DataEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@NotBlank(message = "Code is required")
	@Column(name = "s_code")
	private String code;

	@NotBlank(message = "State is required")
	@Column(name = "s_state")
	private String state;

	@NotBlank(message = "Category of branch is required")
	@Column(name = "s_cat_of_branch")
	private String catOfBranch;

	@Column(name = "s_status_of_perpetrator")
	private String statusOfPerpetrator;

	@NotBlank(message = "Type of account is required")
	@Column(name = "s_type_of_account")
	private String typeOfAccount;

	@Column(name = "s_type_of_acc_other")
	private String typeOfAccOther;

	@NotBlank(message = "Section of account is required")
	@Column(name = "s_sec_of_acc")
	private String secOfAcc;

	@NotBlank(message = "Borrowal account is required")
	@Column(name = "s_borrowal_acc")
	private String borrowalAcc;

	@NotBlank(message = "Banking arrangement is required")
	@Column(name = "s_banking_arrangement")
	private String bankingArrangement;

	@NotBlank(message = "Area of operation is required")
	@Column(name = "s_area_of_operation")
	private String areaOfOperation;

	@Column(name = "s_area_other")
	private String areaOther;

	@NotBlank(message = "Classification of fraud is required")
	@Column(name = "s_classification_of_fraud")
	private String classificationOfFraud;

	@Column(name = "s_classification_other")
	private String classificationOther;

	@NotBlank(message = "Type of fraud is required")
	@Column(name = "s_type_of_fraud")
	private String typeOfFraud;

	@Column(name = "s_fraud_type_other")
	private String fraudTypeOther;

	@NotNull(message = "Total amount is required")
	@Column(name = "d_total_amount", precision = 18, scale = 2)
	private BigDecimal totalAmount;

	@NotNull(message = "Occurrence date is required")
	@Column(name = "d_occurrence")
	private LocalDate occurrence;

	@NotNull(message = "Detection date is required")
	@Column(name = "d_detection")
	private LocalDate detection;

	@NotNull(message = "Classification date is required")
	@Column(name = "d_classification")
	private LocalDate classification;

	@NotNull(message = "Reported to RBI date is required")
	@Column(name = "d_reported_to_rbi")
	private LocalDate reportedToRbi;

	@NotBlank(message = "Delay reason is required")
	@Column(name = "s_delay_reason", columnDefinition = "TEXT")
	private String delayReason;

	@Column(name = "d_full_payment_settlement")
	private LocalDate fullPaymentSettlement;

	@NotBlank(message = "Detection method is required")
	@Column(name = "s_detection_method")
	private String detectionMethod;

	@Column(name = "s_detection_other_source")
	private String detectionOtherSource;

	@NotBlank(message = "Fraud committed by is required")
	@Column(name = "s_fraud_committed_by")
	private String fraudCommittedBy;

	@NotBlank(message = "Controlling office detection is required")
	@Column(name = "s_controlling_office_detect")
	private String controllingOfficeDetect;

	@NotBlank(message = "Internal audit conducted is required")
	@Column(name = "s_internal_audit_conducted")
	private String internalAuditConducted;

	@NotBlank(message = "Audit detection reason is required")
	@Column(name = "s_audit_detection_reason", columnDefinition = "TEXT")
	private String auditDetectionReason;

	@NotBlank(message = "Audit action taken is required")
	@Column(name = "s_audit_action_taken", columnDefinition = "TEXT")
	private String auditActionTaken;

	@Column(name = "s_lea_complaint")
	private String leaComplaint;

	@NotBlank(message = "Preventive steps are required")
	@Column(name = "s_preventive_steps", columnDefinition = "TEXT")
	private String preventiveSteps;

	@Column(name = "d_amount_recovered", precision = 18, scale = 2)
	private BigDecimal amountRecovered;

	@NotNull(message = "Loss to bank is required")
	@Column(name = "d_loss_to_bank", precision = 18, scale = 2)
	private BigDecimal lossToBank;

	@NotNull(message = "Provision held is required")
	@Column(name = "d_provision_held", precision = 18, scale = 2)
	private BigDecimal provisionHeld;

	@NotNull(message = "Amount written off is required")
	@Column(name = "d_amount_written_off", precision = 18, scale = 2)
	private BigDecimal amountWrittenOff;

	@NotBlank(message = "Compromise settlement is required")
	@Column(name = "s_compromise_settlement")
	private String compromiseSettlement;

	@NotNull(message = "Compromise amount is required")
	@Column(name = "d_compromise_amount", precision = 18, scale = 2)
	private BigDecimal compromiseAmount;

	@Column(name = "i_staus")
	@Builder.Default
	private Integer status = 1;

	@Column(name = "i_bankid")
	private Integer bankId;

	@Column(name = "ts_regdate", insertable = false, updatable = false)
	private LocalDateTime regDate;

	@Column(name = "ts_moddate", insertable = false, updatable = false)
	private LocalDateTime modDate;
}
