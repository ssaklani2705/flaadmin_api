package com.webelement.esgadminportal.common;

import java.time.LocalDateTime;
import java.util.List;

import com.webelement.esgadminportal.entity.TransactionEntity;

public class EventAndWebinarDTO {

	private Integer eventId;
	private String title;
	private String location;
	private String description;
	private LocalDateTime eventDate;
	private Integer approval;
	private Integer bankApproval;
	private Integer userId;
	private Integer BankUserId;
	private String addedBy;
	private Integer type;
	private Integer bankId;
	private String name;
	private Short status;
	private String eventtime;
	private String shortDesc;
	private String filename;
	private String link;
	private String durationHours;
	private String durationMinutes;

	List<TransactionEntity> history;

	public EventAndWebinarDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EventAndWebinarDTO(Integer eventId, String title, String location, String description,
			LocalDateTime eventDate, Integer approval, Integer bankApproval, Integer userId, Integer bankUserId,
			String addedBy, Integer type, Integer bankId, String name, Short status, String eventtime,
			String durationHours, String durationMinutes) {
		super();
		this.eventId = eventId;
		this.title = title;
		this.location = location;
		this.description = description;
		this.eventDate = eventDate;
		this.approval = approval;
		this.bankApproval = bankApproval;
		this.userId = userId;
		BankUserId = bankUserId;
		this.addedBy = addedBy;
		this.type = type;
		this.bankId = bankId;
		this.name = name;
		this.status = status;
		this.eventtime = eventtime;
		this.durationHours = durationHours;
		this.durationMinutes = durationMinutes;
	}

	public EventAndWebinarDTO(Integer eventId, String title, String location, String description,
			LocalDateTime eventDate, Integer approval, Integer bankApproval, Integer userId, Integer bankUserId,
			String addedBy, Integer type, Integer bankId, String name, Short status, String eventtime, String shortDesc,
			String filename, String link, String durationHours, String durationMinutes) {
		super();
		this.eventId = eventId;
		this.title = title;
		this.location = location;
		this.description = description;
		this.eventDate = eventDate;
		this.approval = approval;
		this.bankApproval = bankApproval;
		this.userId = userId;
		BankUserId = bankUserId;
		this.addedBy = addedBy;
		this.type = type;
		this.bankId = bankId;
		this.name = name;
		this.status = status;
		this.eventtime = eventtime;
		this.shortDesc = shortDesc;
		this.filename = filename;
		this.link = link;
		this.durationHours = durationHours;
		this.durationMinutes = durationMinutes;
	}

	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getEventDate() {
		return eventDate;
	}

	public void setEventDate(LocalDateTime eventDate) {
		this.eventDate = eventDate;
	}

	public Integer getApproval() {
		return approval;
	}

	public void setApproval(Integer approval) {
		this.approval = approval;
	}

	public Integer getBankApproval() {
		return bankApproval;
	}

	public void setBankApproval(Integer bankApproval) {
		this.bankApproval = bankApproval;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getBankUserId() {
		return BankUserId;
	}

	public void setBankUserId(Integer bankUserId) {
		BankUserId = bankUserId;
	}

	public String getAddedBy() {
		return addedBy;
	}

	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public List<TransactionEntity> getHistory() {
		return history;
	}

	public void setHistory(List<TransactionEntity> history) {
		this.history = history;
	}

	public String getEventtime() {
		return eventtime;
	}

	public void setEventtime(String eventtime) {
		this.eventtime = eventtime;
	}

	public String getShortDesc() {
		return shortDesc;
	}

	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDurationHours() {
		return durationHours;
	}

	public void setDurationHours(String durationHours) {
		this.durationHours = durationHours;
	}

	public String getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(String durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

	public EventAndWebinarDTO(Integer eventId, String title, String location, String description,
			LocalDateTime eventDate, Integer approval, Integer bankApproval, Integer userId, Integer bankUserId,
			String addedBy, Integer type, Integer bankId, String name, Short status, List<TransactionEntity> history) {
		super();
		this.eventId = eventId;
		this.title = title;
		this.location = location;
		this.description = description;
		this.eventDate = eventDate;
		this.approval = approval;
		this.bankApproval = bankApproval;
		this.userId = userId;
		BankUserId = bankUserId;
		this.addedBy = addedBy;
		this.type = type;
		this.bankId = bankId;
		this.name = name;
		this.status = status;
		this.history = history;
	}

}
