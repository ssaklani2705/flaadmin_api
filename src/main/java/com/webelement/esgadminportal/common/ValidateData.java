package com.webelement.esgadminportal.common;

import org.springframework.stereotype.Component;

@Component
public class ValidateData {

	public String replacename(String input) {
		return sanitize(input, "[^a-zA-Z-'\\s\\.]+");
	}

	public String replaceint(String input) {
		return sanitize(input, "[^0-9\\-]+");
	}

	public String replacedouble(String input) {
		return sanitize(input, "[^0-9\\.]+");
	}

	public String replacenamenum(String input) {
		return sanitize(input, "[^a-zA-Z0-9]+");
	}

	public String replacedesc(String input) {
		return sanitize(input, "[^a-zA-Z0-9,;:=$\\-_\\.\\+\\'\\\"/\\\\!@`#%&()#\\s\\?]+");
	}

	public String replacedeschtml(String input) {
		return sanitize(input, "[^a-zA-Z0-9,;=$\\-_\\.\\+\\'\\\"/\\\\!@`#%&()#\\s\\?<>:\n\r]+");
	}

	public String replacereg(String input) {
		return sanitize(input, "[^a-zA-Z0-9\\s\\-/\\\\]+");
	}

	public String replacenamewithand(String input) {
		return sanitize(input, "[^a-zA-Z-'\\s\\.&]+");
	}

	public String replacedate(String input) {
		return sanitize(input, "[^0-9\\-]+");
	}

	public String replacetime(String input) {
		return sanitize(input, "[^0-9AMP\\-:\\s]+");
	}

	public static String replacechar(String input) {
		return sanitize(input, "[^a-zA-Z]+");
	}

	public String replacealphacomma(String input) {
		return sanitize(input, "[^0-9,\\s]+");
	}

	public boolean emailvalidation(String input) {
		if (input == null)
			return false;
		String regexPattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		return input.matches(regexPattern);
	}

	public boolean mobilevalidation(String input) {
		if (input == null)
			return false;
		String mobilePattern = "[6-9][0-9]{9}";
		return input.matches(mobilePattern);
	}

	// Utility method for repeated sanitization logic
	private static String sanitize(String input, String regex) {
		if (input == null || input.isEmpty())
			return "";
		input = input.replaceAll("(?i)iframe", "").replaceAll("(?i)hyperlink", "").replaceAll("(?i)script", "")
				.replaceAll("(?i)style", "");
		return input.replaceAll(regex, "").trim();
	}
}
