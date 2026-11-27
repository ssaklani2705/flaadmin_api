package com.webelement.esgadminportal.common;

import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssociationDTO {

	private Integer id;
	private Short associationType;
	private String name;
	private Short status;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date  regDate;

	public AssociationDTO(Integer id, String name, Short status) {
		
		this.id = id;
		this.name = name;
		this.status = status;
	}
	
	
	
	
	
}
