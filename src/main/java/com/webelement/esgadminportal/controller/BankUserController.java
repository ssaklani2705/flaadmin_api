package com.webelement.esgadminportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.common.UserInfoDTO;
import com.webelement.esgadminportal.entity.BankUserEntity;
import com.webelement.esgadminportal.jwttoken.JwtUtil;
import com.webelement.esgadminportal.service.AuthService;
import com.webelement.esgadminportal.service.BankUserService;
import com.webelement.esgadminportal.service.JwtService;
import com.webelement.esgadminportal.service.TokenBlacklistService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class BankUserController {

	private final BankUserService bankUserService;

	@GetMapping("/getUserDetails")
	public Map<String, Object> findBasicUserInfo(@RequestParam() int page, @RequestParam() int size,
			@RequestParam() int statusIndex, @RequestParam() String search) {
		Page<UserInfoDTO> userPage = bankUserService.findBasicUserInfo(page, size, statusIndex, search);
		Map<String, Object> response = new HashMap<>();
		response.put("data", userPage.getContent());
		response.put("totalElements", userPage.getTotalElements());
		return response;
	}

	@GetMapping("/{userId}")
	public ResponseEntity<?> getUserById(@PathVariable int userId) {
		UserInfoDTO bankUser = bankUserService.getUserById(userId);
		if (bankUser != null) {
			return ResponseEntity.ok(bankUser);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/saveBankUserDetail")
	public ResponseEntity<?> saveBankUserDetail(@RequestBody BankUserEntity bankUser, HttpServletRequest httpRequest)
			throws Exception {
		return bankUserService.saveBankUserDetail(bankUser, httpRequest);
	}

	@PostMapping("/deleteBankUser")
	public ResponseEntity<ResponseApi<String>> deleteBankUser(@RequestBody BankUserEntity bankUser,
			HttpServletRequest httpRequest) throws Exception {
		return bankUserService.deleteBankUser(bankUser.getBankUserId(), bankUser.getUserId(), httpRequest);
	}
	
	@GetMapping("/getBankUsersByBankId")
	public ResponseEntity<List<UserInfoDTO>> getBankUsersByBankId(@RequestParam int bankId) {
		return ResponseEntity.ok(bankUserService.getUsersByBankId(bankId));
	}

}
