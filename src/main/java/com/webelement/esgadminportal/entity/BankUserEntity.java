package com.webelement.esgadminportal.entity;

import java.sql.Timestamp;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_bankuser")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BankUserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "i_bankuserid")
	private Integer bankUserId;

	@Column(name = "i_bankid")
	private Integer bankId;

	@Column(name = "s_name")
	private String name;

	@Column(name = "s_contactno")
	private String contactNo;

	@Column(name = "s_email")
	private String email;

	@Column(name = "s_password")
	@JsonIgnore
	private String password;

	@Column(name = "i_status")
	private Short status;

	@Column(name = "i_userid")
//    @JsonIgnore
	private Integer userId;

	@Column(name = "ts_regdate")
	@JsonIgnore
	private Timestamp regDate;

	@Column(name = "ts_moddate")
	@JsonIgnore
	private Timestamp modDate;

	@Column(name = "ts_sentlinkdate")
	@JsonIgnore
	private Timestamp sentLinkDate;

	@Column(name = "s_random")
	@JsonIgnore
	private String random;

}
