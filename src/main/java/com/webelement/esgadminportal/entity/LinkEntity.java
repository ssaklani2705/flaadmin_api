package com.webelement.esgadminportal.entity;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_link")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LinkEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "i_linkid")
	private Integer linkId;

	@Column(name = "s_name")
	private String name;

	@Column(name = "s_url")
	private String url;

	@Column(name = "i_status")
	private Short status;

	@Column(name = "i_userid")
	private Integer userId;

	@Column(name = "ts_regdate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp regDate;

	@Column(name = "ts_moddate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp modDate;

	public LinkEntity(Integer linkId, String name, String url, Short status) {
		super();
		this.linkId = linkId;
		this.name = name;
		this.url = url;
		this.status = status;
	}

	@Transient
	private List<TransactionEntity> transactionhistory;

	@Override
	public String toString() {
		return "LinkEntity [linkId=" + linkId + ", name=" + name + ", url=" + url + ", status=" + status + ", userId="
				+ userId + ", regDate=" + regDate + ", modDate=" + modDate + ", transactionhistory="
				+ transactionhistory + "]";
	}

}
