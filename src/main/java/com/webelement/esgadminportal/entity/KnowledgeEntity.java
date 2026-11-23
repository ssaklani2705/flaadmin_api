package com.webelement.esgadminportal.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "t_knowledge")
public class KnowledgeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "i_knowledge")
	private Integer knowledge;

	@Column(name = "i_bankid")
	private Integer bankId;

	@Column(name = "s_name")
	private String name;

	@Column(name = "s_filename")
	private String filename;

	@Column(name = "i_bankapproval")
	private Integer bankApproval;

	@Column(name = "i_approval")
	private Integer approval;

	@Column(name = "i_userid")
	private Integer userId;

	@Column(name = "i_bankuserid")
	private Integer bankUserId;

	@Column(name = "ts_regdate")
	private LocalDateTime regDate;

	@Column(name = "ts_moddate")
	private LocalDateTime modDate;

	@Column(name = "s_username")
	private String username;

	@Column(name = "s_source")
	private String source;

	@Column(name = "i_status")
	private Short status;

	@Column(name = "i_addedby")
	private Integer addedBy;
	
	@Column(name = "d_date")
	private LocalDateTime knowledgedate;

	public KnowledgeEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public KnowledgeEntity(Integer knowledge, Integer bankId, String name, String filename, Integer bankApproval,
			Integer approval, Integer userId, Integer bankUserId, LocalDateTime regDate, LocalDateTime modDate,
			String username, String source, Short status, Integer addedBy) {
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
		this.source = source;
		this.status = status;
		this.addedBy = addedBy;
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
	
	

	public LocalDateTime getKnowledgedate() {
		return knowledgedate;
	}

	public void setKnowledgedate(LocalDateTime knowledgedate) {
		this.knowledgedate = knowledgedate;
	}

	@Override
	public String toString() {
		return "KnowledgeEntity [knowledge=" + knowledge + ", bankId=" + bankId + ", name=" + name + ", filename="
				+ filename + ", bankApproval=" + bankApproval + ", approval=" + approval + ", userId=" + userId
				+ ", bankUserId=" + bankUserId + ", regDate=" + regDate + ", modDate=" + modDate + ", username="
				+ username + ", source=" + source + ", status=" + status + ", addedBy=" + addedBy + "]";
	}

}
