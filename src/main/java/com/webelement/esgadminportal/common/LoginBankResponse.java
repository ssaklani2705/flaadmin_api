package com.webelement.esgadminportal.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginBankResponse {

	private String token;
    private int userId;
    private String username;
    private String refreshToken;
    private int sessionId;
    private int bankId;
	public LoginBankResponse(String token, int userId, String username, int sessionId) {
		super();
		this.token = token;
		this.userId = userId;
		this.username = username;
		this.sessionId = sessionId;
	}
	public LoginBankResponse(String token, int userId, String username, int sessionId, int bankId) {
		super();
		this.token = token;
		this.userId = userId;
		this.username = username;
		this.sessionId = sessionId;
		this.bankId = bankId;
	}
    
	
}
