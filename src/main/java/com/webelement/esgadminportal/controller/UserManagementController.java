package com.webelement.esgadminportal.controller;

import java.util.HashMap;
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

import com.webelement.esgadminportal.common.UserInfo;
import com.webelement.esgadminportal.entity.UserLoginEntity;
import com.webelement.esgadminportal.service.UserManagementService;

@RestController
@RequestMapping("/admin")
//@CrossOrigin(value = "*")
@CrossOrigin(origins = { "http://localhost:4200", "https://www.iba.org.in", "http://192.168.0.173:4200",
		"http://192.168.0.100:4200" })
public class UserManagementController {

	@Autowired
	private UserManagementService userService;

	@GetMapping("/getUserManagementDetails")
	public Map<String, Object> findBasicUserInfo(@RequestParam() int page, @RequestParam() int size,
			@RequestParam() int statusIndex, @RequestParam() String search) {
		System.out.println(
				"page : " + page + " size : " + size + " statusIndex : " + statusIndex + " search : " + search);
		Page<UserInfo> userPage = userService.findBasicUserInfo(page, size, statusIndex, search);
		Map<String, Object> response = new HashMap<>();
		response.put("data", userPage.getContent());
		response.put("totalElements", userPage.getTotalElements());
		return response;
	}

	@GetMapping("/getUserManagementDetails/{userId}")
	public ResponseEntity<?> getUserById(@PathVariable int userId) {
		UserLoginEntity user = userService.getUserById(userId);
		if (user != null) {
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/saveUserDetail")
	public ResponseEntity<?> saveUserDetail(@RequestBody UserLoginEntity userRequest, HttpServletRequest httpRequest)
			throws Exception {
		return userService.saveUserDetail(userRequest, httpRequest);
	}

	@PostMapping("/deleteUser")
	public ResponseEntity<com.webelement.esgadminportal.common.ResponseApi<String>> deleteUser(@RequestParam int userId,
			@RequestParam int createdBy, HttpServletRequest httpRequest) throws Exception {
		return userService.deleteUser(userId, createdBy, httpRequest);
	}

}
