package com.webelement.esgadminportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webelement.esgadminportal.common.ApiResponse;
import com.webelement.esgadminportal.common.BankInfoDTO;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.common.TypeDTO;
import com.webelement.esgadminportal.entity.BankEntity;
import com.webelement.esgadminportal.service.BankService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = { "http://localhost:4200", "https://www.iba.org.in", "http://192.168.0.173:4200",
		"http://192.168.0.100:4200", "http://192.168.0.100:8080" })
public class BankController {

	@Autowired
	private BankService bankService;

	@GetMapping("/getBankDetails")
	public Map<String, Object> findBankDetails(@RequestParam() int page, @RequestParam() int size,
			@RequestParam() int statusIndex, @RequestParam() String search) {
		Page<BankInfoDTO> userPage = bankService.findBankDetails(page, size, statusIndex, search);
		Map<String, Object> response = new HashMap<>();
		response.put("data", userPage.getContent());
		response.put("totalElements", userPage.getTotalElements());
		return response;
	}

	@GetMapping("/bank/{bankId}")
	public ResponseEntity<?> getBankById(@PathVariable int bankId) {
		BankInfoDTO bank = bankService.getBankById(bankId);
		if (bank != null) {
			return ResponseEntity.ok(bank);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/bankType")
	public ResponseEntity<List<TypeDTO>> getActiveTypes() {
		return ResponseEntity.ok(bankService.getAllActiveBankTypes());
	}

	@GetMapping("/all-bank-list")
	public ResponseEntity<List<BankInfoDTO>> getAllBank() {
		return ResponseEntity.ok(bankService.getAllActiveBank());
	}

	@PostMapping("/saveBankDetails")
	public ResponseEntity<ApiResponse<BankEntity>> createBank(@RequestBody BankEntity bank,
			HttpServletRequest httpRequest) {
		try {
			bankService.createBank(bank, httpRequest);
			return ResponseEntity.ok(new ApiResponse<>(true, "Bank added successfully", null));
		} catch (RuntimeException ex) {
			return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse<>(false, "Something went wrong", null));
		}
	}

	@PostMapping("/updateBankDetails")
	public ResponseEntity<ApiResponse<BankEntity>> updateBank(@RequestBody BankEntity bank,
			HttpServletRequest httpRequest) {
		try {
			bankService.updateBank(bank, httpRequest);
			return ResponseEntity.ok(new ApiResponse<>(true, "Bank updated successfully", null));
		} catch (RuntimeException ex) {
			return ResponseEntity.badRequest().body(new ApiResponse<>(false, ex.getMessage(), null));
		}
	}

	@PostMapping("/deleteBank")
	public ResponseEntity<ResponseApi<String>> deleteBank(@RequestBody BankEntity bank, HttpServletRequest httpRequest)
			throws Exception {
		return bankService.deleteBank(bank.getBankId(), bank.getUserId(), httpRequest);
	}

}
