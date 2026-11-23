package com.webelement.esgadminportal.service;

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

import com.webelement.esgadminportal.common.ApiResponse;
import com.webelement.esgadminportal.common.BankInfoDTO;
import com.webelement.esgadminportal.common.CommonFunction;
import com.webelement.esgadminportal.common.GroupDTO;
import com.webelement.esgadminportal.common.GroupInfoDTO;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.entity.GroupEntity;
import com.webelement.esgadminportal.entity.TransactionEntity;
import com.webelement.esgadminportal.repo.BankRepo;
import com.webelement.esgadminportal.repo.GroupRepo;

@Service
public class GalleryGroupService {
	
	@Autowired
	private GroupRepo groupRepo;
	
	@Autowired
	private BankRepo bankRepo;
	
	@Autowired
	private CommonFunction commonFunction;
	
	 public List<GroupDTO> getActiveGroups() {
	        return groupRepo.findActiveGroups();
	    }

	public Page<GroupInfoDTO> findGroupDetails(int page, int size, int statusIndex, String search) {
		return groupRepo.findGroupDetails(PageRequest.of(page, size), statusIndex, search);
	}
	
	
public ApiResponse<Integer> saveOrUpdate(
        GroupEntity dto,
        HttpServletRequest httpRequest
) {

    String cleanName = dto.getName().trim();
    Integer userId = dto.getUserId();

    // 🔹 ADD
    if (dto.getGroupId() == null) {

        if (groupRepo.countByNameActive(cleanName) > 0) {
            return new ApiResponse<>(
                false,
                "Group with name '" + cleanName + "' already exists",
                null
            );
        }

        GroupEntity entity = new GroupEntity();
        entity.setName(cleanName);
        entity.setDate(dto.getDate());
        entity.setUserId(userId);
        entity.setStatus((short) 1);
        entity.setRegDate(LocalDateTime.now());

        GroupEntity saved = groupRepo.save(entity);

        // ✅ HISTORY: ADD
        commonFunction.createHistoryAccess(
            userId,
            commonFunction.resolveClientIp(httpRequest),
            commonFunction.getLocalIp(),
            "Group Added",
            11,
            saved.getGroupId(),
            -1
        );

        return new ApiResponse<>(
            true,
            "Group added successfully",
            saved.getGroupId()
        );
    }

    // 🔹 UPDATE
    if (groupRepo.countByNameActiveAndNotId(cleanName, dto.getGroupId()) > 0) {
        return new ApiResponse<>(
            false,
            "Group with name '" + cleanName + "' already exists",
            null
        );
    }

    GroupEntity entity = groupRepo.findById(dto.getGroupId()).orElse(null);

    if (entity == null) {
        return new ApiResponse<>(
            false,
            "Group not found",
            null
        );
    }

    entity.setName(cleanName);
    entity.setDate(dto.getDate());
    entity.setUserId(userId);
    entity.setModDate(LocalDateTime.now());
    entity.setStatus(dto.getStatus());

    groupRepo.save(entity);

    // ✅ HISTORY: UPDATE
    commonFunction.createHistoryAccess(
        userId,
        commonFunction.resolveClientIp(httpRequest),
        commonFunction.getLocalIp(),
        "Group Updated",
        11,
        entity.getGroupId(),
        -1
    );

    return new ApiResponse<>(
        true,
        "Group updated successfully",
        entity.getGroupId()
    );
}




		public GroupInfoDTO getGroupById(int groupId) {
			GroupInfoDTO user = groupRepo.getGroupById(groupId);

			if (user != null) {
				// Append transaction history
				List<TransactionEntity> history = getTransactionLogs(11, groupId);
				
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


		
		
		public ResponseEntity<ResponseApi<String>> deleteGroup(int groupId, Integer userId, HttpServletRequest httpRequest) {
			Optional<GroupEntity> existingBank = groupRepo.findById(groupId);

			if (!existingBank.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseApi<>(false, "Group not found", null));
			}

			int updatedRows = groupRepo.deleteGroup((short) 3, groupId);

			if (updatedRows > 0) {
				commonFunction.createHistoryAccess(userId, commonFunction.resolveClientIp(httpRequest),
						commonFunction.getLocalIp(), "Group Deleted", 11, groupId, -1);
				return ResponseEntity.ok(new ResponseApi<>(true, "Group deleted successfully", null));
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(new ResponseApi<>(false, "Failed to delete Group", null));
			}
		}
	
}
