package com.webelement.esgadminportal.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.webelement.esgadminportal.common.CommonFunction;
import com.webelement.esgadminportal.common.ModulePermissionDTO;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.common.UserInfo;
import com.webelement.esgadminportal.entity.MailLogEntity;
import com.webelement.esgadminportal.entity.PermissionEntity;
import com.webelement.esgadminportal.entity.SmtpEntity;
import com.webelement.esgadminportal.entity.TransactionEntity;
import com.webelement.esgadminportal.entity.UserLoginEntity;
import com.webelement.esgadminportal.repo.MailLogRepo;
import com.webelement.esgadminportal.repo.PermissionRepo;
import com.webelement.esgadminportal.repo.SmtpRepo;
import com.webelement.esgadminportal.repo.UserLoginRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManagementService {

	
	private final UserLoginRepository loginRepository;
	private final CommonFunction commonFunction;
	private final PermissionRepo permissionRepo;
	private final SmtpRepo smtpRepo;
	private final MailService mailService;
	private final MailLogRepo mailLogRepo;

	@Value("${paths:}")
	private String paths;
	@Value("${file_maillog:}")
	private String file_maillog;

	public Page<UserInfo> findBasicUserInfo(int page, int size, int statusIndex, String search) {
		return loginRepository.findBasicUserInfo(PageRequest.of(page, size), statusIndex, search);
	}

	public ResponseEntity<ResponseApi<String>> deleteUser(int userId, int createdBy, HttpServletRequest httpRequest) {
		Optional<UserLoginEntity> existingUser = loginRepository.findById(userId);

		if (!existingUser.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseApi<>(false, "User not found", null));
		}

		int updatedRows = loginRepository.deleteUser(3, userId);

		if (updatedRows > 0) {
			commonFunction.createHistoryAccess(createdBy, commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "User Deleted", 9, userId, -1);
			return ResponseEntity.ok(new ResponseApi<>(true, "User deleted successfully", null));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseApi<>(false, "Failed to delete user", null));
		}
	}

	public UserLoginEntity getUserById(int userId) {
		UserLoginEntity user = loginRepository.getUserById(userId);

		if (user != null) {

			int newUserId = 0;

			if ("N".equalsIgnoreCase(user.getPermission())) {
				newUserId = userId;
			} else {
				newUserId = -1;
			}
			List<ModulePermissionDTO> permissions = getPermissionsByUserId(newUserId);
			user.setModule(permissions);

			// Append transaction history
			System.out.println("getTransactionLogs userId : " + userId);
			List<TransactionEntity> history = getTransactionLogs(9, userId);
			System.out.println("history : " + history);
			user.setTransactionhistory(history);
		} else {
			int newUserId = (userId == 0) ? -1 : userId;
			System.out.println("newUserId " + newUserId);

			List<ModulePermissionDTO> permissions = getPermissionsByUserId(newUserId);

			user = new UserLoginEntity(); // create new object
			user.setModule(permissions);
		}

		return user;
	}

	public List<TransactionEntity> getTransactionLogs(int moduleId, Integer recordId) {
		System.out.println(moduleId + "------" + recordId);
		List<Object[]> results = loginRepository.getTransactionLogs(moduleId, recordId);
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

	public List<ModulePermissionDTO> getPermissionsByUserId(int userId) {

		List<Object[]> results = loginRepository.getModulePermissions(userId);

		return results.stream().map(obj -> {
			ModulePermissionDTO dto = new ModulePermissionDTO();

			dto.setUserId(obj[0] != null ? ((Number) obj[0]).intValue() : null);
			dto.setModuleId(obj[1] != null ? ((Number) obj[1]).intValue() : null);
			dto.setName(obj[2] != null ? obj[2].toString() : null);
			dto.setType(obj[3] != null ? ((Number) obj[3]).intValue() : null);

			dto.setAddPer(obj[4] != null ? obj[4].toString() : null);
			dto.setEditPer(obj[5] != null ? obj[5].toString() : null);
			dto.setDeletePer(obj[6] != null ? obj[6].toString() : null);
			dto.setApprovePer(obj[7] != null ? obj[7].toString() : null);
			dto.setAdminApprovePer(obj[8] != null ? obj[8].toString() : null);
			dto.setViewPer(obj[9] != null ? obj[9].toString() : null);

			return dto;
		}).collect(Collectors.toList());
	}

	public ResponseEntity<?> saveUserDetail(UserLoginEntity userRequest, HttpServletRequest httpRequest)
			throws Exception {
		System.out.println("userRequest " + userRequest);
		boolean isEdit = userRequest.getUserId() > 0;

		if (!isEdit) {
			// ADD logic - check for duplicate email
			int isDuplicateEmail = loginRepository.checkDuplicacy(0, userRequest.getEmail());
			if (isDuplicateEmail == 1) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(new ResponseApi<>(false, "User already exists", "Exist"));
			}
		} else {
			// EDIT logic - check if email belongs to another user
			int isDuplicateEmail = loginRepository.checkDuplicacy(userRequest.getUserId(), userRequest.getEmail());
			if (isDuplicateEmail == 1) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(new ResponseApi<>(false, "Email already used by another user", "Exist"));
			}
		}

		int uid;
		String password = null;
		if (!isEdit) {
			// Add new user
			password = commonFunction.getRandomPassword(8);
			uid = createUser(userRequest, password, 0);

			// Optionally send welcome/reset email
			String ip = commonFunction.resolveClientIp(httpRequest);
			String subject = "IBAESG :: Login Credentials";
			int type = 1;
			int ccv = sendUserCreationEmail(userRequest, password, uid, ip, subject, type);
			commonFunction.createHistoryAccess(userRequest.getCreatedBy(), commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "Add User", 9, uid, -1);
		} else {
			// Update existing user
			uid = updateUser(userRequest);
			commonFunction.createHistoryAccess(userRequest.getCreatedBy(), commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "Update User", 9, uid, -1);
		}

		// Update permissions for both add & edit
		if (!"Y".equalsIgnoreCase(userRequest.getPermission()) && uid > 0) {
			permissionRepo.deleteByUserId(uid);
			if (userRequest.getModule() != null && !userRequest.getModule().isEmpty()) {
				for (ModulePermissionDTO m : userRequest.getModule()) {
					if ("Y".equalsIgnoreCase(m.getViewPer()) || "Y".equalsIgnoreCase(m.getAddPer())
							|| "Y".equalsIgnoreCase(m.getEditPer()) || "Y".equalsIgnoreCase(m.getDeletePer())
							|| "Y".equalsIgnoreCase(m.getApprovePer())
							|| "Y".equalsIgnoreCase(m.getAdminApprovePer())) {

						PermissionEntity entity = new PermissionEntity();
						entity.setUserId(uid);
						entity.setModuleId(m.getModuleId());
						entity.setAdd(defaultIfNull(m.getAddPer()));
						entity.setEdit(defaultIfNull(m.getEditPer()));
						entity.setDelete(defaultIfNull(m.getDeletePer()));
						entity.setApprove(defaultIfNull(m.getApprovePer()));
						entity.setAdminApprove(defaultIfNull(m.getAdminApprovePer()));
						entity.setView(defaultIfNull(m.getViewPer()));
						permissionRepo.save(entity);
					}
				}
			}
		}

		return ResponseEntity.ok(
				new ResponseApi<>(true, "Success", isEdit ? "User updated successfully" : "User added successfully"));
	}

	// Small helper to avoid null values
	private String defaultIfNull(String value) {
		return (value != null && !value.isEmpty()) ? value : "N";
	}

	// Create User
	public int createUser(UserLoginEntity info, String password, int loginuserId) {
		try {
			UserLoginEntity user = new UserLoginEntity();

			user.setFirstName(info.getFirstName());
			user.setMobileNo(info.getMobileNo());
			user.setEmail(info.getEmail());
			user.setPermission(info.getPermission());
			user.setPassword(commonFunction.cipher(password)); // keep your encryption logic
			user.setTelephone(info.getTelephone());
			user.setExpiryDate(info.getExpiryDate());
			user.setStatus((short) 1); // status is hardcoded as in JDBC
			String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			Timestamp timestamp = Timestamp.valueOf(dateStr);
			user.setRegDate(timestamp);
			user.setModDate(timestamp);
			user.setQcFlag((short) info.getQcFlag());
			user.setPcb(info.getPcb());

			UserLoginEntity savedUser = loginRepository.save(user);
			return savedUser.getUserId(); // JPA auto-fills the generated ID

		} catch (Exception e) {
			e.printStackTrace(); // or use a logger
			return 0;
		}
	}

	// Update User
	private int updateUser(UserLoginEntity request) {
		// Fetch existing user, update only necessary fields
		UserLoginEntity existing = loginRepository.findById(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));
		existing.setFirstName(request.getFirstName());
		existing.setMobileNo(request.getMobileNo());
		// existing.setEmail(request.getEmail());
		existing.setExpiryDate(request.getExpiryDate());
		existing.setStatus(request.getStatus());
		existing.setPermission(request.getPermission());
		existing.setTelephone(request.getTelephone());
		// existing.setQcFlag(request.getQcFlag());
		// existing.setPcb(request.getPcb());
		loginRepository.save(existing);
		return existing.getUserId();
	}

	private int sendUserCreationEmail(UserLoginEntity userRequest, String password, int uid, String ip, String subject,
			int type) throws Exception {
		String usernameN = userRequest.getEmail();
		int ccv = 0;
		if (usernameN != null && !usernameN.isEmpty()) {
			String websitePath = paths;
//			String link = websitePath + "recoapp/Forgotpassword?emailId=" + commonFunction.cipher(usernameN)
			String link = websitePath + "forgot-password?emailId=" + commonFunction.cipher(usernameN) + "&userId="
					+ commonFunction.cipher(Integer.toString(uid));
			String mailBody = commonFunction.getForgotMessageCreate(userRequest.getFirstName(), link, websitePath);
			String filePath = commonFunction.createFolder(file_maillog);
			String fname = commonFunction.writeHTMLFile(mailBody, file_maillog + "/" + filePath,
					"np-" + System.currentTimeMillis());
			String[] to = { usernameN };
//			String[] to = { "sunil.saklani@webelementinc.com" };
//			to [0]= "sunil.saklani@webelementinc.com";
			String cc[] = new String[0];
			String bcc[] = new String[0];
//			String subject = "";
//			subject = "RECO: Login Credentials";

			SmtpEntity findLatestSmtpDetail = smtpRepo.findLatestSmtpDetails();
			System.out.println("findLatestSmtpDetail : " + findLatestSmtpDetail);
			ccv = mailService.postMailAttach(to, cc, bcc, mailBody, subject, "", "", -1, "", findLatestSmtpDetail);
			String iplocal = commonFunction.getLocalIp();
			if (ccv > 0) {
				String linksentdate = commonFunction.getDateAfter(commonFunction.currDate1(), 4, 24,
						"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
				System.out.println("linksentdate : " + linksentdate);
				updateForgotpasswordlink(userRequest.getEmail(), "", linksentdate);

				createMailLog(type, userRequest.getFirstName(), usernameN, "", "", "", subject, filePath + "/" + fname,
						ip, iplocal, 1);
				// "/" + fname, ipAddrStr, iplocal, 1);

			} else {
				createMailLog(type, userRequest.getFirstName(), usernameN, "", "", "", subject, filePath + "/" + fname,
						ip, iplocal, 2);
			}
		}
		return ccv;
	}

	@Transactional
	public int updateForgotpasswordlink(String username, String password, String linksentdate) throws Exception {
		Timestamp linkDateTs = linksentdate != null && !linksentdate.isEmpty() ? Timestamp.valueOf(linksentdate) : null;

		return loginRepository.updateForgotPasswordLink(username, commonFunction.cipher(password), linkDateTs);
	}

	private void createMailLog(int type, String name, String to, String cc, String bcc, String from, String subject,
			String filename, String ip, String iplocal, int status) {
		MailLogEntity log = new MailLogEntity();
		log.setType(type);
		log.setName(name);
		log.setTo(to);
		log.setCc(cc);
		log.setBcc(bcc);
		log.setFrom(from);
		log.setSubject(subject);
		log.setStatus(status);
		log.setFilename(filename);
		log.setRegDate(LocalDateTime.now());
		log.setModDate(LocalDateTime.now());
		log.setIpAddress(ip);
		log.setLocalIp(iplocal);
		mailLogRepo.save(log);

	}

}
