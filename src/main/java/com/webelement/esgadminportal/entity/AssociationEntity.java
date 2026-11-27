package com.webelement.esgadminportal.entity;

import java.security.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_association")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssociationEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "i_id")
	private Integer id;

	@Column(name = "i_associationtype")
	private Short associationType;

	@Column(name = "s_name", length = 200)
	private String name;

	@Column(name = "i_status")
	private Short status;

	@Column(name = "ts_regdate")
	private Timestamp regDate;
}
