package com.webelement.esgadminportal.controller;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.webelement.esgadminportal.common.ApiResponse;
import com.webelement.esgadminportal.common.DataDetailDTO;
import com.webelement.esgadminportal.common.DataInfoDTO;
import com.webelement.esgadminportal.entity.DataEntity;
import com.webelement.esgadminportal.entity.TransactionEntity;
import com.webelement.esgadminportal.repo.DataRepo;
import com.webelement.esgadminportal.repo.UserTransactionRepo;
import com.webelement.esgadminportal.service.DataService;
import lombok.RequiredArgsConstructor;
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/data")
@CrossOrigin(origins = "${app.cors.allowed-origins}")

public class DataController {
	private final DataService dataService;
	private final DataRepo dataRepo;
	private final UserTransactionRepo transactionRepo;

	@PostMapping(value = "/uploadDataExcel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, Object>> uploadDataExcel(@RequestParam("file") MultipartFile file,
			@RequestParam(value = "addedBy", required = false) Integer addedBy,
			@RequestParam(value = "bankId", required = false) Integer bankId, HttpServletRequest httpRequest) {

		try {
			return ResponseEntity.ok(dataService.uploadDataExcel(file, addedBy, bankId, httpRequest));

		} catch (IllegalArgumentException e) {
			System.out.println("Excel upload rejected: {}" + e.getMessage());
			Map<String, Object> body = new LinkedHashMap<>();
			body.put("success", false);
			body.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(body);

		} catch (Exception e) {
			System.out.println("Excel upload failed" + e);

			Map<String, Object> body = new LinkedHashMap<>();
			body.put("success", false);
			body.put("message", "Upload failed. Please try again.");
			return ResponseEntity.status(500).body(body);
		}
	}


	@GetMapping("/list")
	public Map<String, Object> findDataDetails(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam int statusIndex,
			@RequestParam(defaultValue = "") String search, @RequestParam int userId) {
		Page<DataInfoDTO> dataPage = dataService.findDataDetails(page, size, statusIndex, search, userId);

		Map<String, Object> response = new HashMap<>();
		response.put("data", dataPage.getContent());
		response.put("totalElements", dataPage.getTotalElements());
		return response;
	}

	@PostMapping("/approve/{dataId}")
	public Map<String, Object> approveData(@PathVariable Integer dataId,
			@RequestParam(value = "userId", required = false) Integer userId, HttpServletRequest httpRequest) {

		Map<String, Object> response = new HashMap<>();

		boolean approved = dataService.approveData(dataId, userId, httpRequest);

		response.put("data", approved);
		response.put("totalElements", approved ? 1 : 0);
		response.put("message", approved ? "Record approved successfully" : "Record not found or already processed");

		return response;
	}

	@PostMapping("/delete/{dataId}")
	public Map<String, Object> deleteData(@PathVariable Integer dataId,
			@RequestParam(value = "userId", required = false) Integer userId, HttpServletRequest httpRequest) {

		Map<String, Object> response = new HashMap<>();

		boolean deleted = dataService.softDeleteData(dataId, userId, httpRequest);

		response.put("data", deleted);
		response.put("totalElements", deleted ? 1 : 0);
		response.put("message", deleted ? "Record deleted successfully" : "Record not found or cannot be deleted");

		return response;
	}

	@GetMapping("/admin-data-index")
	public Map<String, Object> adminDataList(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "0") int statusIndex,
			@RequestParam(defaultValue = "") String search, @RequestParam(defaultValue = "0") int bankId) {
		Page<DataInfoDTO> dataPage = dataService.adminDataListService(page, size, statusIndex, search, bankId);

		Map<String, Object> response = new HashMap<>();
		response.put("data", dataPage.getContent());
		response.put("totalElements", dataPage.getTotalElements());
		return response;
	}

//	@PostMapping("/save-or-update")
//	public ApiResponse<DataEntity> saveOrUpdateData(@Valid @RequestBody DataEntity request,
//			HttpServletRequest httpRequest) {
//		ApiResponse<DataEntity> response = dataService.saveOrUpdateData(request, httpRequest);
//		return response;
//	}

	@PostMapping("/save-or-update")
	public ResponseEntity<Map<String, Object>> saveDataAdmin(@RequestBody DataEntity request,
			HttpServletRequest httpRequest) {

		Map<String, Object> response = new HashMap<>();

		try {

			DataEntity savedData = dataService.saveDataAdmin(request, httpRequest);

			response.put("success", true);
			response.put("message", "Data saved successfully");
			response.put("id", savedData.getId());
			response.put("status", savedData.getStatus());

			return ResponseEntity.ok(response);

		} catch (IllegalArgumentException e) {

			response.put("success", false);
			response.put("message", e.getMessage());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

		} catch (Exception e) {

			e.printStackTrace();

			response.put("success", false);
			response.put("message", "Failed to save data");

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/getDataById/{id}")
	public Map<String, Object> getDataById(@PathVariable Integer id) {
		Map<String, Object> response = new HashMap<>();
		DataDetailDTO detail = dataService.findDataById(id);

		if (detail == null) {
			response.put("success", false);
			response.put("message", "Record not found");
			return response;
		}

		// Append transaction history
		List<TransactionEntity> history = getTransactionLogs(2, id);
//        detail.setHistory(history);
		response.put("success", true);
		response.put("data", detail);
		response.put("history", history); // TODO: fill from an action-history table
		return response;
	}

	public List<TransactionEntity> getTransactionLogs(int moduleId, Integer recordId) {
		List<Object[]> results = transactionRepo.getTransactionLogs2(moduleId, recordId);
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
}
