package com.webelement.esgadminportal.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.webelement.esgadminportal.common.CommonFunction;
import com.webelement.esgadminportal.common.GalleryDTO;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.entity.GalleryEntity;
import com.webelement.esgadminportal.entity.TransactionEntity;
import com.webelement.esgadminportal.repo.GalleryRepo;
import com.webelement.esgadminportal.service.GalleryService;

@RestController
@RequestMapping("/gallery")
@CrossOrigin(origins = { "http://localhost:4200", "http://192.168.0.196:4200", "http://192.168.0.100:4200",
		"https://www.iba.org.in" })
public class GalleryController {

	@Autowired
	GalleryService galleryService;

	@Autowired
	GalleryRepo galleryRepo;
	@Autowired
	CommonFunction commonFunction;

	@GetMapping("/image/{id}")
	public ResponseEntity<Resource> getImage(@PathVariable Integer id) throws IOException {
		System.out.println("-------------------");
		return galleryService.getImageById(id);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getGalleryById(@PathVariable Integer id, HttpServletRequest request) {
		return galleryRepo.findById(id).map(gallery -> {
			Map<String, Object> response = new HashMap<>();
			response.put("iGalleryId", gallery.getGalleryId());
			response.put("iType", gallery.getType());
			response.put("sCaption", gallery.getCaption());
			response.put("iUserId", gallery.getUserId());
			response.put("iStatus", gallery.getStatus());
			response.put("tsRegDate", gallery.getRegDate());
			response.put("tsModDate", gallery.getModDate());
			response.put("groupId", gallery.getGroupId());
			if (gallery.getType() == 1) { // Image
				response.put("imageUrl", gallery.getFilename());
			} else if (gallery.getType() == 2) { // Video
				response.put("videoUrl", gallery.getFilename());
			}
			List<TransactionEntity> history = commonFunction.getTransactionLogs(3, gallery.getGalleryId());
			System.out.println("history : " + history);
			response.put("history", history);

			return ResponseEntity.ok(response);
		}).orElseGet(() -> {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Gallery not found with ID " + id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		});
	}

	@PostMapping("/addImgOrVideo")
	public ResponseEntity<Map<String, String>> addImgOrVideo(
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "videoUrl", required = false) String videoUrl,
			@RequestParam("sCaption") String sCaption, @RequestParam("iUserId") int iUserId,
			@RequestParam("iType") int iType,@RequestParam("groupId") int groupId, HttpServletRequest httpRequest) {
		try {
			if (iType == 1) { // Image
				if (file == null || file.isEmpty()) {
					Map<String, String> response = new HashMap<>();
					response.put("message", "Image file is required");
					return ResponseEntity.badRequest().body(response);
				}
				String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
				galleryService.addImgOrVideo(fileName, sCaption, iUserId, file, iType,groupId, httpRequest);
			} else if (iType == 2) { // Video
				if (videoUrl == null || videoUrl.isEmpty()) {
					Map<String, String> response = new HashMap<>();
					response.put("message", "Video URL is required");
					return ResponseEntity.badRequest().body(response);
				}
				// For video, we do not have file, so pass null for MultipartFile
				galleryService.addImgOrVideo(videoUrl, sCaption, iUserId, null, iType,groupId, httpRequest);
			} else {
				Map<String, String> response = new HashMap<>();
				response.put("message", "Invalid type");
				return ResponseEntity.badRequest().body(response);
			}

			Map<String, String> response = new HashMap<>();
			response.put("message", "Gallery Details Saved successfully");
			return ResponseEntity.ok(response);

		} catch (IllegalArgumentException e) {
			Map<String, String> response = new HashMap<>();
			response.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(response);
		} catch (Exception e) {
			Map<String, String> response = new HashMap<>();
			response.put("message", "Error: " + e.getMessage());
			return ResponseEntity.status(500).body(response);
		}
	}

	@PostMapping("/updateGallery")
	public ResponseEntity<Map<String, String>> updateGalleryRecord(@RequestParam("iGalleryId") Integer iGalleryId,
			@RequestParam("sCaption") String sCaption, @RequestParam("iUserId") Integer iUserId,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "videoUrl", required = false) String videoUrl, @RequestParam("iType") Integer iType,
			@RequestParam("iStatus") Integer iStatus,@RequestParam("groupId") int groupId, HttpServletRequest httpRequest) {
		try {
			System.out.println("iType : " + iType + " File : " + file);
			boolean updated = galleryService.updateGalleryRecord(iGalleryId, sCaption, iUserId, file, videoUrl, iType,
					iStatus,groupId, httpRequest);

			if (!updated) {
				Map<String, String> response = new HashMap<>();
				response.put("message", "Gallery record not found");
				return ResponseEntity.status(404).body(response);
			}

			Map<String, String> response = new HashMap<>();
			response.put("message", "Gallery updated successfully");
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			Map<String, String> response = new HashMap<>();
			response.put("message", "Error: " + e.getMessage());
			return ResponseEntity.status(500).body(response);
		}
	}

	// GET BY ID
	@GetMapping("/getGalleryRecordById")
	public ResponseEntity<?> getGalleryRecordById(@RequestParam("iGalleryId") Integer iGalleryId) {

		if (iGalleryId == null) {
			Map<String, String> response = new HashMap<>();
			response.put("message", "iGalleryId is required");
			return ResponseEntity.badRequest().body(response);
		}

		GalleryEntity entity = galleryService.getGalleryRecordById(iGalleryId);
		if (entity == null) {
			Map<String, String> response = new HashMap<>();
			response.put("message", "Record not found");
			return ResponseEntity.status(404).body(response);

		}
		return ResponseEntity.ok(entity);
	}

	@GetMapping("/getAllGalleries")
	public Map<String, Object> getAllGalleries(@RequestParam int page, @RequestParam int size,
			@RequestParam int statusIndex, @RequestParam String search) {
		Page<GalleryDTO> galleryPage = galleryService.getAllGalleries(page, size, statusIndex, search);
		Map<String, Object> response = new HashMap<>();
		response.put("data", galleryPage.getContent());
		response.put("totalElements", galleryPage.getTotalElements());
		return response;
	}

	@DeleteMapping("/deleteById")
	public ResponseEntity<Map<String, String>> deleteGalleryById(@RequestParam("iGalleryId") Integer iGalleryId,
			@RequestParam("iUserId") Integer iUserId) {
		if (iGalleryId == null) {
			Map<String, String> response = new HashMap<>();
			response.put("message", "GalleryId is required");
			return ResponseEntity.badRequest().body(response);
		}

		boolean deleted = galleryService.deleteGalleryById(iGalleryId, iUserId);
		Map<String, String> response = new HashMap<>();

		if (!deleted) {
			response.put("message", "Gallery record not found");
			return ResponseEntity.status(404).body(response);
		}

		response.put("message", "Record deleted Successfully");
		return ResponseEntity.ok(response);
	}

	@PostMapping("/deleteGallery")
	public ResponseEntity<ResponseApi<String>> deleteGallery(@RequestBody GalleryEntity galleryEntity,
			HttpServletRequest httpRequest) throws Exception {
		System.out.println("galleryEntity : " + galleryEntity);
		return galleryService.deleteGallery(galleryEntity.getGalleryId(), galleryEntity.getUserId(), httpRequest);
	}

}