package com.webelement.esgadminportal.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.webelement.esgadminportal.Mapper.DataMapper;
import com.webelement.esgadminportal.common.ApiResponse;
import com.webelement.esgadminportal.common.CommonFunction;
import com.webelement.esgadminportal.common.DataDetailDTO;
import com.webelement.esgadminportal.common.DataExcelParser;
import com.webelement.esgadminportal.common.DataInfoDTO;
import com.webelement.esgadminportal.entity.DataEntity;
import com.webelement.esgadminportal.entity.StateEntity;
import com.webelement.esgadminportal.entity.TransactionEntity;
import com.webelement.esgadminportal.repo.BankUserRepo;
import com.webelement.esgadminportal.repo.DataRepo;
import com.webelement.esgadminportal.repo.StateRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataService {

	private final DataRepo dataRepo;

	private final BankUserRepo bankUserRepo;
	private final DataMapper dataMapper;
	private final StateRepo stateRepo;
	
	private final CommonFunction commonFunction;
	
	public Page<DataInfoDTO> findDataDetails(int page, int size, int statusIndex, String search,int userId,String catOfBranch) {
		System.out.println("userId : "+userId);
		Integer bankId = bankUserRepo.findBankIdByBankUserId(userId);
		//System.out.println("bankId : "+bankId);
		return dataRepo.findDataDetails(PageRequest.of(page, size), statusIndex, search,bankId,catOfBranch);
	}

//	public Page<DataInfoDTO> findDataDetails(int page, int size, int statusIndex, String search) {
//		return dataRepo.findDataDetails(PageRequest.of(page, size), statusIndex, search);
//	}
	
	 public Page<DataInfoDTO> adminDataListService(int page, int size, int statusIndex, String search,int bankId) {
	        return dataMapper.toInfoDtoPage(
	            dataRepo.dataAdminIndexList(PageRequest.of(page, size), statusIndex, search,bankId));
	    }
	
	 @Transactional
	    public boolean approveData(Integer dataId, Integer userId, HttpServletRequest httpRequest) {
	        int updatedRows = dataRepo.approveData(dataId);
	        
	        if (updatedRows > 0) {
		         commonFunction.createHistoryAccess(
		                 userId,
		                 commonFunction.resolveClientIp(httpRequest),
		                 commonFunction.getLocalIp(),
		                 "Data Approved",
		                 2,
		                 dataId,
		                 userId);
		     }
	        return updatedRows > 0;
	    }
	 
	 
	 @Transactional
	 public boolean softDeleteData(Integer dataId, Integer userId, HttpServletRequest httpRequest) {
	     int updatedRows = dataRepo.softDeleteData(dataId);

	     if (updatedRows > 0) {
	         commonFunction.createHistoryAccess(
	                 userId,
	                 commonFunction.resolveClientIp(httpRequest),
	                 commonFunction.getLocalIp(),
	                 "Data Deleted",
	                 2,
	                 dataId,
	                 userId);
	     }
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
	            
	         // Only Draft (1) and Approval Pending (2) records can be edited.
	            Integer currentStatus = entity.getStatus();

	            if (Objects.equals(currentStatus, 3)) {
	                throw new IllegalArgumentException("Approved record cannot be edited");
	            }
	            if (Objects.equals(currentStatus, 4)) {
	                throw new IllegalArgumentException("Deleted record cannot be edited");
	            }

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
	                request.getId() == null ? "Data Added" : "Data Updated",
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

	    

		private static final long EXCEL_MAX_SIZE = 5L * 1024 * 1024; // 5 MB
		private static final int STATUS_DRAFT = 1;


@Transactional
public Map<String, Object> uploadDataExcel(MultipartFile file, Integer addedBy, Integer bankId,
                                           HttpServletRequest httpRequest) {
	
	System.out.println("UPLOAD HIT v2");
	System.out.println("PARSER LOADED FROM: "
	        + DataExcelParser.class.getProtectionDomain().getCodeSource().getLocation());

    if (file == null || file.isEmpty()) {
        throw new IllegalArgumentException("Please select an Excel file");
    }
    String fileName = file.getOriginalFilename();
    if (fileName == null || !fileName.toLowerCase().endsWith(".xlsx")) {
        throw new IllegalArgumentException("Only .xlsx files are allowed");
    }
    if (file.getSize() > EXCEL_MAX_SIZE) {
        throw new IllegalArgumentException("File size must not exceed 5 MB");
    }

    // TODO: use your own state repository / entity here (the same source as getAllStates()).
    Set<String> stateNames = stateRepo.findAll().stream()
            .map(StateEntity::getName)
            .collect(Collectors.toSet());

    DataExcelParser.ParseResult result;
    try (InputStream in = file.getInputStream()) {
        // Rows are saved as Draft so the user can review and Submit from the Edit page.
        result = DataExcelParser.parse(in, stateNames, STATUS_DRAFT, addedBy, bankId);
    } catch (IOException | RuntimeException e) {
        e.printStackTrace();   // or log.error("Unable to parse uploaded Excel", e);
        throw new IllegalArgumentException(
                "Unable to read the file. Please upload a valid .xlsx file based on the template");
    }

    Map<String, Object> response = new LinkedHashMap<>();
    response.put("totalRows", result.getTotalRows());

    // All-or-nothing: if any row is invalid, nothing is saved.
    if (result.hasErrors()) {
        response.put("success", false);
        response.put("message", "Validation failed. No records were saved. Fix the errors and upload again.");
        response.put("savedRows", 0);
        response.put("errorCount", result.getErrorCount());
        response.put("errors", result.getErrors());
        return response;
    }

    List<DataEntity> saved = dataRepo.saveAll(result.getEntities());

    String clientIp = commonFunction.resolveClientIp(httpRequest);
    String localIp = commonFunction.getLocalIp();
    for (DataEntity d : saved) {
        commonFunction.createHistoryAccess(addedBy, clientIp, localIp,
                "Data Added (Excel Upload)", 2, d.getId(), addedBy);
    }

    response.put("success", true);
    response.put("message", saved.size() + " record(s) uploaded successfully as Draft");
    response.put("savedRows", saved.size());
    response.put("errorCount", 0);
    response.put("errors", Collections.emptyList());
    return response;
}
	    
	    
	    
}
