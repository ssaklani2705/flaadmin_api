package com.webelement.esgadminportal.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import com.webelement.esgadminportal.common.GalleryDTO;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.entity.BankEntity;
import com.webelement.esgadminportal.entity.GalleryEntity;
import com.webelement.esgadminportal.jwttoken.SecurityConfig;
import com.webelement.esgadminportal.repo.GalleryRepo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class GalleryService {

	private final SecurityConfig securityConfig;

	@Autowired
	GalleryRepo galleryRepo;
	@Autowired
	private CommonFunction commonFunction;

//    @Value("${gallery.upload.path}")
//    private String uploadDir;

	@Value("${file_gellery:}")
	private String uploadDir;

	GalleryService(SecurityConfig securityConfig) {
		this.securityConfig = securityConfig;
	}

	public void updateGallery(int galleryId, String sCaption, int iUserId, int iStatus,
			HttpServletRequest httpRequest) {
		LocalDateTime now = LocalDateTime.now();

		GalleryEntity galleryEntity = galleryRepo.findById(galleryId)
				.orElseThrow(() -> new IllegalArgumentException("Gallery not found with ID: " + galleryId));

		// update only allowed fields
		galleryEntity.setCaption(sCaption);
		galleryEntity.setStatus(iStatus);
		galleryEntity.setUserId(iUserId);
		galleryEntity.setModDate(now);

		galleryRepo.save(galleryEntity);

		// log history
		commonFunction.createHistoryAccess(iUserId, commonFunction.resolveClientIp(httpRequest),
				commonFunction.getLocalIp(), "Update Gallery", 3, galleryId, -1);
	}

	public ResponseEntity<ApiResponse<String>> addImgOrVideo(String fileNameOrVideoUrl, String sCaption, int iUserId,
			MultipartFile file, int iType, HttpServletRequest httpRequest) throws IOException {

		LocalDateTime now = LocalDateTime.now();
		String relativePath = null;

		// 1️⃣ Validate media type and file first
		if (iType == 1) { // Image
			if (file == null || file.isEmpty()) {
				return ResponseEntity.badRequest().body(new ApiResponse<>(false, "File is required for images.", null));
			}

			// Content type validation
			String contentType = file.getContentType();
			List<String> validTypes = Arrays.asList("image/jpeg", "image/png", "image/jpg");
			if (contentType == null || !validTypes.contains(contentType.toLowerCase())) {
				return ResponseEntity.badRequest()
						.body(new ApiResponse<>(false, "Only JPG, PNG, and JPEG images are allowed.", null));
			}

			// File size validation (5 MB max)
			long maxSize = 5 * 1024 * 1024;
			if (file.getSize() > maxSize) {
				return ResponseEntity.badRequest()
						.body(new ApiResponse<>(false, "File size exceeds 5 MB limit.", null));
			}
		} else if (iType == 2) { // Video URL
			if (fileNameOrVideoUrl == null || fileNameOrVideoUrl.isEmpty()) {
				return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Video URL is required.", null));
			}
		} else {
			return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Invalid media type.", null));
		}

		// 2️⃣ Create DB record
		GalleryEntity galleryEntity = new GalleryEntity();
		galleryEntity.setType(iType);
		galleryEntity.setCaption(sCaption);
		galleryEntity.setUserId(iUserId);
		galleryEntity.setStatus(1);
		galleryEntity.setModDate(now);
		galleryEntity.setRegDate(now);
		galleryEntity.setFilename(null);

		galleryEntity = galleryRepo.save(galleryEntity);
		Integer uid = galleryEntity.getGalleryId();

		// 3️⃣ Save file or set video URL
		if (iType == 1) {
			String extension = "";
			int dotIndex = fileNameOrVideoUrl.lastIndexOf('.');
			if (dotIndex >= 0)
				extension = fileNameOrVideoUrl.substring(dotIndex);

			String newFileName = "np-" + System.currentTimeMillis() + extension;
			String month = now.getMonth().toString().toLowerCase().substring(0, 3);
			String year = String.valueOf(now.getYear());
			String folderName = month + "-" + year;

			Path dirPath = Paths.get(uploadDir, folderName);
			Files.createDirectories(dirPath);

			Path fullPath = dirPath.resolve(newFileName);
			Files.write(fullPath, file.getBytes());

			relativePath = folderName + "/" + newFileName;
			galleryEntity.setFilename(relativePath);
			galleryRepo.save(galleryEntity);

		} else if (iType == 2) {
			relativePath = fileNameOrVideoUrl;
			galleryEntity.setFilename(relativePath);
			galleryRepo.save(galleryEntity);
		}

		// 4️⃣ Create history
		commonFunction.createHistoryAccess(iUserId, commonFunction.resolveClientIp(httpRequest),
				commonFunction.getLocalIp(), "Gallery Added", 3, uid, -1);

		return ResponseEntity.ok(new ApiResponse<>(true, "Media uploaded successfully.", relativePath));
	}

	private int determineMediaType(String fileName) {
		String lower = fileName.toLowerCase();
		if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")) {
			return 1; // Image
		} else if (lower.endsWith(".mp4") || lower.endsWith(".avi") || lower.endsWith(".mov") || lower.endsWith(".wmv")
				|| lower.endsWith(".mkv")) {
			return 2; // Video
		} else {
			return 0; // Unknown or unsupported type
		}
	}

	public boolean updateGalleryRecord(Integer iGalleryId, String sCaption, Integer iUserId, MultipartFile file,
			String videoUrl, Integer iType, Integer iStatus, HttpServletRequest httpRequest) throws IOException {
		Optional<GalleryEntity> optionalEntity = galleryRepo.findById(iGalleryId);

		if (!optionalEntity.isPresent()) {
			return false; // Record not found
		}

		GalleryEntity entity = optionalEntity.get();
		entity.setCaption(sCaption);
		entity.setUserId(iUserId);
		entity.setModDate(LocalDateTime.now());
		entity.setStatus(iStatus);
		;

		// ✅ Handle Image update
		if (iType == 1 && file != null && !file.isEmpty()) {
			// Delete old file if exists
			if (entity.getFilename() != null) {
				Path oldPath = Paths.get(uploadDir, entity.getFilename());
				Files.deleteIfExists(oldPath);
			}

			// Save new file
			String extension = "";
			int dotIndex = file.getOriginalFilename().lastIndexOf('.');
			if (dotIndex >= 0)
				extension = file.getOriginalFilename().substring(dotIndex);

			String newFileName = "np-" + System.currentTimeMillis() + extension;
			LocalDateTime now = LocalDateTime.now();
			String month = now.getMonth().toString().toLowerCase().substring(0, 3);
			String year = String.valueOf(now.getYear());
			String folderName = month + "-" + year;

			Path dirPath = Paths.get(uploadDir, folderName);
			Files.createDirectories(dirPath);

			Path fullPath = dirPath.resolve(newFileName);
			Files.write(fullPath, file.getBytes());

			entity.setFilename(folderName + "/" + newFileName);
		}

		// ✅ Handle Video update
		else if (iType == 2 && videoUrl != null && !videoUrl.trim().isEmpty()) {
			entity.setFilename(videoUrl.trim());
		}

		// Save updated entity
		galleryRepo.save(entity);

		// ✅ Add history
		commonFunction.createHistoryAccess(iUserId, commonFunction.resolveClientIp(httpRequest),
				commonFunction.getLocalIp(), "Gallery Updated", 3, iGalleryId, -1);

		return true;
	}

	public GalleryEntity getGalleryRecordById(Integer id) {
		return galleryRepo.findById(id).orElse(null);
	}

	public Page<GalleryDTO> getAllGalleries(int page, int size, int statusIndex, String search) {
		Pageable pageable = PageRequest.of(page, size);
		return galleryRepo.getAllGalleries(pageable, statusIndex, search);
	}

	public boolean deleteGalleryById(Integer iGalleryId, Integer iUserId) {
		Optional<GalleryEntity> optional = galleryRepo.findById(iGalleryId);

		if (!optional.isPresent()) {
			return false; // Record not found
		}

		GalleryEntity entity = optional.get();
		entity.setStatus(3);
		entity.setUserId(iUserId);
		entity.setModDate(LocalDateTime.now());

		galleryRepo.save(entity); // Save updated entity
		return true;
	}

	public ResponseEntity<Resource> getImageById(Integer id) throws IOException {
		Optional<GalleryEntity> entityOpt = galleryRepo.findById(id);
		System.out.println("entityOpt : " + entityOpt);

		if (!entityOpt.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		GalleryEntity entity = entityOpt.get();
		if (entity.getFilename() == null) {
			return ResponseEntity.notFound().build();
		}

		// relativePath: "aug-2025/np-1756461476656.png"
		String relativePath = entity.getFilename();

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

	public ResponseEntity<ResponseApi<String>> deleteGallery(Integer iGalleryId, Integer iUserId,
			HttpServletRequest httpRequest) {
		Optional<GalleryEntity> existingGallery = galleryRepo.findById(iGalleryId);
		if (!existingGallery.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseApi<>(false, "Gallery not found", null));
		}
		int updatedRows = galleryRepo.deleteGallery(3, iGalleryId);
		if (updatedRows > 0) {
			commonFunction.createHistoryAccess(iUserId, commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "Gallery Deleted", 3, iGalleryId, -1);
			return ResponseEntity.ok(new ResponseApi<>(true, "Gallery deleted successfully", null));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseApi<>(false, "Failed to delete Gallery", null));
		}
	}

}