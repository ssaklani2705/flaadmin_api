package com.webelement.esgadminportal.common;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
	private String token;
	private int userId;
	private String username;
	private String refreshToken;
	private int sessionId;
	private List<ModulePermissionDTO> modules;

	public LoginResponse(String token, int userId, String username, int sessionId, List<ModulePermissionDTO> modules) {
		super();
		this.token = token;
		this.userId = userId;
		this.username = username;
		this.sessionId = sessionId;
		this.modules = modules;
	}

}
