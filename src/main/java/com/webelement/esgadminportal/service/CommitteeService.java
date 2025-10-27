package com.webelement.esgadminportal.service;


import com.webelement.esgadminportal.common.CommitteeDTO;
import com.webelement.esgadminportal.common.CommonFunction;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.entity.CommitteeEntity;
import com.webelement.esgadminportal.entity.TransactionEntity;
import com.webelement.esgadminportal.repo.BankRepo;
import com.webelement.esgadminportal.repo.CommitteeRepo;

import java.time.LocalDateTime;
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

@Service
public class CommitteeService {

    @Autowired
    private CommitteeRepo committeeRepo;
    
    @Autowired
    private CommonFunction commonFunction;
    
    @Autowired
	private BankRepo bankRepo;
    
    public Page<CommitteeDTO> findCommitteeDetails(int page, int size, int statusIndex, String search) {
        return committeeRepo.findCommitteeDetails(PageRequest.of(page, size), statusIndex, search);
    }
    
    public CommitteeDTO getCommitteeById(int committeeId) {
        CommitteeDTO committee = committeeRepo.getCommitteeById(committeeId);

        if (committee != null) {
            // Example: fetch transaction logs for this committee (if you have such a table)
            List<TransactionEntity> history = getTransactionLogs(10, committeeId); // 2 = module type for committee
            committee.setTransactionHistory(history);
        }

        return committee;
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
    
    
    // 🔹 Create Committee
    public CommitteeEntity createCommittee(CommitteeEntity committee,HttpServletRequest httpRequest) {
        // validate email uniqueness only for active members
        int count = committeeRepo.countByEmailActive(committee.getEmail());
        if (count > 0) {
            throw new RuntimeException("Email already exists among active members: " + committee.getEmail());
        }

        // set timestamps
        committee.setRegDate(LocalDateTime.now());
        committee.setModDate(LocalDateTime.now());
        committee.setStatus((short)1);
        
        CommitteeEntity committeeEntity = committeeRepo.save(committee);
        commonFunction.createHistoryAccess(
        		committee.getUserId(),
                commonFunction.resolveClientIp(httpRequest),
                commonFunction.getLocalIp(),
                "Committee Member Added",
                10,
                committeeEntity.getCommitteeId(),
                -1
        );
        return committeeEntity;
    }
    
    
    // 🔹 Update Committee
    public CommitteeEntity updateCommittee(Integer id, CommitteeEntity updatedCommittee,HttpServletRequest httpRequest) {
        return committeeRepo.findById(id).map(existing -> {
            // validate email uniqueness (ignore self)
            int count = committeeRepo.countByEmailActiveAndCommitteeId(updatedCommittee.getEmail(), updatedCommittee.getCommitteeId());
            if (count > 0 && !existing.getEmail().equalsIgnoreCase(updatedCommittee.getEmail())) {
                throw new RuntimeException("Email already exists among active members: " + updatedCommittee.getEmail());
            }

//            if (updatedCommittee.getStatus() == null ||
//                    !(updatedCommittee.getStatus() == 1 || updatedCommittee.getStatus() == 2)) {
//                throw new RuntimeException("Invalid status. Allowed values are 1 or 2.");
//            }

            existing.setBankId(updatedCommittee.getBankId());
            existing.setName(updatedCommittee.getName());
            existing.setDesignation(updatedCommittee.getDesignation());
            existing.setEmail(updatedCommittee.getEmail());
            existing.setSequence(updatedCommittee.getSequence());
            existing.setStatus(updatedCommittee.getStatus());
            existing.setUserId(updatedCommittee.getUserId());

            // update timestamp
            existing.setModDate(LocalDateTime.now());
            commonFunction.createHistoryAccess(
            		updatedCommittee.getUserId(),
                    commonFunction.resolveClientIp(httpRequest),
                    commonFunction.getLocalIp(),
                    "Committee Member Updated",
                    10,
                    existing.getCommitteeId(),
                    -1
            );
            return committeeRepo.save(existing);
        }).orElseThrow(() -> new RuntimeException("Committee not found with id " + id));
    }

    
    public ResponseEntity<ResponseApi<String>> deleteCommittee(int committeeId, int createdBy, HttpServletRequest httpRequest) {
        Optional<CommitteeEntity> existingCommittee = committeeRepo.findById(committeeId);

        if (!existingCommittee.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseApi<>(false, "Committee member not found", null));
        }

        int updatedRows = committeeRepo.deleteCommittee((short) 3, committeeId);

        if (updatedRows > 0) {
            commonFunction.createHistoryAccess(
                    createdBy,
                    commonFunction.resolveClientIp(httpRequest),
                    commonFunction.getLocalIp(),
                    "Committee Member Deleted",
                    10,
                    committeeId,
                    -1
            );

            return ResponseEntity.ok(new ResponseApi<>(true, "Committee member deleted successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseApi<>(false, "Failed to delete committee member", null));
        }
    }

    
}