package com.webelement.esgadminportal.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webelement.esgadminportal.common.MailLogDTO;
import com.webelement.esgadminportal.service.MailLogService;

@RestController
@RequestMapping("/mailLog")
@CrossOrigin(origins = { "http://localhost:4200", "http://192.168.0.196:4200", "https://www.iba.org.in" })
public class MailLogController {

	@Autowired
	MailLogService mailLogService;

	@GetMapping("/getMailLogDetails")
	public Map<String, Object> getMailLogDetails(@RequestParam int page, @RequestParam int size,
			@RequestParam String search) {
		Page<MailLogDTO> mailLogPage = mailLogService.getMailLogDetails(page, size, search);
		Map<String, Object> response = new HashMap<>();
		response.put("data", mailLogPage.getContent());
		response.put("totalElements", mailLogPage.getTotalElements());
		return response;
	}

	@GetMapping("/getMailLogHtml")
	public ResponseEntity<Map<String, String>> getMailLogHtml(@RequestParam int mailLogId) {
		String htmlContent = mailLogService.getMailHtmlById(mailLogId);
		Map<String, String> response = new HashMap<>();
		response.put("htmlContent", htmlContent);
		return ResponseEntity.ok(response);
	}

}