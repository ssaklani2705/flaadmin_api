package com.webelement.esgadminportal.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.webelement.esgadminportal.common.ApiResponse;
import com.webelement.esgadminportal.common.CommonFunction;
import com.webelement.esgadminportal.common.EventAndWebinarDTO;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.entity.EventEntity;
import com.webelement.esgadminportal.entity.TransactionEntity;
import com.webelement.esgadminportal.repo.BankRepo;
import com.webelement.esgadminportal.repo.EventRepo;

@Service
public class EventWebinarService {

	@Autowired
	EventRepo eventRepo;

	@Value("${file_event}")
	private String uploadDir;

	@Autowired
	private CommonFunction commonFunction;

	@Autowired
	private BankRepo bankRepo;

	public Page<EventAndWebinarDTO> getEventAndWebinar(int page, int size, int statusIndex, String search) {
		Pageable pageable = PageRequest.of(page, size);
		return eventRepo.findEvents(pageable, statusIndex, search);
	}

	public ResponseEntity<ApiResponse<EventEntity>> addEventWebinar(String title, LocalDateTime dateTime, String time,
			String type, String location, String link, String shortDesc, String description, MultipartFile file,
			int iUserId, String sUsername, String durationHours, String durationMinutes,
			HttpServletRequest httpRequest) {
		try {

			// Step 1: Validate content type
			String contentType = file.getContentType();
			System.out.println("contentType: " + contentType);

			List<String> validTypes = Arrays.asList("image/jpeg", "image/png", "image/jpg");

			if (contentType == null || !validTypes.contains(contentType.toLowerCase())) {
				return ResponseEntity.badRequest()
						.body(new ApiResponse<>(false, "Only JPG,PNG and JPEG images are allowed.", null));
			}

			// Step 2: Validate file size (5 MB = 5 * 1024 * 1024 bytes)
			if (file.getSize() > 5 * 1024 * 1024) {
//            if (file.getSize() > 500 * 1024) {

				return ResponseEntity.badRequest()
						.body(new ApiResponse<>(false, "File size must be less than 5 MB.", null));
			}

			int iType = "event".equalsIgnoreCase(type) ? 1 : 2;
			int existingCount = eventRepo.countByTitleActive(title.trim());
			if (existingCount > 0) {
				return ResponseEntity.badRequest()
						.body(new ApiResponse<>(false, "Event with this title already exists.", null));
			}

			EventEntity event = new EventEntity();
			event.setsTitle(title.trim());
			event.setdDate(dateTime);
			event.setTime(time);
			event.setiType(iType);
			event.setsLocation(location);
			event.setsLink(link);
			event.setsShortDesc(shortDesc);
			event.setDurationHours(durationHours);
			event.setDurationMinutes(durationMinutes);
//	        event.setsDescription(description);

			LocalDateTime now = LocalDateTime.now();
			String month = now.getMonth().toString().toLowerCase().substring(0, 3); // e.g., sep
			String year = String.valueOf(now.getYear());
			String monthYearFolder = month + "-" + year;

			// === Step 1: Save description as HTML file ===
			String descFolderPath = Paths.get(uploadDir, "Description", monthYearFolder).toString();
			Files.createDirectories(Paths.get(descFolderPath));

			String htmlFileName = "desc-" + System.currentTimeMillis() + ".html";
			Path htmlFilePath = Paths.get(descFolderPath, htmlFileName);
			Files.write(htmlFilePath, description.getBytes(StandardCharsets.UTF_8));

			// Store relative HTML path
			String relativeHtmlPath = "Description/" + monthYearFolder + "/" + htmlFileName;
			event.setsDescription(relativeHtmlPath);

			// --- File handling ---
			String relativePath = null;
			if (iType == 1 || iType == 2) { // Event requires image
				if (file == null || file.isEmpty()) {
					return ResponseEntity.badRequest()
							.body(new ApiResponse<>(false, "Image file is required for events.", null));
				}

				String originalName = file.getOriginalFilename();
				String extension = "";
				int dotIndex = originalName.lastIndexOf('.');
				if (dotIndex >= 0)
					extension = originalName.substring(dotIndex);

				if (determineMediaType(extension) != 1) {
					return ResponseEntity.badRequest()
							.body(new ApiResponse<>(false, "Only image files are allowed for events.", null));
				}

//	            LocalDateTime now = LocalDateTime.now();
				String folderName = now.getMonth().toString().toLowerCase().substring(0, 3) + "-" + now.getYear();
				String newFileName = "np-" + System.currentTimeMillis() + extension;

				Path dirPath = Paths.get(uploadDir, folderName);
				Files.createDirectories(dirPath);
				Path fullPath = dirPath.resolve(newFileName);
				Files.write(fullPath, file.getBytes());

				relativePath = folderName + "/" + newFileName;
			}
//	        else if (iType == 2) { // Webinar requires link
//	            if (link == null || link.trim().isEmpty()) {
//	                return ResponseEntity
//	                        .badRequest()
//	                        .body(new ApiResponse<>(false, "Webinar link is required.", null));
//	            }
//	            relativePath = link;
//	        }

			event.setsFilename(relativePath);

			// --- Metadata ---
			event.setiBankId(-1);
			event.setiAddedBy(iUserId);
			event.setiUserId(iUserId);
			event.setiBankUserId(-1);
			event.setsUsername(sUsername);
			event.setiBankApproval(1);
			event.setiApproval(1);
			event.setStatus((short) 1);
			event.setTsRegDate(LocalDateTime.now());
			event.setTsModDate(LocalDateTime.now());

			EventEntity savedEvent = eventRepo.save(event);

			commonFunction.createHistoryAccess(iUserId, commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "Events & Webinar Added", 5, savedEvent.getiEventId(), -1);

			return ResponseEntity.ok(new ApiResponse<>(true, "Event/Webinar added successfully", null));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse<>(false, "Error: " + e.getMessage(), null));
		}
	}

	private int determineMediaType(String extension) {
		List<String> imageExt = Arrays.asList(".jpg", ".jpeg", ".png");
		if (imageExt.contains(extension.toLowerCase()))
			return 1;
		return 0; // Not supported
	}

	public ResponseEntity<ApiResponse<EventEntity>> updateEventWebinar(int eventId, String title,
			LocalDateTime dateTime, String time, String type, String location, String link, String shortDesc,
			String description, MultipartFile file, int iUserId, String sUsername, String durationHours,
			String durationMinutes, int status, HttpServletRequest httpRequest) {
		try {
			Optional<EventEntity> optionalEvent = eventRepo.findById(eventId);
			if (!optionalEvent.isPresent()) {
				return ResponseEntity.badRequest()
						.body(new ApiResponse<>(false, "Event/Webinar not found with ID: " + eventId, null));
			}

			int existingCount = eventRepo.countByTitleActiveAndIEventId(title.trim(), eventId);
			if (existingCount > 0) {
				return ResponseEntity.badRequest()
						.body(new ApiResponse<>(false, "Event with this title already exists.", null));
			}

			EventEntity event = optionalEvent.get();
			int iType = "event".equalsIgnoreCase(type) ? 1 : 2;

			// Update fields
			event.setsTitle(title.trim());
			event.setdDate(dateTime);
			event.setTime(time);
			event.setiType(iType);
			event.setsLocation(location);
			event.setsLink(link);
			event.setsShortDesc(shortDesc);
			event.setDurationHours(durationHours);
			event.setDurationMinutes(durationMinutes);

			// === Description HTML handling ===
			try {
				// Delete old description HTML if exists
				if (event.getsDescription() != null && !event.getsDescription().trim().isEmpty()) {
					Path oldHtmlPath = Paths.get(uploadDir, event.getsDescription());
					try {
						Files.deleteIfExists(oldHtmlPath);
					} catch (Exception ex) {
						System.err.println("Failed to delete old description file: " + ex.getMessage());
					}
				}

				// Create new HTML file
				LocalDateTime now = LocalDateTime.now();
				String month = now.getMonth().toString().toLowerCase().substring(0, 3); // e.g., sep
				String year = String.valueOf(now.getYear());
				String monthYearFolder = month + "-" + year;

				String descFolderPath = Paths.get(uploadDir, "Description", monthYearFolder).toString();
				Files.createDirectories(Paths.get(descFolderPath));

				String htmlFileName = "desc-" + System.currentTimeMillis() + ".html";
				Path htmlFilePath = Paths.get(descFolderPath, htmlFileName);
				Files.write(htmlFilePath, description.getBytes(StandardCharsets.UTF_8));

				// Store relative path in DB
				String relativeHtmlPath = "Description/" + monthYearFolder + "/" + htmlFileName;
				event.setsDescription(relativeHtmlPath);

			} catch (Exception ex) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(new ApiResponse<>(false, "Failed to update description file: " + ex.getMessage(), null));
			}

			// === File handling for event image / webinar link ===
//            if (iType == 1 && file != null && !file.isEmpty()) {
			if (file != null && !file.isEmpty()) {
				// Delete old image if exists
				if (event.getsFilename() != null && !event.getsFilename().trim().isEmpty()) {
					Path oldFilePath = Paths.get(uploadDir, event.getsFilename());
					try {
						Files.deleteIfExists(oldFilePath);
					} catch (Exception ex) {
						System.err.println("Failed to delete old image: " + ex.getMessage());
					}
				}

				String originalName = file.getOriginalFilename();
				String extension = "";
				int dotIndex = originalName.lastIndexOf('.');
				if (dotIndex >= 0)
					extension = originalName.substring(dotIndex);

				// Step 1: Validate content type
				String contentType = file.getContentType();
				System.out.println("contentType: " + contentType);

				List<String> validTypes = Arrays.asList("image/jpeg", "image/png", "image/jpg");

				if (contentType == null || !validTypes.contains(contentType.toLowerCase())) {
					return ResponseEntity.badRequest()
							.body(new ApiResponse<>(false, "Only JPG,PNG and JPEG images are allowed.", null));
				}

				// Step 2: Validate file size (5 MB = 5 * 1024 * 1024 bytes)
				if (file.getSize() > 5 * 1024 * 1024) {
//                if (file.getSize() > 500 * 1024) {

					return ResponseEntity.badRequest()
							.body(new ApiResponse<>(false, "File size must be less than 5 MB.", null));
				}

				if (determineMediaType(extension) != 1) {
					return ResponseEntity.badRequest()
							.body(new ApiResponse<>(false, "Only image files are allowed for events.", null));
				}

				LocalDateTime now = LocalDateTime.now();
				String folderName = now.getMonth().toString().toLowerCase().substring(0, 3) + "-" + now.getYear();
				String newFileName = "np-" + System.currentTimeMillis() + extension;

				Path dirPath = Paths.get(uploadDir, folderName);
				Files.createDirectories(dirPath);
				Path fullPath = dirPath.resolve(newFileName);
				Files.write(fullPath, file.getBytes());

				event.setsFilename(folderName + "/" + newFileName);

			}
//            else if (iType == 2) { // Webinar → link only
//                if (link == null || link.trim().isEmpty()) {
//                    return ResponseEntity.badRequest()
//                            .body(new ApiResponse<>(false, "Webinar link is required.", null));
//                }
//                event.setsFilename(link);
//            }

			// Metadata
			event.setiUserId(iUserId);
			event.setsUsername(sUsername);
			event.setTsModDate(LocalDateTime.now());
			event.setStatus((short) status);

			// Save updated event
			EventEntity updatedEvent = eventRepo.save(event);

			// Create history
			commonFunction.createHistoryAccess(iUserId, commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "Events & Webinar Updated", 5, updatedEvent.getiEventId(), -1);

			return ResponseEntity.ok(new ApiResponse<>(true, "Event/Webinar updated successfully", updatedEvent));

		} catch (Exception e) {
			return ResponseEntity.status(500).body(new ApiResponse<>(false, "Error: " + e.getMessage(), null));
		}
	}

	public ResponseEntity<ResponseApi<String>> deleteEventWebinar(int eventId, int userId,
			HttpServletRequest httpRequest) {
		Optional<EventEntity> existingEvent = eventRepo.findById(eventId);

		if (!existingEvent.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseApi<>(false, "Event/Webinar not found", null));
		}

		int updatedRows = eventRepo.deleteEvent((short) 3, eventId);

		if (updatedRows > 0) {
			commonFunction.createHistoryAccess(userId, commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "Events & Webinar Deleted", 5, eventId, -1);
			return ResponseEntity.ok(new ResponseApi<>(true, "Events & Webinar deleted successfully", null));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseApi<>(false, "Failed to delete Events & Webinar", null));
		}

	}

	public ResponseEntity<ResponseApi<String>> approveEventWebinar(int eventId, int userId,
			HttpServletRequest httpRequest) {
		Optional<EventEntity> existingEvent = eventRepo.findById(eventId);

		if (!existingEvent.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseApi<>(false, "Event/Webinar not found", null));
		}

		int updatedRows = eventRepo.approveEvent(2, eventId);

		if (updatedRows > 0) {
			commonFunction.createHistoryAccess(userId, commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "Events & Webinar Approved", 5, eventId, -1);
			return ResponseEntity.ok(new ResponseApi<>(true, "Events & Webinar Approved successfully", null));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseApi<>(false, "Failed to Approved Events & Webinar", null));
		}

	}
	
	
	
	public ResponseEntity<ResponseApi<String>> revertEventWebinar(int eventId, int userId,
	        HttpServletRequest httpRequest) {
	    Optional<EventEntity> existingEvent = eventRepo.findById(eventId);

	    if (!existingEvent.isPresent()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(new ResponseApi<>(false, "Event/Webinar not found", null));
	    }

	    int updatedRows = eventRepo.approveEvent(1, eventId); // 1 = Pending

	    if (updatedRows > 0) {
	        commonFunction.createHistoryAccess(
	                userId,
	                commonFunction.resolveClientIp(httpRequest),
	                commonFunction.getLocalIp(),
	                "Events & Webinar reverted to Pending", // ✅ updated message
	                5,
	                eventId,
	                -1
	        );
	        return ResponseEntity.ok(
	                new ResponseApi<>(true, "Events & Webinar reverted to Pending successfully", null) // ✅ updated message
	        );
	    } else {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ResponseApi<>(false, "Failed to revert Events & Webinar to Pending", null)); // ✅ updated message
	    }
	}

	

	public EventAndWebinarDTO getEventWebinar(int eventId) {
		EventAndWebinarDTO andWebinarDTO = eventRepo.getEventAndWebinarByEventId(eventId);

		if (andWebinarDTO != null) {
			// Load description HTML file content if path exists
			if (andWebinarDTO.getDescription() != null && !andWebinarDTO.getDescription().isEmpty()) {
				try {
					Path descPath = Paths.get(uploadDir, andWebinarDTO.getDescription());
					if (Files.exists(descPath)) {
//	                    String htmlContent = Files.readString(descPath, StandardCharsets.UTF_8);
						String htmlContent = new String(Files.readAllBytes(descPath), StandardCharsets.UTF_8);
						andWebinarDTO.setDescription(htmlContent); // Replace path with actual HTML
					}
				} catch (Exception e) {
					System.err.println("Error reading description file: " + e.getMessage());
					// Optional: keep the path if reading fails
				}
			}

			// Append transaction history
			List<TransactionEntity> history = getTransactionLogs(5, eventId);

			andWebinarDTO.setHistory(history);

//	        List<TransactionEntity> history;
//
//	        if (andWebinarDTO.getBankUserId() == -1) {
//	            history = getTransactionLogs(5, eventId);
//	        } else {
//	            history = getUserTransactionLogs(5, eventId);
//	        }

			andWebinarDTO.setHistory(history);

		}

		return andWebinarDTO;
	}

	public List<TransactionEntity> getTransactionLogs(int moduleId, Integer recordId) {
//		List<Object[]> results = bankRepo.getTransactionLogs(moduleId, recordId);
		List<Object[]> results = bankRepo.getTransactionLogsEventAndKnow(moduleId, recordId);
		System.out.println("results : " + results);

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

	public ResponseEntity<Resource> getEventImageById(Integer id) throws IOException {
		Optional<EventEntity> entityOpt = eventRepo.findById(id);
		System.out.println("entityOpt : " + entityOpt);

		if (!entityOpt.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		EventEntity entity = entityOpt.get();
		if (entity.getsFilename() == null) {
			return ResponseEntity.notFound().build();
		}

		// relativePath: "aug-2025/np-1756461476656.png"
		String relativePath = entity.getsFilename();

		// ✅ adjust this according to where files are actually stored
		// Example: if your uploads are saved in "<uploadDir>/uploads/aug-2025/..."
		Path filePathFull = Paths.get(uploadDir, relativePath);

		System.out.println("Looking for file at: " + filePathFull.toAbsolutePath());

		if (!Files.exists(filePathFull)) {
			return ResponseEntity.notFound().build();
		}

		Resource resource = new UrlResource(filePathFull.toUri());

		return ResponseEntity.ok()
				.contentType(Files.probeContentType(filePathFull) != null
						? MediaType.parseMediaType(Files.probeContentType(filePathFull))
						: MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"inline; filename=\"" + filePathFull.getFileName().toString() + "\"")
				.body(resource);
	}

}
