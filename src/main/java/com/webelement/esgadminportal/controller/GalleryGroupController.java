package com.webelement.esgadminportal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.webelement.esgadminportal.common.GroupDTO;
import com.webelement.esgadminportal.common.GroupInfoDTO;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.entity.BankEntity;
import com.webelement.esgadminportal.entity.GroupEntity;
import com.webelement.esgadminportal.repo.GalleryRepo;
import com.webelement.esgadminportal.repo.GroupRepo;
import com.webelement.esgadminportal.service.GalleryGroupService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = { "http://localhost:4200", "https://www.iba.org.in", "http://192.168.0.173:4200",
		"http://192.168.0.100:4200", "http://192.168.0.100:8080" })
public class GalleryGroupController {
	
	@Autowired
	private GalleryGroupService galleryGroupService;
	
	@Autowired
	private GroupRepo groupRepo;

	@GetMapping("/getGalleryGroupDetails")
	public Map<String, Object> getGalleryGroupDetails(@RequestParam() int page, @RequestParam() int size,
			@RequestParam() int statusIndex, @RequestParam() String search) {
		Page<GroupInfoDTO> userPage = galleryGroupService.findGroupDetails(page, size, statusIndex, search);
		Map<String, Object> response = new HashMap<>();
		response.put("data", userPage.getContent());
		response.put("totalElements", userPage.getTotalElements());
		return response;
	}
	
//	 @PostMapping("/save")
//	    public ResponseEntity<?> saveOrUpdateGroup(@RequestBody GroupEntity dto) {
//
//	        GroupEntity saved = galleryGroupService.saveOrUpdate(dto);
//
//	        return ResponseEntity.ok(
//	            new ApiResponse<>(true,
//	                dto.getGroupId() == null ? "Group added successfully" : "Group updated successfully",
//	                saved.getGroupId()
//	            )
//	        );
//	    }
	
	
	@PostMapping("/save")
	public ResponseEntity<ApiResponse<Integer>> saveOrUpdateGroup(
	        @RequestBody GroupEntity dto,HttpServletRequest httpRequest) {

	    ApiResponse<Integer> response =
	            galleryGroupService.saveOrUpdate(dto,httpRequest);

	    return ResponseEntity.ok(response);
	}

	 
	 @GetMapping("/group/{id}")
	 public ApiResponse getById(@PathVariable Integer id) {

	     GroupEntity group = groupRepo.findById(id)
	         .orElseThrow(() -> new RuntimeException("Group not found"));

	     return new ApiResponse(true, "Success", group);
	 }
	 
	 
	 @GetMapping("/groupdetails/{groupId}")
		public ResponseEntity<?> getGroupById(@PathVariable int groupId) {
		 
		 System.out.println("INSIDE CONTROLLER - groupId = " + groupId);
		 GroupInfoDTO groupInfo = galleryGroupService.getGroupById(groupId);
			if (groupInfo != null) {
				return ResponseEntity.ok(groupInfo);
			} else {
				return ResponseEntity.notFound().build();
			}
		}
	

		@PostMapping("/deleteGroup")
		public ResponseEntity<ResponseApi<String>> deleteGroup(@RequestBody GroupEntity group, HttpServletRequest httpRequest)
				throws Exception {
			return galleryGroupService.deleteGroup(group.getGroupId(), group.getUserId(), httpRequest);
		}
		
		
		 @GetMapping("/activeGroup")
		    public ResponseEntity<List<GroupDTO>> getActiveGroups() {
		        return ResponseEntity.ok(galleryGroupService.getActiveGroups());
		    }

}
