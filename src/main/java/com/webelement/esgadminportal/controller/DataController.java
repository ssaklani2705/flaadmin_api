package com.webelement.esgadminportal.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webelement.esgadminportal.common.ApiResponse;
import com.webelement.esgadminportal.entity.DataEntity;
import com.webelement.esgadminportal.service.DataService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/data")
public class DataController {

	private final DataService dataService;

	@PostMapping("/save-or-update")
	public ApiResponse<DataEntity> saveOrUpdateData(@Valid @RequestBody DataEntity data,
			HttpServletRequest httpRequest) {
		ApiResponse<DataEntity> response = dataService.saveOrUpdateData(data, httpRequest);

		return response;
	}
}
