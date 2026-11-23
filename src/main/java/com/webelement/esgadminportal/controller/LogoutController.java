package com.webelement.esgadminportal.controller;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webelement.esgadminportal.common.ApiResponse;
import com.webelement.esgadminportal.common.RefreshTokenResponse;
import com.webelement.esgadminportal.jwttoken.JwtUtil;
import com.webelement.esgadminportal.service.JwtService;
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
	
	@Autowired
	private JwtService jwtService;

	public LogoutController(JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
		this.jwtUtil = jwtUtil;
		this.tokenBlacklistService = tokenBlacklistService;
	}


	
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
	
	
	
	 @PostMapping("/refreshtoken")
	    public ApiResponse<RefreshTokenResponse> refreshToken(HttpServletRequest request) {
	        try {
	            // 🔹 Extract old token from Authorization header
	            String oldToken = jwtService.extractTokenFromRequest(request);
	            if (oldToken == null) {
	                return new ApiResponse<>(false, "Authorization header missing", null);
	            }

	            // 🔹 Validate old token
	            if (!jwtService.validateToken(oldToken)) {
	                return new ApiResponse<>(false, "Invalid or expired token", null);
	            }

	            // ✅ Allow refresh even if expired — only verify signature
	            if (!jwtService.isTokenSignatureValid(oldToken)) {
	                return new ApiResponse<>(false, "Invalid token signature", null);
	            }

	            // 🔹 Extract username (subject)
	            String username = jwtService.extractUsername(oldToken);

	            // 🔹 Generate new JWT
	            String newToken = jwtUtil.generateAccessToken(username);

	            // 🔹 Return refreshed token
	            RefreshTokenResponse data = new RefreshTokenResponse(newToken);
	            return new ApiResponse<>(true, "Token refreshed successfully", data);

	        } catch (Exception e) {
	            e.printStackTrace();
	            return new ApiResponse<>(false, "Token refresh failed", null);
	        }
	    }
   

}