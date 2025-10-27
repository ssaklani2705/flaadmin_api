package com.webelement.esgadminportal.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webelement.esgadminportal.jwttoken.JwtUtil;
import com.webelement.esgadminportal.service.TokenBlacklistService;
import com.webelement.esgadminportal.service.UserAccessLogService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = { "http://localhost:4200", "https://www.iba.org.in", "http://192.168.0.173:4200/",
		"http://192.168.0.100:4200" })
public class LogoutController {

	private final JwtUtil jwtUtil;
	private final TokenBlacklistService tokenBlacklistService;

	@Autowired
	private UserAccessLogService accessLogService;

	public LogoutController(JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
		this.jwtUtil = jwtUtil;
		this.tokenBlacklistService = tokenBlacklistService;
	}

//	@PostMapping("/logout")
//	public ResponseEntity<?> logout(@RequestHeader("Authorization") String token,
//			@RequestBody Map<String, String> body) {
//		String sessionIdStr = body.get("sessionId");
//		Integer sessionId = 0;
//		try {
//			sessionId = Integer.parseInt(sessionIdStr);
//		} catch (NumberFormatException e) {
//			// return ResponseEntity.badRequest().body("Invalid sessionId");
//		}
//		accessLogService.updateLogout(sessionId);
//		String jwt = token.substring(7); // Remove "Bearer "
//		String jti = jwtUtil.extractAllClaims(jwt).getId();
//		tokenBlacklistService.blacklistToken(jti);
//		return ResponseEntity.ok("Logged out successfully");
//	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader("Authorization") String token,
	                                @RequestBody Map<String, String> body) {
	    String sessionIdStr = body.get("sessionId");
	    Integer sessionId = 0;

	    try {
	        sessionId = Integer.parseInt(sessionIdStr);
	    } catch (NumberFormatException e) {
	        return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid sessionId"));
	    }

	    try {
	        // update logout in user access logs
	        accessLogService.updateLogout(sessionId);

	        // strip Bearer prefix
	        String jwt = token.substring(7);

	        // extract claims
	        String jti = jwtUtil.extractAllClaims(jwt).getId();

	        // blacklist token
	        tokenBlacklistService.blacklistToken(jti);

	        return ResponseEntity.ok(Collections.singletonMap("message", "Logged out successfully"));

	    } catch (Exception ex) {
	        // if token invalid or expired
	        return ResponseEntity.status(401).body(Collections.singletonMap("error", "Invalid token"));
	    }
	}
   

}