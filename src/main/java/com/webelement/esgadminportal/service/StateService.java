package com.webelement.esgadminportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.webelement.esgadminportal.common.StateDropdownDTO;
import com.webelement.esgadminportal.repo.StateRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StateService {

	private final StateRepo stateRepo;

	public List<StateDropdownDTO> getAllStates() {
		return stateRepo.getAllStates();
	}
}
