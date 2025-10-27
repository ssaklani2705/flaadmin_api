package com.webelement.esgadminportal.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.entity.EventGallery;
import com.webelement.esgadminportal.service.EventGalleryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/eventGallery")
@CrossOrigin(origins = { "http://localhost:4200", "https://www.iba.org.in", "http://192.168.0.173:4200",
		"http://192.168.0.100:4200", "http://192.168.0.100:8080" })
public class EventGalleryController {

	private final EventGalleryService galleryService;

	public EventGalleryController(EventGalleryService galleryService) {
		this.galleryService = galleryService;
	}

	@PostMapping("/deleteGallery/{id}")
	public ResponseEntity<Map<String, String>> deleteGallery(@PathVariable Integer id) {
		Map<String, String> response = new HashMap<>();
		try {
			galleryService.deleteGalleryById(id);
			response.put("message", "Gallery deleted successfully");
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			response.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(response);
		} catch (Exception e) {
			response.put("message", "Error deleting gallery: " + e.getMessage());
			return ResponseEntity.status(500).body(response);
		}
	}

	@PostMapping("/approveEventGallery")
	public ResponseEntity<ResponseApi<String>> approveEventGallery(@RequestParam("eventGalleryId") int eventGalleryId,
			@RequestParam("userId") int userId, HttpServletRequest httpRequest) throws Exception {
		System.out.println("eventGalleryId : " + eventGalleryId + " userId : " + userId);
		return galleryService.approveEventWebinar(eventGalleryId, userId, httpRequest);
	}
	
	@PostMapping("/revertEventWebinar")
	public ResponseEntity<ResponseApi<String>> revertEventWebinar(@RequestParam("eventGalleryId") int eventGalleryId,
			@RequestParam("userId") int userId, HttpServletRequest httpRequest) throws Exception {
		System.out.println("eventGalleryId : " + eventGalleryId + " userId : " + userId);
		return galleryService.revertEventWebinar(eventGalleryId, userId, httpRequest);
	}

	@GetMapping("/getEventGalleryById/{id}")
	public ResponseEntity<Resource> getEventGalleryById(@PathVariable Integer id) throws Exception {
		System.out.println("-------------------");
		return galleryService.getEventGalleryById(id);
	}

	@GetMapping("/listByUser/{addedBy}/{eventId}")
	public ResponseEntity<List<EventGallery>> getGalleryByUser(@PathVariable Integer addedBy,
			@PathVariable Integer eventId) {
		List<EventGallery> galleries = galleryService.getGalleryByUser(addedBy, eventId);
		return ResponseEntity.ok(galleries);
	}

	@PostMapping("/addImgOrVideo")
	public ResponseEntity<Map<String, String>> addImgOrVideo(
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "videoUrl", required = false) String videoUrl,
			@RequestParam("caption") String caption, @RequestParam("userId") int userId,
			@RequestParam("eventId") int eventId, @RequestParam("type") int type,
			@RequestParam("username") String username) {
		Map<String, String> response = new HashMap<>();
		try {
			if (type == 1) { // Image
				if (file == null || file.isEmpty()) {
					response.put("message", "Image file is required");
					return ResponseEntity.badRequest().body(response);
				}
				galleryService.addImgOrVideo(file.getOriginalFilename(), caption, userId, file, type, eventId,
						username);

			} else if (type == 2) { // Video
				if (videoUrl == null || videoUrl.isEmpty()) {
					response.put("message", "Video URL is required");
					return ResponseEntity.badRequest().body(response);
				}
				galleryService.addImgOrVideo(videoUrl, caption, userId, null, type, eventId, username);

			} else {
				response.put("message", "Invalid type");
				return ResponseEntity.badRequest().body(response);
			}

			response.put("message", "Gallery saved successfully");
			return ResponseEntity.ok(response);

		} catch (IllegalArgumentException e) {
			response.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(response);

		} catch (Exception e) {
			response.put("message", "Error: " + e.getMessage());
			return ResponseEntity.status(500).body(response);
		}
	}
}