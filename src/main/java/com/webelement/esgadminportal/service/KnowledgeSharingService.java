package com.webelement.esgadminportal.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.webelement.esgadminportal.common.CommonFunction;
import com.webelement.esgadminportal.common.KnowledgeDTO;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.entity.KnowledgeEntity;
import com.webelement.esgadminportal.entity.TransactionEntity;
import com.webelement.esgadminportal.repo.BankRepo;
import com.webelement.esgadminportal.repo.KnowledgeRepo;

@Service
public class KnowledgeSharingService {

	@Autowired
	KnowledgeRepo knowledgeRepo;
	@Autowired
	BankRepo bankRepo;

	@Autowired
	private CommonFunction commonFunction;

	@Value("${file_knowledge}")
	private String uploadDir;

	public Page<KnowledgeDTO> getKnowledgeSharingDetails(int page, int size, int statusIndex, String search) {
		Pageable pageable = PageRequest.of(page, size);
		return knowledgeRepo.findKnowledgeDetails(pageable, statusIndex, search);
	}

	public ResponseEntity<Map<String, String>> addKnowledgeSharing(String source, String title, MultipartFile file,
			int iUserId, String sUsername, HttpServletRequest httpRequest) {
		Map<String, String> response = new HashMap<>();

		try {

			KnowledgeEntity knowledge = new KnowledgeEntity();
			knowledge.setName(title);

			// File handling - allow any file type
			String relativePath = null;

			if (file == null || file.isEmpty()) {
				throw new IllegalArgumentException("File is required.");
			}

			String originalName = file.getOriginalFilename();
			String extension = "";

			int dotIndex = originalName.lastIndexOf('.');
			if (dotIndex >= 0) {
				extension = originalName.substring(dotIndex);
			}

			// Generate the file path based on the current date
			LocalDateTime now = LocalDateTime.now();
			String month = now.getMonth().toString().toLowerCase().substring(0, 3);
			String year = String.valueOf(now.getYear());
			String folderName = month + "-" + year;

			String newFileName = "np-" + System.currentTimeMillis() + extension;

			// Define the directory to store the file
			Path dirPath = Paths.get(uploadDir, folderName);
			Files.createDirectories(dirPath);

			// Save the file to the disk
			Path fullPath = dirPath.resolve(newFileName);
			Files.write(fullPath, file.getBytes());

			relativePath = folderName + "/" + newFileName;

			knowledge.setFilename(relativePath);

			// Metadata
			knowledge.setBankId(-1);
			knowledge.setUserId(iUserId);
			knowledge.setBankUserId(-1); // Replace with actual bank user ID if needed
			knowledge.setUsername(sUsername);

			knowledge.setBankApproval(1);
			knowledge.setApproval(1);
			knowledge.setStatus((short) 1);

			knowledge.setRegDate(LocalDateTime.now());
			knowledge.setModDate(LocalDateTime.now());
			knowledge.setSource(source);
			knowledge.setAddedBy(-1);

			// Save to the database
			KnowledgeEntity knowledgeEntity = knowledgeRepo.save(knowledge);
			// Create history
			commonFunction.createHistoryAccess(knowledge.getUserId(), commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "Knowledge Sharing Added", 6, knowledgeEntity.getKnowledge(), -1);
			response.put("message", "Knowledge Sharing added successfully");
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			response.put("message", "Error: " + e.getMessage());
			return ResponseEntity.status(500).body(response);
		}
	}

	public KnowledgeDTO getKnowledgeSharing(int knowledgeId) {
		KnowledgeDTO knowledge = knowledgeRepo.getKnowledgeById(knowledgeId);

		if (knowledge != null) {
			// Append transaction history
			List<TransactionEntity> history = getTransactionLogs(6, knowledgeId);
			// System.out.println("history : " + history);
			knowledge.setTransactionhistory(history);
		}

		return knowledge;
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

	public ResponseEntity<Resource> getKnowledgeSharingDocById(Integer id) throws IOException {
		Optional<KnowledgeEntity> entityOpt = knowledgeRepo.findById(id);
		System.out.println("entityOpt : " + entityOpt);

		if (!entityOpt.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		KnowledgeEntity entity = entityOpt.get();
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

	public ResponseEntity<ResponseApi<String>> approveKnowledge(int knowledgeId, int userId,
			HttpServletRequest httpRequest) {
		Optional<KnowledgeEntity> existingKnowledge = knowledgeRepo.findById(knowledgeId);

		if (!existingKnowledge.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseApi<>(false, "Knowledge Sharing not found", null));
		}

		int updatedRows = knowledgeRepo.approveKnowledge(2, knowledgeId);

		if (updatedRows > 0) {
			commonFunction.createHistoryAccess(userId, commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "Knowledge Sharing Approved", 6, knowledgeId, -1);
			return ResponseEntity.ok(new ResponseApi<>(true, "Knowledge Sharing Approved successfully", null));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseApi<>(false, "Failed to Approved Knowledge Sharing", null));
		}
	}
	
	
	public ResponseEntity<ResponseApi<String>> revertKnowledge(int knowledgeId, int userId,
			HttpServletRequest httpRequest) {
		Optional<KnowledgeEntity> existingKnowledge = knowledgeRepo.findById(knowledgeId);

		if (!existingKnowledge.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseApi<>(false, "Knowledge Sharing not found", null));
		}

		int updatedRows = knowledgeRepo.approveKnowledge(1, knowledgeId);

		if (updatedRows > 0) {
			commonFunction.createHistoryAccess(userId, commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "Knowledge Sharing reverted to Pending", 6, knowledgeId, -1);
			return ResponseEntity.ok(new ResponseApi<>(true, "Knowledge Sharing reverted to Pending successfully", null));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseApi<>(false, "Failed to reverted Knowledge Sharing", null));
		}
	}


	public ResponseEntity<ResponseApi<String>> deleteKnowledge(int knowledgeId, int userId,
			HttpServletRequest httpRequest) {
		Optional<KnowledgeEntity> existingKnowledge = knowledgeRepo.findById(knowledgeId);

		if (!existingKnowledge.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseApi<>(false, "Knowledge Sharing not found", null));
		}

		int updatedRows = knowledgeRepo.deleteKnowledge((short) 3, knowledgeId);

		if (updatedRows > 0) {
			commonFunction.createHistoryAccess(userId, commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "Knowledge Sharing Deleted", 6, knowledgeId, -1);
			return ResponseEntity.ok(new ResponseApi<>(true, "Knowledge Sharing Approved successfully", null));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseApi<>(false, "Failed to Delete Knowledge Sharing", null));
		}
	}

	public ResponseEntity<Map<String, String>> updateKnowledgeSharing(int knowledgeId, String source, String title,
			MultipartFile file, int iUserId, String sUsername, int status, HttpServletRequest httpRequest) {
		Map<String, String> response = new HashMap<>();

		try {

			Optional<KnowledgeEntity> optionalKnowledge = knowledgeRepo.findById(knowledgeId);
			if (!optionalKnowledge.isPresent()) {
				response.put("message", "Knowledge Sharing not found");
				return ResponseEntity.status(404).body(response);
			}

			KnowledgeEntity knowledge = optionalKnowledge.get();
			knowledge.setName(title);
			knowledge.setSource(source);
			knowledge.setModDate(LocalDateTime.now());
			knowledge.setUserId(iUserId);
			knowledge.setStatus((short) status);

			// Handle file if provided
			if (file != null && !file.isEmpty()) {
				// Delete old file if exists
				String oldFile = knowledge.getFilename();
				if (oldFile != null) {
					Path oldFilePath = Paths.get(uploadDir, oldFile);
					Files.deleteIfExists(oldFilePath);
				}

				// Save new file
				String originalName = file.getOriginalFilename();
				String extension = "";
				int dotIndex = originalName.lastIndexOf('.');
				if (dotIndex >= 0) {
					extension = originalName.substring(dotIndex);
				}

				LocalDateTime now = LocalDateTime.now();
				String month = now.getMonth().toString().toLowerCase().substring(0, 3);
				String year = String.valueOf(now.getYear());
				String folderName = month + "-" + year;

				String newFileName = "np-" + System.currentTimeMillis() + extension;
				Path dirPath = Paths.get(uploadDir, folderName);
				Files.createDirectories(dirPath);

				Path fullPath = dirPath.resolve(newFileName);
				Files.write(fullPath, file.getBytes());

				String relativePath = folderName + "/" + newFileName;
				knowledge.setFilename(relativePath);

			}

			knowledgeRepo.save(knowledge);

			// Create history
			commonFunction.createHistoryAccess(iUserId, commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "Knowledge Sharing Updated", 6, knowledge.getKnowledge(), -1);

			response.put("message", "Knowledge Sharing updated successfully");
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			response.put("message", "Error: " + e.getMessage());
			return ResponseEntity.status(500).body(response);
		}
	}

}
