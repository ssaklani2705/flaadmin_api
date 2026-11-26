package com.webelement.esgadminportal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webelement.esgadminportal.service.StateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/state")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class StateController {

	private final StateService stateService;

	@GetMapping("/list")
	public ResponseEntity<Map<String, Object>> getAllStates() {

		Map<String, Object> response = new HashMap<>();
		response.put("data", stateService.getAllStates());

		return ResponseEntity.ok(response);
	}
}
