package com.webelement.esgadminportal.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webelement.esgadminportal.common.LoginRequest;
import com.webelement.esgadminportal.common.LoginResponse;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.jwttoken.JwtUtil;
import com.webelement.esgadminportal.service.AuthService;
import com.webelement.esgadminportal.service.TokenBlacklistService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = { "http://localhost:4200", "https://www.iba.org.in", "http://192.168.0.173:4200",
		"http://192.168.0.100:4200" }, allowCredentials = "true")
public class AuthController {

	@Autowired
	private AuthService authService;
	@Autowired
	private TokenBlacklistService blacklistService;

	@Autowired
	private JwtUtil jwtUtil;

	@GetMapping("/test")
	public String test() {

		return "09-09-2025 10:24";
	}

	@GetMapping("/captcha")
	public ResponseEntity<ResponseApi<String>> generateCaptcha(HttpSession session, HttpServletRequest httpRequest) {
		int captcha = 100000 + new Random().nextInt(900000);
		session.setAttribute("captcha",String.valueOf(captcha));
//		String captchasession = (String) session.getAttribute("captcha");
		ResponseApi<String> response = new ResponseApi<>(true, "CAPTCHA generated", String.valueOf(captcha));
		return ResponseEntity.ok(response);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseApi<LoginResponse>> checkLogin(@RequestBody LoginRequest request, HttpSession session,
			HttpServletRequest httpRequest, HttpServletResponse response) throws Exception {
		ResponseEntity<ResponseApi<LoginResponse>> loginResponse = authService.login(request, session, httpRequest);
		if (loginResponse.getBody().isSuccess()) {
			String username = request.getUsername();
			// Generate refresh token in HTTP-only cookie
			Cookie refreshCookie = new Cookie("refreshToken", jwtUtil.generateRefreshToken(username));
			refreshCookie.setHttpOnly(true);
//	        refreshCookie.setSecure(!"localhost".equals(request.getServerName())); // only secure in prod
//	        refreshCookie.setPath("/auth/refresh"); 
			refreshCookie.setPath("/esgadminportal/auth/refresh");
			refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
			response.addCookie(refreshCookie);
			// Remove refresh token from JSON response
			loginResponse.getBody().getData().setRefreshToken(null);

		}
		return loginResponse;
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			Map<String, Object> body = new HashMap<>();
			body.put("success", false);
			body.put("message", "No refresh token found");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
		}

		String refreshToken = null;
		for (Cookie cookie : cookies) {
			if ("refreshToken".equals(cookie.getName())) {
				refreshToken = cookie.getValue();
			}
		}

		if (refreshToken == null) {
			Map<String, Object> body = new HashMap<>();
			body.put("success", false);
			body.put("message", "Refresh token missing");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
		}

		try {
			if (jwtUtil.isTokenExpired(refreshToken)) {
				Map<String, Object> body = new HashMap<>();
				body.put("success", false);
				body.put("message", "Refresh token expired");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
			}

			String jti = jwtUtil.extractJti(refreshToken);
			if (blacklistService.isBlacklisted(jti)) {
				Map<String, Object> body = new HashMap<>();
				body.put("success", false);
				body.put("message", "Refresh token blacklisted");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
			}

			String username = jwtUtil.extractUsername(refreshToken);

			// Rotate refresh token
			blacklistService.blacklistToken(jti); // old token invalid
			String newRefreshToken = jwtUtil.generateRefreshToken(username);

			Cookie refreshCookie = new Cookie("refreshToken", newRefreshToken);
			refreshCookie.setHttpOnly(true);
			refreshCookie.setPath("/esgadminportal");
			refreshCookie.setMaxAge(7 * 24 * 60 * 60);
			response.addCookie(refreshCookie);

			// Generate new access token
			String newAccessToken = jwtUtil.generateAccessToken(username);

			Map<String, Object> tokens = new HashMap<>();
			tokens.put("accessToken", newAccessToken);

			Map<String, Object> body = new HashMap<>();
			body.put("success", true);
			body.put("data", tokens);

			return ResponseEntity.ok(body);

		} catch (Exception e) {
			Map<String, Object> body = new HashMap<>();
			body.put("success", false);
			body.put("message", "Invalid refresh token");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
		}
	}

	@PostMapping("/forgotpasswordMail")
	public ResponseEntity<ResponseApi<String>> forgotpasswordMail(@RequestParam String emailId,
			HttpServletRequest httpRequest) throws Exception {
		return authService.forgotpasswordMail(emailId, httpRequest);
	}

	@PostMapping("/forgotpassword")
	public ResponseEntity<ResponseApi<String>> forgotpassword(@RequestParam String emailId, @RequestParam String userId,
			@RequestParam String password) throws Exception {
		return authService.forgotpassword(emailId, userId, password);
	}

}