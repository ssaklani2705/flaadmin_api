package com.webelement.esgadminportal.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.webelement.esgadminportal.common.BankInfoDTO;
import com.webelement.esgadminportal.common.CommonFunction;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.common.TypeDTO;
import com.webelement.esgadminportal.common.ValidateData;
import com.webelement.esgadminportal.entity.BankEntity;
import com.webelement.esgadminportal.entity.TransactionEntity;
import com.webelement.esgadminportal.jwttoken.JwtUtil;
import com.webelement.esgadminportal.repo.BankRepo;
import com.webelement.esgadminportal.repo.MailLogRepo;
import com.webelement.esgadminportal.repo.SmtpRepo;
import com.webelement.esgadminportal.repo.TypeRepo;
import com.webelement.esgadminportal.repo.UserLoginRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankService {

	
	private BankRepo bankRepo;

	
	private TypeRepo typeRepo;

	
	private CommonFunction commonFunction;
	
	private ValidateData validateData;

	public Page<BankInfoDTO> findBankDetails(int page, int size, int statusIndex, String search) {
		return bankRepo.findBankDetails(PageRequest.of(page, size), statusIndex, search);
	}

	public BankInfoDTO getBankById(int bankId) {
		BankInfoDTO user = bankRepo.getBankById(bankId);

		if (user != null) {
			// Append transaction history
			List<TransactionEntity> history = getTransactionLogs(1, bankId);
			
			user.setTransactionhistory(history);
		}

		return user;
	}

	public List<TransactionEntity> getTransactionLogs(int moduleId, Integer recordId) {
		List<Object[]> results = bankRepo.getTransactionLogs(moduleId, recordId);
		
		return results.stream().map(obj -> {
			TransactionEntity dto = new TransactionEntity();
			dto.setEntryDate((String) obj[0]);
			dto.setName((String) obj[1]);
			dto.setAction((String) obj[2]);
			dto.setUserId(obj[3] != null ? ((Number) obj[3]).intValue() : null);
			dto.setFlag((String) obj[4]);
			return dto;
		}).collect(Collectors.toList());
	}

	public List<TypeDTO> getAllActiveBankTypes() {
		return typeRepo.findAllActiveBankType();
	}

	public List<BankInfoDTO> getAllActiveBank() {
		return bankRepo.findAllActiveBank();
	}

	public BankEntity createBank(BankEntity bank, HttpServletRequest httpRequest) {

		// Sanitize
//		    String cleanName = validateData.replacename(bank.getName());
		String cleanName = validateData.replacedesc(bank.getName());
		if (cleanName.isEmpty()) {
			throw new RuntimeException("Bank name is invalid or empty");
		}
		bank.setName(cleanName); // Important: update bank entity with sanitized name

		// 2 Check duplicate using sanitized name
		if (bankRepo.countByNameActive(cleanName) > 0) {
			throw new RuntimeException("Bank with name '" + cleanName + "' already exists");
		}

		// Set timestamps and status
		bank.setRegDate(new Timestamp(System.currentTimeMillis()));
		bank.setModDate(new Timestamp(System.currentTimeMillis()));
		bank.setStatus((short) 1);

		// Save bank
		BankEntity savedBank = bankRepo.save(bank);

		// Create history
		commonFunction.createHistoryAccess(savedBank.getUserId(), commonFunction.resolveClientIp(httpRequest),
				commonFunction.getLocalIp(), "Bank Added", 1, savedBank.getBankId(), -1);

		return savedBank;
	}

	// Update bank
	public BankEntity updateBank(BankEntity bankDetails, HttpServletRequest httpRequest) {

		// Sanitize
		String cleanName = validateData.replacedesc(bankDetails.getName());
		if (cleanName.isEmpty()) {
			throw new RuntimeException("Bank name is invalid or empty");
		}
		bankDetails.setName(cleanName); // Important: update bank entity with sanitized name

		// Find existing bank
		BankEntity bank = bankRepo.findById(bankDetails.getBankId())
				.orElseThrow(() -> new RuntimeException("Bank not found with ID " + bankDetails.getBankId()));

		// Check duplicate name excluding current bank
		bankRepo.findByNameIgnoreCaseAndBankIdNot(bankDetails.getName(), bankDetails.getBankId())
				.ifPresent(existing -> {
					throw new RuntimeException("Bank with name '" + bankDetails.getName() + "' already exists");
				});

		// Update fields
		bank.setName(bankDetails.getName());
		bank.setTypeId(bankDetails.getTypeId());
		bank.setStatus(bankDetails.getStatus());
		bank.setUserId(bankDetails.getUserId());
		bank.setModDate(new Timestamp(System.currentTimeMillis()));

		// Save updated bank
		BankEntity updatedBank = bankRepo.save(bank);

		// Get the bankId (uid) for logging/history
		Integer uid = updatedBank.getBankId(); // or Integer if bankId is Integer

		// Log history
		commonFunction.createHistoryAccess(updatedBank.getUserId(), commonFunction.resolveClientIp(httpRequest),
				commonFunction.getLocalIp(), "Bank Updated", 1, // assuming 2 = update action
				uid, -1);

		return updatedBank;
	}

	public ResponseEntity<ResponseApi<String>> deleteBank(int bankId, int createdBy, HttpServletRequest httpRequest) {
		Optional<BankEntity> existingBank = bankRepo.findById(bankId);

		if (!existingBank.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseApi<>(false, "Bank not found", null));
		}

		int updatedRows = bankRepo.deleteBank((short) 3, bankId);

		if (updatedRows > 0) {
			commonFunction.createHistoryAccess(createdBy, commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "Bank Deleted", 1, bankId, -1);
			return ResponseEntity.ok(new ResponseApi<>(true, "Bank deleted successfully", null));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseApi<>(false, "Failed to delete bank", null));
		}
	}

}
