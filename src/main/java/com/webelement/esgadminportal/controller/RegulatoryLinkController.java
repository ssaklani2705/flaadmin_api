package com.webelement.esgadminportal.controller;

import java.util.HashMap;
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
import com.webelement.esgadminportal.entity.LinkEntity;
import com.webelement.esgadminportal.service.RegulatoryLinkService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = { "http://localhost:4200", "https://www.iba.org.in", "http://192.168.0.173:4200",
		"http://192.168.0.100:4200" })
public class RegulatoryLinkController {

	@Autowired
	private RegulatoryLinkService regulatoryLinkService;

	@GetMapping("/getRegulatoryLinkDetails")
	public Map<String, Object> findBankDetails(@RequestParam() int page, @RequestParam() int size,
			@RequestParam() int statusIndex, @RequestParam() String search) {
		Page<LinkEntity> userPage = regulatoryLinkService.findRegulatoryLinkDetails(page, size, statusIndex, search);
		Map<String, Object> response = new HashMap<>();
		response.put("data", userPage.getContent());
		response.put("totalElements", userPage.getTotalElements());
		return response;
	}

	// Add new link
	@PostMapping("/addRegulatoryLink")
	public ResponseEntity<Map<String, String>> addLink(@RequestBody LinkEntity link, HttpServletRequest httpRequest) {
		regulatoryLinkService.addLink(link, httpRequest);
		Map<String, String> resp = new HashMap<>();
		resp.put("message", "Link added successfully");
		return ResponseEntity.ok(resp);
	}

	// Get by ID
	@GetMapping("/getRegulatoryLinkById/{id}")
	public ResponseEntity<LinkEntity> getById(@PathVariable("id") Integer id) {
		LinkEntity link = regulatoryLinkService.getById(id);
		return ResponseEntity.ok(link);
	}

	@GetMapping("/regulatoryLink/{linkId}")
	public ResponseEntity<?> getBankById(@PathVariable int linkId) {
		LinkEntity link = regulatoryLinkService.getRegulatoryLinkById(linkId);
		if (link != null) {
			return ResponseEntity.ok(link);
		} else {
			return ResponseEntity.notFound().build();

		}
	}

	// Update link
	@PostMapping("/updateRegulatoryLink/{id}")
	public ResponseEntity<Map<String, String>> updateLink(@PathVariable("id") Integer id,
			@RequestBody LinkEntity link) {
		regulatoryLinkService.updateLink(id, link);
		Map<String, String> resp = new HashMap<>();
		resp.put("message", "Link updated successfully");
		return ResponseEntity.ok(resp);
	}

	@PostMapping("/deleteLinkById")
	public ResponseEntity<Map<String, Object>> deleteGalleryById(@RequestBody LinkEntity linkEntity,
			HttpServletRequest httpRequest) {

		Map<String, Object> response = new HashMap<>();

		if (linkEntity.getLinkId() == null) {
			response.put("success", false);
			response.put("message", "LinkId is required");
			return ResponseEntity.badRequest().body(response);
		}

		boolean deleted = regulatoryLinkService.deleteLinkById(linkEntity.getLinkId(), linkEntity.getUserId(),
				httpRequest);

		if (!deleted) {
			response.put("success", false);
			response.put("message", "Regulatory Link record not found");
			return ResponseEntity.status(404).body(response);
		}

		response.put("success", true);
		response.put("message", "Regulatory Link deleted Successfully");
		return ResponseEntity.ok(response);
	}

	// Update
	@PostMapping("/updateRegulatoryLink")
	public ResponseEntity<Map<String, String>> updateLink(@RequestBody LinkEntity link,
			HttpServletRequest httpRequest) {
		System.out.println("link : " + link);
		link.setLinkId(link.getLinkId()); // ensure correct id
		link.setName(link.getName());
		link.setStatus(link.getStatus());
		regulatoryLinkService.updateLink(link, httpRequest);

		Map<String, String> resp = new HashMap<>();
		resp.put("message", "Link updated successfully");
		return ResponseEntity.ok(resp);
	}

}
