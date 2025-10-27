package com.webelement.esgadminportal.common;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Transient;

import com.webelement.esgadminportal.entity.TransactionEntity;

public class KnowledgeDTO {

	private Integer knowledge;
	private Integer bankId;
	private String name;
	private String filename;
	private Integer bankApproval;
	private Integer approval;
	private Integer userId;
	private Integer bankUserId;
	private LocalDateTime regDate;
	private LocalDateTime modDate;
	private String username;
	private String bankName;
	private String source;
	private Short status;
	private Integer addedBy;
	private String addedByName;

	@Transient
	private List<TransactionEntity> transactionhistory;

	public KnowledgeDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public KnowledgeDTO(Integer knowledge, Integer bankId, String name, String filename, Integer bankApproval,
			Integer approval, Integer userId, Integer bankUserId, LocalDateTime regDate, LocalDateTime modDate,
			String username, String bankName, String source, Short status, Integer addedBy, String addedByName) {
		super();
		this.knowledge = knowledge;
		this.bankId = bankId;
		this.name = name;
		this.filename = filename;
		this.bankApproval = bankApproval;
		this.approval = approval;
		this.userId = userId;
		this.bankUserId = bankUserId;
		this.regDate = regDate;
		this.modDate = modDate;
		this.username = username;
		this.bankName = bankName;
		this.source = source;
		this.status = status;
		this.addedBy = addedBy;
		this.addedByName = addedByName;
	}

	public KnowledgeDTO(Integer knowledge, Integer bankId, String name, String filename, Integer bankApproval,
			Integer approval, Integer userId, Integer bankUserId, LocalDateTime regDate, LocalDateTime modDate,
			String username, String bankName, String source, Short status, List<TransactionEntity> transactionhistory) {
		super();
		this.knowledge = knowledge;
		this.bankId = bankId;
		this.name = name;
		this.filename = filename;
		this.bankApproval = bankApproval;
		this.approval = approval;
		this.userId = userId;
		this.bankUserId = bankUserId;
		this.regDate = regDate;
		this.modDate = modDate;
		this.username = username;
		this.bankName = bankName;
		this.source = source;
		this.status = status;
		this.transactionhistory = transactionhistory;
	}

	public Integer getKnowledge() {
		return knowledge;
	}

	public void setKnowledge(Integer knowledge) {
		this.knowledge = knowledge;
	}

	public Integer getBankId() {
		return bankId;
	}

	public void setBankId(Integer bankId) {
		this.bankId = bankId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Integer getBankApproval() {
		return bankApproval;
	}

	public void setBankApproval(Integer bankApproval) {
		this.bankApproval = bankApproval;
	}

	public Integer getApproval() {
		return approval;
	}

	public void setApproval(Integer approval) {
		this.approval = approval;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getBankUserId() {
		return bankUserId;
	}

	public void setBankUserId(Integer bankUserId) {
		this.bankUserId = bankUserId;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public Integer getAddedBy() {
		return addedBy;
	}

	public void setAddedBy(Integer addedBy) {
		this.addedBy = addedBy;
	}

	public String getAddedByName() {
		return addedByName;
	}

	public void setAddedByName(String addedByName) {
		this.addedByName = addedByName;
	}

	public List<TransactionEntity> getTransactionhistory() {
		return transactionhistory;
	}

	public void setTransactionhistory(List<TransactionEntity> transactionhistory) {
		this.transactionhistory = transactionhistory;
	}

	@Override
	public String toString() {
		return "KnowledgeDTO [knowledge=" + knowledge + ", bankId=" + bankId + ", name=" + name + ", filename="
				+ filename + ", bankApproval=" + bankApproval + ", approval=" + approval + ", userId=" + userId
				+ ", bankUserId=" + bankUserId + ", regDate=" + regDate + ", modDate=" + modDate + ", username="
				+ username + ", bankName=" + bankName + ", source=" + source + ", status=" + status + ", addedBy="
				+ addedBy + ", addedByName=" + addedByName + ", transactionhistory=" + transactionhistory + "]";
	}

}
