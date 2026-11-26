package com.webelement.esgadminportal.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webelement.esgadminportal.service.DashboardService;
import com.webelement.esgadminportal.service.DataService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/dasboard")
@CrossOrigin(origins ="${app.cors.allowed-origins}")
public class DashboardController {
	
	private final DashboardService dashboardService;
	
	@GetMapping({ "/status-count", "/status-count/{bankId}" })
	public ResponseEntity<Map<String, Object>> getStatusCount(@PathVariable(required = false) Integer bankId) {

		if (bankId == null) {
			bankId = 0;
		}
		Map<String, Object> response = dashboardService.getStatusCount(bankId);
		return ResponseEntity.ok(response);
	}
}
