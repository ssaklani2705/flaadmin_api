package com.webelement.esgadminportal.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.webelement.esgadminportal.repo.DataRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

	private final DataRepo dataRepository;

	public Map<String, Object> getStatusCount(Integer bankId) {
		List<Object[]> results = dataRepository.countByStatusAndBankId(bankId);

		List<Map<String, Object>> list = results.stream().map(row -> {
			Map<String, Object> item = new LinkedHashMap<>();
			item.put("status", row[0]);
			item.put("count", ((Number) row[1]).longValue());
			return item;
		}).collect(Collectors.toList());

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("data", list);
		return response;
	}

}
