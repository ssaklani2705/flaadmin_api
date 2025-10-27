package com.webelement.esgadminportal.service;

import com.webelement.esgadminportal.common.CommonFunction;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.entity.EventGallery;
import com.webelement.esgadminportal.repo.EventGalleryRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

@Service
public class EventGalleryService {

	@Autowired
	private EventGalleryRepo eventGalleryRepo;
	@Autowired
	private CommonFunction commonFunction;

	@Value("${file_event}")
	private String uploadDir;

	public void deleteGalleryById(Integer id) throws IOException {
		Optional<EventGallery> galleryOpt = eventGalleryRepo.findById(id);
		if (!galleryOpt.isPresent()) {
			throw new IllegalArgumentException("Gallery with ID " + id + " not found");
		}

		EventGallery gallery = galleryOpt.get();

		// 1️⃣ Delete file from disk if it exists and is an uploaded file (type == 1 for
		// images)
		if (gallery.getType() == 1 && gallery.getFilename() != null) {
			Path filePath = Paths.get(uploadDir, "EventGallery", gallery.getFilename());
			if (Files.exists(filePath)) {
				Files.delete(filePath); // delete the file
			}
		}

		// 2️⃣ Delete the database record
		eventGalleryRepo.deleteById(id);
	}

	public EventGallery addImgOrVideo(String fileNameOrVideoUrl, String caption, int userId, MultipartFile file,
			int type, int eventId, String username) throws IOException {

		LocalDateTime now = LocalDateTime.now();
		String relativePath;

		// 1️⃣ Create DB record first
		EventGallery gallery = new EventGallery();
		gallery.setType((short) type);
		gallery.setCaption(caption);
		gallery.setUserId(userId);
		gallery.setRegDate(now);
		gallery.setModDate(now);
		gallery.setFilename(null);
		gallery.setUsername(username);
		gallery.setEventId(eventId);
		gallery.setAddedBy(userId);
		gallery.setBankUserId(-1);
		gallery.setApproval((short) 1);
		gallery.setBankApproval((short) 1);
//					gallery.set

		gallery = eventGalleryRepo.save(gallery);

		// 2️⃣ Handle image
		if (type == 1) {
			String extension = fileNameOrVideoUrl.substring(fileNameOrVideoUrl.lastIndexOf("."));
			if (!isImageExtension(extension)) {
				throw new IllegalArgumentException("Only image files are allowed.");
			}
			// String descFolderPath = Paths.get(uploadDir, "Description",
			// monthYearFolder).toString();
			String newFileName = "np-" + System.currentTimeMillis() + extension;
			Path dirPath = Paths.get(uploadDir, "EventGallery");
			Files.createDirectories(dirPath);

			Path fullPath = dirPath.resolve(newFileName);
			Files.write(fullPath, file.getBytes());

			relativePath = newFileName;

		} else if (type == 2) { // Video URL
			relativePath = fileNameOrVideoUrl;
		} else {
			throw new IllegalArgumentException("Invalid media type");
		}

		// 3️⃣ Update DB record with file path / URL
		gallery.setFilename(relativePath);
		eventGalleryRepo.save(gallery);

		return gallery;
	}

	private boolean isImageExtension(String extension) {
		String ext = extension.toLowerCase();
		return ext.equals(".jpg") || ext.equals(".jpeg") || ext.equals(".png");
	}

	public List<EventGallery> getGalleryByUser(Integer addedBy, Integer eventId) {
		return eventGalleryRepo.findByEventId(eventId);
	}

	public ResponseEntity<Resource> getEventGalleryById(Integer id) throws Exception {
		Optional<EventGallery> entityOpt = eventGalleryRepo.findById(id);
		

		if (!entityOpt.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		EventGallery entity = entityOpt.get();
		if (entity.getFilename() == null) {
			return ResponseEntity.notFound().build();
		}

		// relativePath: "aug-2025/np-1756461476656.png"
		String relativePath = entity.getFilename();

		// ✅ adjust this according to where files are actually stored
		// Example: if your uploads are saved in "<uploadDir>/uploads/aug-2025/..."
		Path filePathFull = Paths.get(uploadDir, "EventGallery", relativePath);


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

	public ResponseEntity<ResponseApi<String>> approveEventWebinar(int eventGalleryId, int userId,
			HttpServletRequest httpRequest) {
		Optional<EventGallery> existingEvent = eventGalleryRepo.findById(eventGalleryId);

		if (!existingEvent.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseApi<>(false, "Event/Webinar Gallery not found", null));
		}

		int updatedRows = eventGalleryRepo.approveEventGallery((short) 2, eventGalleryId);

		if (updatedRows > 0) {
			commonFunction.createHistoryAccess(userId, commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "Events & Webinar Gallery  Approved", 5, eventGalleryId, -1);
			return ResponseEntity.ok(new ResponseApi<>(true, "Events & Webinar Gallery Approved successfully", null));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseApi<>(false, "Failed to Approved Events & Webinar Gallery", null));
		}
	}
	
	public ResponseEntity<ResponseApi<String>> revertEventWebinar(int eventGalleryId, int userId,
			HttpServletRequest httpRequest) {
		Optional<EventGallery> existingEvent = eventGalleryRepo.findById(eventGalleryId);

		if (!existingEvent.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseApi<>(false, "Event/Webinar Gallery not found", null));
		}

		int updatedRows = eventGalleryRepo.approveEventGallery((short) 1, eventGalleryId);

		if (updatedRows > 0) {
			commonFunction.createHistoryAccess(userId, commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "Events & Webinar Gallery  reverted to Pending", 5, eventGalleryId, -1);
			return ResponseEntity.ok(new ResponseApi<>(true, "Events & Webinar Gallery reverted to Pending successfully", null));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseApi<>(false, "Failed to revert Events & Webinar Gallery to Pending", null));
		}
	}
	
	
	
}