package com.webelement.esgadminportal.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.webelement.esgadminportal.common.ApiResponse;
import com.webelement.esgadminportal.common.EventAndWebinarDTO;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.entity.EventEntity;
import com.webelement.esgadminportal.service.EventWebinarService;

@RestController
@RequestMapping("/eventWebinar")
@CrossOrigin(origins = { "http://localhost:4200", "http://192.168.0.196:4200", "https://www.iba.org.in" })
public class EventWebinarController {

	@Autowired
	EventWebinarService eventWebinarService;

	@GetMapping("/getEventAndWebinar")
	public Map<String, Object> getEventAndWebinar(@RequestParam int page, @RequestParam int size,
			@RequestParam int statusIndex, @RequestParam String search) {
		Page<EventAndWebinarDTO> eventPage = eventWebinarService.getEventAndWebinar(page, size, statusIndex, search);
		Map<String, Object> response = new HashMap<>();
		response.put("data", eventPage.getContent());
		response.put("totalElements", eventPage.getTotalElements());
		return response;
	}

	@PostMapping("/addEventWebinar")
	public ResponseEntity<ApiResponse<EventEntity>> addEventWebinar(@RequestParam("title") String title,
			@RequestParam("dateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
			@RequestParam("time") String time, @RequestParam("type") String type,
			@RequestParam(value = "location", required = false) String location,
			@RequestParam(value = "link", required = false) String link,
			@RequestParam("shortDescription") String shortDesc, @RequestParam("description") String description,
			@RequestParam("file") MultipartFile file, @RequestParam("iUserId") int iUserId,
			@RequestParam("sUsername") String sUsername, @RequestParam("durationHours") String durationHours,
			@RequestParam("durationMinutes") String durationMinutes, HttpServletRequest httpRequest) {
		return eventWebinarService.addEventWebinar(title, dateTime, time, type, location, link, shortDesc, description,
				file, iUserId, sUsername, durationHours, durationMinutes, httpRequest);
	}

	@PostMapping("/updateEventWebinar")
	public ResponseEntity<ApiResponse<EventEntity>> updateEventWebinar(@RequestParam("eventId") int eventId,
			@RequestParam("title") String title,
			@RequestParam("dateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
			@RequestParam("time") String time, @RequestParam("type") String type,
			@RequestParam(value = "location", required = false) String location,
			@RequestParam(value = "link", required = false) String link,
			@RequestParam("shortDescription") String shortDesc, @RequestParam("description") String description,
			@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("iUserId") int iUserId,
			@RequestParam("sUsername") String sUsername, @RequestParam("durationHours") String durationHours,
			@RequestParam("durationMinutes") String durationMinutes, @RequestParam("status") int status,

			HttpServletRequest httpRequest) {
		return eventWebinarService.updateEventWebinar(eventId, title, dateTime, time, type, location, link, shortDesc,
				description, file, iUserId, sUsername, durationHours, durationMinutes, status, httpRequest);
	}

	@PostMapping("/deleteEventWebinar")
	public ResponseEntity<ResponseApi<String>> deleteEventWebinar(@RequestParam("eventId") int eventId,
			@RequestParam("userId") int userId, HttpServletRequest httpRequest) throws Exception {
		System.out.println("eventId : " + eventId + " userId : " + userId);
		return eventWebinarService.deleteEventWebinar(eventId, userId, httpRequest);
	}

	@PostMapping("/approveEventWebinar")
	public ResponseEntity<ResponseApi<String>> approveEventWebinar(@RequestParam("eventId") int eventId,
			@RequestParam("userId") int userId, HttpServletRequest httpRequest) throws Exception {
		System.out.println("eventId : " + eventId + " userId : " + userId);
		return eventWebinarService.approveEventWebinar(eventId, userId, httpRequest);
	}
	
	@PostMapping("/revertEventWebinar")
	public ResponseEntity<ResponseApi<String>> revertEventWebinar(@RequestParam("eventId") int eventId,
			@RequestParam("userId") int userId, HttpServletRequest httpRequest) throws Exception {
		System.out.println("eventId : " + eventId + " userId : " + userId);
		return eventWebinarService.revertEventWebinar(eventId, userId, httpRequest);
	}

	@GetMapping("/getEventWebinar/{eventId}")
	public ResponseEntity<?> getEventWebinar(@PathVariable int eventId) {
		EventAndWebinarDTO event = eventWebinarService.getEventWebinar(eventId);
		if (event != null) {
			return ResponseEntity.ok(event);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/getEventImageById/{id}")
	public ResponseEntity<Resource> getEventImageById(@PathVariable Integer id) throws IOException {
		System.out.println("-------------------");
		return eventWebinarService.getEventImageById(id);
	}

}
