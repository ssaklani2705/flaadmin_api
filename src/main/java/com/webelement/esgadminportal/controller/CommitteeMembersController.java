package com.webelement.esgadminportal.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.webelement.esgadminportal.common.ApiResponse;
import com.webelement.esgadminportal.common.CommitteeDTO;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.entity.CommitteeEntity;
import com.webelement.esgadminportal.service.CommitteeService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = { "http://localhost:4200", "https://www.iba.org.in"})
public class CommitteeMembersController {

  
    @Autowired
    private CommitteeService committeeService;

    @GetMapping("/getCommitteeDetails")
    public Map<String, Object> findCommitteeDetails(@RequestParam int page,
                                                    @RequestParam int size,
                                                    @RequestParam int statusIndex,
                                                    @RequestParam String search) {
        Page<CommitteeDTO> committeePage = committeeService.findCommitteeDetails(page, size, statusIndex, search);

        Map<String, Object> response = new HashMap<>();
        response.put("data", committeePage.getContent());
        response.put("totalElements", committeePage.getTotalElements());
        return response;
    }
    
    @GetMapping("/getCommitteeDetailById/{committeeId}")
    public ResponseEntity<?> getCommitteeById(@PathVariable int committeeId) {
        CommitteeDTO committee = committeeService.getCommitteeById(committeeId);
        if (committee != null) {
            return ResponseEntity.ok(committee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    
    // 🔹 Create API
    @PostMapping("/saveCommittee")
    public ResponseEntity<ApiResponse<CommitteeEntity>> createCommittee(
            @RequestBody CommitteeEntity committee,
            HttpServletRequest httpRequest) {
        try {
            CommitteeEntity saved = committeeService.createCommittee(committee, httpRequest);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Committee created successfully", saved)
            );
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Something went wrong", null));
        }
    }

//    @PostMapping("/saveCommittee")
//    public ResponseEntity<?> createCommittee(@RequestBody CommitteeEntity committee,HttpServletRequest httpRequest) {
//        try {
//            committeeService.createCommittee(committee,httpRequest);
//            return ResponseEntity.ok("Committee created successfully");
//        } catch (RuntimeException ex) {
//            return ResponseEntity.badRequest().body(ex.getMessage());
//        }
//    }
    
    @PostMapping("/updateCommittee")
    public ResponseEntity<ApiResponse<CommitteeEntity>> updateCommittee(
            @RequestBody CommitteeEntity committee,
            HttpServletRequest httpRequest) {
        try {
            CommitteeEntity updated = committeeService.updateCommittee(
                    committee.getCommitteeId(),
                    committee,
                    httpRequest
            );

            return ResponseEntity.ok(
                new ApiResponse<>(true, "Committee updated successfully", updated)
            );
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, ex.getMessage(), null));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Something went wrong", null));
        }
    }

    
//    public ResponseEntity<?> updateCommittee(@RequestBody CommitteeEntity committee,HttpServletRequest httpRequest) {
//        try {
//            committeeService.updateCommittee(committee.getCommitteeId(), committee,httpRequest);
//            return ResponseEntity.ok("Committee updated successfully");
//        } catch (RuntimeException ex) {
//            return ResponseEntity.badRequest().body(ex.getMessage());
//        }
//    }
    
    
    @PostMapping("/deleteCommittee")
    public ResponseEntity<ResponseApi<String>> deleteCommittee(
            @RequestBody CommitteeEntity committee,
            HttpServletRequest httpRequest) throws Exception {
        return committeeService.deleteCommittee(committee.getCommitteeId(), committee.getUserId(), httpRequest);
    }

	
}
