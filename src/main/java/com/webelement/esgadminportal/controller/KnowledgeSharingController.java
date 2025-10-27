package com.webelement.esgadminportal.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.webelement.esgadminportal.common.KnowledgeDTO;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.service.KnowledgeSharingService;

@RestController
@RequestMapping("/knowledgeSharing")
@CrossOrigin(origins = { "http://localhost:4200", "https://www.iba.org.in" })
public class KnowledgeSharingController {

	@Autowired
	KnowledgeSharingService knowledgeSharingService;

	@GetMapping("/getKnowledgeSharingDetails")
	public Map<String, Object> getKnowledgeSharingDetails(@RequestParam int page, @RequestParam int size,
			@RequestParam int statusIndex, @RequestParam String search) {
		Page<KnowledgeDTO> knowledgePage = knowledgeSharingService.getKnowledgeSharingDetails(page, size, statusIndex,
				search);
		Map<String, Object> response = new HashMap<>();
		response.put("data", knowledgePage.getContent());
		response.put("totalElements", knowledgePage.getTotalElements());
		return response;
	}

	@PostMapping("/addKnowledgeSharing")
	public ResponseEntity<Map<String, String>> addKnowledgeSharing(@RequestParam("title") String title,
			@RequestParam("source") String source, @RequestParam("file") MultipartFile file,
			@RequestParam("iUserId") int iUserId, @RequestParam("sUsername") String sUsername,
			HttpServletRequest httpRequest) {
		return knowledgeSharingService.addKnowledgeSharing(source, title, file, iUserId, sUsername, httpRequest);
	}

	@PostMapping("/updateKnowledgeSharing")
	public ResponseEntity<Map<String, String>> updateKnowledgeSharing(@RequestParam("knowledgeId") int knowledgeId,
			@RequestParam("title") String title, @RequestParam("source") String source,
			@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("iUserId") int iUserId,
			@RequestParam("sUsername") String sUsername, @RequestParam("status") int status,
			HttpServletRequest httpRequest) {

		return knowledgeSharingService.updateKnowledgeSharing(knowledgeId, source, title, file, iUserId, sUsername,
				status, httpRequest);
	}

	@GetMapping("/getKnowledgeSharing/{knowledgeId}")
	public ResponseEntity<?> getKnowledgeSharing(@PathVariable int knowledgeId) {
		KnowledgeDTO knowledge = knowledgeSharingService.getKnowledgeSharing(knowledgeId);
		if (knowledge != null) {
			return ResponseEntity.ok(knowledge);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/getKnowledgeSharingDocById/{id}")
	public ResponseEntity<Resource> getEventImageById(@PathVariable Integer id) throws IOException {
		System.out.println("-------------------");
		return knowledgeSharingService.getKnowledgeSharingDocById(id);
	}

	@PostMapping("/approveKnowledge")
	public ResponseEntity<ResponseApi<String>> approveKnowledge(@RequestParam("knowledgeId") int knowledgeId,
			@RequestParam("userId") int userId, HttpServletRequest httpRequest) throws Exception {
		System.out.println("knowledgeId : " + knowledgeId + " userId : " + userId);
		return knowledgeSharingService.approveKnowledge(knowledgeId, userId, httpRequest);
	}
	
	@PostMapping("/revertKnowledge")
	public ResponseEntity<ResponseApi<String>> revertKnowledge(@RequestParam("knowledgeId") int knowledgeId,
			@RequestParam("userId") int userId, HttpServletRequest httpRequest) throws Exception {
		System.out.println("knowledgeId : " + knowledgeId + " userId : " + userId);
		return knowledgeSharingService.revertKnowledge(knowledgeId, userId, httpRequest);
	}

	@PostMapping("/deleteKnowledge")
	public ResponseEntity<ResponseApi<String>> deleteKnowledge(@RequestParam("knowledgeId") int knowledgeId,
			@RequestParam("userId") int userId, HttpServletRequest httpRequest) throws Exception {
		System.out.println("knowledgeId : " + knowledgeId + " userId : " + userId);
		return knowledgeSharingService.deleteKnowledge(knowledgeId, userId, httpRequest);
	}

}
