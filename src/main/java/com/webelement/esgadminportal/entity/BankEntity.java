package com.webelement.esgadminportal.entity;

import java.sql.Timestamp;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BankEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "i_bankid")
	private Integer bankId;

	@Column(name = "i_typeid")
	private Integer typeId;

	@Column(name = "s_name")
	private String name;

	@Column(name = "i_status")
	private Short status;

	@Column(name = "i_userid")
	private Integer userId;

	@Column(name = "ts_regdate")
	private Timestamp regDate;

	@Column(name = "ts_moddate")
	private Timestamp modDate;

}