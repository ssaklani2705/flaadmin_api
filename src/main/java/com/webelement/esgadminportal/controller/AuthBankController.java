package com.webelement.esgadminportal.controller;

import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webelement.esgadminportal.common.LoginBankResponse;
import com.webelement.esgadminportal.common.LoginRequest;
import com.webelement.esgadminportal.common.LoginResponse;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.jwttoken.JwtUtil;
import com.webelement.esgadminportal.service.AuthBankService;


@RestController
@RequestMapping("/authuser")
//@CrossOrigin(value = "*")
@CrossOrigin(origins = {"http://localhost:4300","https://www.iba.org.in","http://192.168.0.173:4200"})
public class AuthBankController {
	
	@Autowired
	private AuthBankService authService;
	
	@Autowired
	private JwtUtil jwtUtil;

	@GetMapping("/captcha")
	public ResponseEntity<ResponseApi<String>> generateCaptcha(HttpSession session, HttpServletRequest httpRequest) {		
		
	    int captcha = 100000 + new Random().nextInt(900000);  
	    String captchasession = (String) session.getAttribute("captcha");
//	    System.out.println("-------------" + captcha+" captchasession : "+captchasession);
	    ResponseApi<String> response = new ResponseApi<>(true, "CAPTCHA generated", String.valueOf(captcha));
	    return ResponseEntity.ok(response);
	}
	
	
	@PostMapping("/login")
	public ResponseEntity<ResponseApi<LoginBankResponse>> checkLogin(
	        @RequestBody LoginRequest request,
	        HttpSession session,
	        HttpServletRequest httpRequest,
	        HttpServletResponse response) throws Exception {
	    ResponseEntity<ResponseApi<LoginBankResponse>> loginResponse = authService.login(request, session, httpRequest);
	    if (loginResponse.getBody().isSuccess()) {
	        String username = request.getUsername();
	        // Generate refresh token in HTTP-only cookie
	        Cookie refreshCookie = new Cookie("refreshToken", jwtUtil.generateRefreshToken(username));
	        refreshCookie.setHttpOnly(true);
//	        refreshCookie.setSecure(!"localhost".equals(request.getServerName())); // only secure in prod
//	        refreshCookie.setPath("/auth/refresh"); 
	        refreshCookie.setPath("/esguserportal/auth/refresh"); 
	        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
	        response.addCookie(refreshCookie);
	        // Remove refresh token from JSON response
	        loginResponse.getBody().getData().setRefreshToken(null);
	        
	    }
	    return loginResponse;
	}

	@PostMapping("/refresh")
	public ResponseEntity<ResponseApi<LoginResponse>> refreshToken(
	        @CookieValue(name = "refreshToken", required = false) String refreshTokenCookie) throws Exception {

	    if (refreshTokenCookie == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(new ResponseApi<>(false, "Refresh token is missing", null));
	    }

	    String username;
	    try {
	        username = jwtUtil.extractUsername(refreshTokenCookie);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(new ResponseApi<>(false, "Invalid refresh token", null));
	    }

	    if (jwtUtil.isTokenExpired(refreshTokenCookie)) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(new ResponseApi<>(false, "Refresh token expired", null));
	    }

	    // Generate new access token
	    String newAccessToken = jwtUtil.generateToken(username);

	    LoginResponse loginResponse = new LoginResponse();
	    loginResponse.setToken(newAccessToken);
	    loginResponse.setUsername(username);

	    return ResponseEntity.ok(new ResponseApi<>(true, "Access token refreshed", loginResponse));
	}
	
	
	@PostMapping("/forgotpasswordMail")
    public ResponseEntity<ResponseApi<String>> forgotpasswordMail(
            @RequestParam String emailId, HttpServletRequest httpRequest) throws Exception {
        return authService.forgotpasswordMail(emailId,httpRequest);
    }

	@PostMapping("/forgotpassword")
    public ResponseEntity<ResponseApi<String>> forgotpassword(
            @RequestParam String emailId,
            @RequestParam String userId,@RequestParam String password) throws Exception {
        return authService.forgotpassword(emailId, userId,password);
    }
	
}
