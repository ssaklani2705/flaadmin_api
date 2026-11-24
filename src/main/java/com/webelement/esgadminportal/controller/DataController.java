package com.webelement.esgadminportal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webelement.esgadminportal.common.ApiResponse;
import com.webelement.esgadminportal.common.DataDetailDTO;
import com.webelement.esgadminportal.common.DataInfoDTO;
import com.webelement.esgadminportal.entity.DataEntity;
import com.webelement.esgadminportal.service.DataService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/data")
@CrossOrigin(origins = { "http://localhost:4200", "https://www.iba.org.in", "http://192.168.0.173:4200",
		"http://192.168.0.100:4200", "http://192.168.0.100:8080" })

public class DataController {

	private final DataService dataService;

	@GetMapping("/list")
	public Map<String, Object> findDataDetails(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "0") int statusIndex,
			@RequestParam(defaultValue = "") String search) {
		Page<DataInfoDTO> dataPage = dataService.findDataDetails(page, size, statusIndex, search);
		Map<String, Object> response = new HashMap<>();
		response.put("data", dataPage.getContent());
		response.put("totalElements", dataPage.getTotalElements());
		return response;
	}

	@PostMapping("/save-or-update")
	public ApiResponse<DataEntity> saveOrUpdateData(@Valid @RequestBody DataEntity request,
			HttpServletRequest httpRequest) {
		ApiResponse<DataEntity> response = dataService.saveOrUpdateData(request, httpRequest);
		return response;
	}

	@GetMapping("/getDataById/{id}")
	public Map<String, Object> getDataById(@PathVariable Integer id) {
		Map<String, Object> response = new HashMap<>();
		DataDetailDTO detail = dataService.findDataById(id);

		if (detail == null) {
			response.put("success", false);
			response.put("message", "Record not found");
			return response;
		}

		response.put("success", true);
		response.put("data", detail);
		response.put("history", new ArrayList<>()); // TODO: fill from an action-history table
		return response;
	}
}
