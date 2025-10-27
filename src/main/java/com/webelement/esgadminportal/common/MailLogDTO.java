package com.webelement.esgadminportal.common;

import java.time.LocalDateTime;

public class MailLogDTO {

	private Integer mailLogId;
	private Integer type;
	private String name;
	private String to;
	private String cc;
	private String bcc;
	private String from;
	private String subject;
	private String filename;
	private Integer moduleId;
	private LocalDateTime regDate;
	private LocalDateTime modDate;
	private String ipAddress;
	private String localIp;
	private Integer status;

	public MailLogDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MailLogDTO(Integer mailLogId, Integer type, String name, String to, String cc, String bcc, String from,
			String subject, String filename, Integer moduleId, LocalDateTime regDate, LocalDateTime modDate,
			String ipAddress, String localIp, Integer status) {
		super();
		this.mailLogId = mailLogId;
		this.type = type;
		this.name = name;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.from = from;
		this.subject = subject;
		this.filename = filename;
		this.moduleId = moduleId;
		this.regDate = regDate;
		this.modDate = modDate;
		this.ipAddress = ipAddress;
		this.localIp = localIp;
		this.status = status;
	}

	public Integer getMailLogId() {
		return mailLogId;
	}

	public void setMailLogId(Integer mailLogId) {
		this.mailLogId = mailLogId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Integer getModuleId() {
		return moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
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

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getLocalIp() {
		return localIp;
	}

	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "MailLogDTO [mailLogId=" + mailLogId + ", type=" + type + ", name=" + name + ", to=" + to + ", cc=" + cc
				+ ", bcc=" + bcc + ", from=" + from + ", subject=" + subject + ", filename=" + filename + ", moduleId="
				+ moduleId + ", regDate=" + regDate + ", modDate=" + modDate + ", ipAddress=" + ipAddress + ", localIp="
				+ localIp + ", status=" + status + "]";
	}

}