package com.webelement.esgadminportal.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.webelement.esgadminportal.Mapper.DataMapper;
import com.webelement.esgadminportal.common.ApiResponse;
import com.webelement.esgadminportal.common.CommonFunction;
import com.webelement.esgadminportal.common.DataDetailDTO;
import com.webelement.esgadminportal.common.DataInfoDTO;
import com.webelement.esgadminportal.entity.DataEntity;
import com.webelement.esgadminportal.entity.TransactionEntity;
import com.webelement.esgadminportal.repo.BankUserRepo;
import com.webelement.esgadminportal.repo.DataRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataService {

	private final DataRepo dataRepo;

	private final BankUserRepo bankUserRepo;
	private final DataMapper dataMapper;
	
	private final CommonFunction commonFunction;
	
	public Page<DataInfoDTO> findDataDetails(int page, int size, int statusIndex, String search,int userId) {
		System.out.println("userId : "+userId);
		Integer bankId = bankUserRepo.findBankIdByBankUserId(userId);
		System.out.println("bankId : "+bankId);
		
		return dataRepo.findDataDetails(PageRequest.of(page, size), statusIndex, search,bankId);
	}

//	public Page<DataInfoDTO> findDataDetails(int page, int size, int statusIndex, String search) {
//		return dataRepo.findDataDetails(PageRequest.of(page, size), statusIndex, search);
//	}
	
	 public Page<DataInfoDTO> adminDataListService(int page, int size, int statusIndex, String search,int bankId) {
	        return dataMapper.toInfoDtoPage(
	            dataRepo.dataAdminIndexList(PageRequest.of(page, size), statusIndex, search,bankId));
	    }
	
	 @Transactional
	    public boolean approveData(Integer dataId) {
	        int updatedRows = dataRepo.approveData(dataId);
	        return updatedRows > 0;
	    }

	@Transactional
	public ApiResponse<DataEntity> saveOrUpdateData(DataEntity data, HttpServletRequest httpRequest) {

		if (data.getAddedBy() == null) {
		    throw new RuntimeException("User id is required");
		}
		
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
		entity.setAddedBy(data.getAddedBy());   // updated on every Save/Submit, not at approval
		entity.setBankId(data.getBankId());

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

	
	 @Transactional
	    public DataEntity saveDataAdmin(DataEntity request,HttpServletRequest httpRequest) {

	        if (request == null) {
	            throw new IllegalArgumentException("Request data is required");
	        }

	        Integer status = request.getStatus() == null
	                ? 1
	                : request.getStatus();

	        if (!Objects.equals(status, 1) && !Objects.equals(status, 2)) {
	            throw new IllegalArgumentException("Invalid data status");
	        }

	        // Validate only when submitting
	        if (status == 2) {
	            validateSubmitData(request);
	        }

	        DataEntity entity;

	        // EDIT
	        if (request.getId() != null) {

	            entity = dataRepo.findById(request.getId())
	                    .orElseThrow(() ->
	                            new IllegalArgumentException("Data record not found"));

	            // Copy all form fields, preserving system-managed fields.
	            BeanUtils.copyProperties(
	                    request,
	                    entity,
	                    "id",
	                    "regDate",
	                    "modDate",
	                    "bank"
	            );

	        } else {

	            // ADD
	            entity = new DataEntity();

	            BeanUtils.copyProperties(
	                    request,
	                    entity,
	                    "id",
	                    "regDate",
	                    "modDate",
	                    "bank"
	            );
	        }

	        entity.setStatus(status);

	        // Save null fields as null.
//	        return dataRepo.save(entity);
	        DataEntity savedData = dataRepo.save(entity);
	        
//	        System.out.println("Data saved successfully. ID: " + savedData.getId());
	        
	     // Save history after successful insert/update
	        commonFunction.createHistoryAccess(
	                entity.getAddedBy(),
	                commonFunction.resolveClientIp(httpRequest),
	                commonFunction.getLocalIp(),
	                entity.getId() == null ? "Data Added" : "Data Updated",
	                2,
	                savedData.getId(),
	                entity.getAddedBy()
	        );

	        return savedData;

	    }

	    private void validateSubmitData(DataEntity data) {

	        // Add your actual mandatory fields here.

	        if (data.getCode() == null || data.getCode().trim().isEmpty()) {
	            throw new IllegalArgumentException("Code is required");
	        }

	        if (data.getTotalAmount() == null) {
	            throw new IllegalArgumentException("Total Amount is required");
	        }

	        if (data.getOccurrence() == null) {
	            throw new IllegalArgumentException("Occurrence Date is required");
	        }

	        if (data.getDetection() == null) {
	            throw new IllegalArgumentException("Detection Date is required");
	        }
	    }
	    
	    
	    
}
