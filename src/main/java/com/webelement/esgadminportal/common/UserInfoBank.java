package com.webelement.esgadminportal.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoBank {
	
	private int userId;
    private String firstName;
    private String email;
    private String mobile;
    private int status;
    private int bankid;
	public UserInfoBank(int userId, String firstName, String email, String mobile, int status) {
		super();
		this.userId = userId;
		this.firstName = firstName;
		this.email = email;
		this.mobile = mobile;
		this.status = status;
	}

}
