package com.webelement.esgadminportal.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "t_event")
public class EventEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "i_eventid")
	private Integer iEventId;

	@Column(name = "i_bankid")
	private Integer iBankId;

	@Column(name = "i_type")
	private Integer iType;

	@Column(name = "s_title")
	private String sTitle;

	@Column(name = "s_link")
	private String sLink;

	@Column(name = "s_filename")
	private String sFilename;

	@Column(name = "s_shortdesc")
	private String sShortDesc;

	@Column(name = "s_description")
	private String sDescription;

	@Column(name = "d_date")
	private LocalDateTime dDate;

	@Column(name = "s_location")
	private String sLocation;

	@Column(name = "i_addedby")
	private Integer iAddedBy;

	@Column(name = "i_userid")
	private Integer iUserId;

	@Column(name = "i_bankuserid")
	private Integer iBankUserId;

	@Column(name = "s_username")
	private String sUsername;

	@Column(name = "i_bankapproval")
	private Integer iBankApproval;

	@Column(name = "i_approval")
	private Integer iApproval;

	@Column(name = "ts_regdate")
	private LocalDateTime tsRegDate;

	@Column(name = "ts_moddate")
	private LocalDateTime tsModDate;

	@Column(name = "s_time")
	private String time;

	@Column(name = "i_status")
	private Short status;

	@Column(name = "s_duration_hours")
	private String durationHours;

	@Column(name = "s_duration_minutes")
	private String durationMinutes;

	public EventEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EventEntity(Integer iEventId, Integer iBankId, Integer iType, String sTitle, String sLink, String sFilename,
			String sShortDesc, String sDescription, LocalDateTime dDate, String sLocation, Integer iAddedBy,
			Integer iUserId, Integer iBankUserId, String sUsername, Integer iBankApproval, Integer iApproval,
			LocalDateTime tsRegDate, LocalDateTime tsModDate, String time, Short status) {
		super();
		this.iEventId = iEventId;
		this.iBankId = iBankId;
		this.iType = iType;
		this.sTitle = sTitle;
		this.sLink = sLink;
		this.sFilename = sFilename;
		this.sShortDesc = sShortDesc;
		this.sDescription = sDescription;
		this.dDate = dDate;
		this.sLocation = sLocation;
		this.iAddedBy = iAddedBy;
		this.iUserId = iUserId;
		this.iBankUserId = iBankUserId;
		this.sUsername = sUsername;
		this.iBankApproval = iBankApproval;
		this.iApproval = iApproval;
		this.tsRegDate = tsRegDate;
		this.tsModDate = tsModDate;
		this.time = time;
		this.status = status;
	}

	public Integer getiEventId() {
		return iEventId;
	}

	public void setiEventId(Integer iEventId) {
		this.iEventId = iEventId;
	}

	public Integer getiBankId() {
		return iBankId;
	}

	public void setiBankId(Integer iBankId) {
		this.iBankId = iBankId;
	}

	public Integer getiType() {
		return iType;
	}

	public void setiType(Integer iType) {
		this.iType = iType;
	}

	public String getsTitle() {
		return sTitle;
	}

	public void setsTitle(String sTitle) {
		this.sTitle = sTitle;
	}

	public String getsLink() {
		return sLink;
	}

	public void setsLink(String sLink) {
		this.sLink = sLink;
	}

	public String getsFilename() {
		return sFilename;
	}

	public void setsFilename(String sFilename) {
		this.sFilename = sFilename;
	}

	public String getsShortDesc() {
		return sShortDesc;
	}

	public void setsShortDesc(String sShortDesc) {
		this.sShortDesc = sShortDesc;
	}

	public String getsDescription() {
		return sDescription;
	}

	public void setsDescription(String sDescription) {
		this.sDescription = sDescription;
	}

	public LocalDateTime getdDate() {
		return dDate;
	}

	public void setdDate(LocalDateTime dDate) {
		this.dDate = dDate;
	}

	public String getsLocation() {
		return sLocation;
	}

	public void setsLocation(String sLocation) {
		this.sLocation = sLocation;
	}

	public Integer getiAddedBy() {
		return iAddedBy;
	}

	public void setiAddedBy(Integer iAddedBy) {
		this.iAddedBy = iAddedBy;
	}

	public Integer getiUserId() {
		return iUserId;
	}

	public void setiUserId(Integer iUserId) {
		this.iUserId = iUserId;
	}

	public Integer getiBankUserId() {
		return iBankUserId;
	}

	public void setiBankUserId(Integer iBankUserId) {
		this.iBankUserId = iBankUserId;
	}

	public String getsUsername() {
		return sUsername;
	}

	public void setsUsername(String sUsername) {
		this.sUsername = sUsername;
	}

	public Integer getiBankApproval() {
		return iBankApproval;
	}

	public void setiBankApproval(Integer iBankApproval) {
		this.iBankApproval = iBankApproval;
	}

	public Integer getiApproval() {
		return iApproval;
	}

	public void setiApproval(Integer iApproval) {
		this.iApproval = iApproval;
	}

	public LocalDateTime getTsRegDate() {
		return tsRegDate;
	}

	public void setTsRegDate(LocalDateTime tsRegDate) {
		this.tsRegDate = tsRegDate;
	}

	public LocalDateTime getTsModDate() {
		return tsModDate;
	}

	public void setTsModDate(LocalDateTime tsModDate) {
		this.tsModDate = tsModDate;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Short getStatus() {
		return status;
	}

	public void setStatus(Short status) {
		this.status = status;
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

	@Override
	public String toString() {
		return "EventEntity [iEventId=" + iEventId + ", iBankId=" + iBankId + ", iType=" + iType + ", sTitle=" + sTitle
				+ ", sLink=" + sLink + ", sFilename=" + sFilename + ", sShortDesc=" + sShortDesc + ", sDescription="
				+ sDescription + ", dDate=" + dDate + ", sLocation=" + sLocation + ", iAddedBy=" + iAddedBy
				+ ", iUserId=" + iUserId + ", iBankUserId=" + iBankUserId + ", sUsername=" + sUsername
				+ ", iBankApproval=" + iBankApproval + ", iApproval=" + iApproval + ", tsRegDate=" + tsRegDate
				+ ", tsModDate=" + tsModDate + ", time=" + time + ", status=" + status + "]";
	}

}
