package com.webelement.esgadminportal.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

	private int userId;
	private String firstName;
	private String email;
	private String mobile;
	private int status;
	private String permission;

}
