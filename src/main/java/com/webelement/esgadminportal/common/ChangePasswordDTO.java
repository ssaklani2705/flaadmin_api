package com.webelement.esgadminportal.common;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ChangePasswordDTO {
	private String username;
	private String oldPassword;
	private String newPassword;
	private String userId;
}
