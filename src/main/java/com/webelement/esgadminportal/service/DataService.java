package com.webelement.esgadminportal.service;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.webelement.esgadminportal.common.ApiResponse;
import com.webelement.esgadminportal.common.DataDetailDTO;
import com.webelement.esgadminportal.common.DataInfoDTO;
import com.webelement.esgadminportal.entity.DataEntity;
import com.webelement.esgadminportal.repo.BankUserRepo;
import com.webelement.esgadminportal.repo.DataRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataService {

	private final DataRepo dataRepo;
	private final BankUserRepo bankUserRepo;
	
	public Page<DataInfoDTO> findDataDetails(int page, int size, int statusIndex, String search,int userId) {
		System.out.println("userId : "+userId);
		Integer bankId = bankUserRepo.findBankIdByBankUserId(userId);
		System.out.println("bankId : "+bankId);
		
		return dataRepo.findDataDetails(PageRequest.of(page, size), statusIndex, search,bankId);
	}

	@Transactional
	public ApiResponse<DataEntity> saveOrUpdateData(DataEntity data, HttpServletRequest httpRequest) {

		boolean isNew = (data.getId() == null || data.getId() == 0);

		DataEntity entity;

		if (!isNew) {

			entity = dataRepo.findById(data.getId()).orElseThrow(() -> new RuntimeException("Data record not found"));

			entity = entity.toBuilder().build();

		} else {
			entity = new DataEntity();
		}

		// Common field mapping
		entity.setCode(data.getCode());
		entity.setState(data.getState());
		entity.setCatOfBranch(data.getCatOfBranch());
		entity.setStatusOfPerpetrator(data.getStatusOfPerpetrator());
		entity.setTypeOfAccount(data.getTypeOfAccount());
		entity.setTypeOfAccOther(data.getTypeOfAccOther());
		entity.setSecOfAcc(data.getSecOfAcc());
		entity.setBorrowalAcc(data.getBorrowalAcc());
		entity.setBankingArrangement(data.getBankingArrangement());
		entity.setAreaOfOperation(data.getAreaOfOperation());
		entity.setAreaOther(data.getAreaOther());
		entity.setClassificationOfFraud(data.getClassificationOfFraud());
		entity.setClassificationOther(data.getClassificationOther());
		entity.setTypeOfFraud(data.getTypeOfFraud());
		entity.setFraudTypeOther(data.getFraudTypeOther());
		entity.setTotalAmount(data.getTotalAmount());
		entity.setOccurrence(data.getOccurrence());
		entity.setDetection(data.getDetection());
		entity.setClassification(data.getClassification());
		entity.setReportedToRbi(data.getReportedToRbi());
		entity.setDelayReason(data.getDelayReason());
		entity.setFullPaymentSettlement(data.getFullPaymentSettlement());
		entity.setDetectionMethod(data.getDetectionMethod());
		entity.setDetectionOtherSource(data.getDetectionOtherSource());
		entity.setFraudCommittedBy(data.getFraudCommittedBy());
		entity.setControllingOfficeDetect(data.getControllingOfficeDetect());
		entity.setInternalAuditConducted(data.getInternalAuditConducted());
		entity.setAuditDetectionReason(data.getAuditDetectionReason());
		entity.setAuditActionTaken(data.getAuditActionTaken());
		entity.setLeaComplaint(data.getLeaComplaint());
		entity.setPreventiveSteps(data.getPreventiveSteps());
		entity.setAmountRecovered(data.getAmountRecovered());
		entity.setLossToBank(data.getLossToBank());
		entity.setProvisionHeld(data.getProvisionHeld());
		entity.setAmountWrittenOff(data.getAmountWrittenOff());
		entity.setCompromiseSettlement(data.getCompromiseSettlement());
		entity.setCompromiseAmount(data.getCompromiseAmount());

		if (isNew) {
			entity.setStatus(1);
		} else {
			entity.setStatus(data.getStatus());
		}

		DataEntity saveEntity = dataRepo.save(entity);

		String action = isNew ? "Data added successfully" : "Data updated successfully";

		return new ApiResponse<>(true, action, saveEntity);
	}

	public DataDetailDTO findDataById(Integer id) {
		return dataRepo.findById(id).map(DataDetailDTO::from).orElse(null);
	}
}
