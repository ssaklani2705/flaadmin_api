package com.webelement.esgadminportal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.webelement.esgadminportal.common.UserAccessLogDTO;
import com.webelement.esgadminportal.jwttoken.JwtUtil;
import com.webelement.esgadminportal.service.JwtService;
import com.webelement.esgadminportal.service.TokenBlacklistService;
import com.webelement.esgadminportal.service.UserAccessLogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/useraccesslog")
//@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class UserAccessLogController {
	
	private final UserAccessLogService accessLogService;
	
	@GetMapping("/getUserAccessDetails")
    public Map<String, Object> getUserAccessDetails(
        @RequestParam int page,
        @RequestParam int size,
        @RequestParam String search
    ) {
        Page<UserAccessLogDTO> userAccessDetailsPage = accessLogService.getUserAccessDetails(page, size, search);
        Map<String, Object> response = new HashMap<>();
        response.put("data", userAccessDetailsPage.getContent());
        response.put("totalElements", userAccessDetailsPage.getTotalElements());
        return response;  
    }

}
