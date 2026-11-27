package com.webelement.esgadminportal.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.webelement.esgadminportal.common.ChangePasswordDTO;
import com.webelement.esgadminportal.common.CommonFunction;
import com.webelement.esgadminportal.common.LoginRequest;
import com.webelement.esgadminportal.common.LoginResponse;
import com.webelement.esgadminportal.common.ModulePermissionDTO;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.common.UserInfo;
import com.webelement.esgadminportal.entity.MailLogEntity;
import com.webelement.esgadminportal.entity.SmtpEntity;
import com.webelement.esgadminportal.entity.UserAccessLogEntity;
import com.webelement.esgadminportal.entity.UserLoginEntity;
import com.webelement.esgadminportal.exception.PasswordValidationException;
import com.webelement.esgadminportal.jwttoken.JwtUtil;
import com.webelement.esgadminportal.repo.BankRepo;
import com.webelement.esgadminportal.repo.BankUserRepo;
import com.webelement.esgadminportal.repo.MailLogRepo;
import com.webelement.esgadminportal.repo.SmtpRepo;
import com.webelement.esgadminportal.repo.UserLoginRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final CommonFunction commonFunction;
	private final UserLoginRepository userLoginRepository;
	private final PasswordService passwordService;	
	private final JwtUtil jwtUtil;
	private final MyUserDetailsService detailsService;
	private final MailService mailService;
	private final MailLogRepo logRepo;
	private final SmtpRepo smtpRepo;
	private final UserAccessLogService accessLogService;
	private final BankUserRepo bankUserRepository;
	@Value("${file_maillog:}")
	private String file_maillog;
	@Value("${paths:}")
	private String paths;
	@Value("${ipflag:}")
	private  String ipFlag;

	public ResponseEntity<ResponseApi<LoginResponse>> login(LoginRequest request, HttpSession session,
			HttpServletRequest httpRequest) throws Exception {
		String userCaptcha = request.getCaptcha(); // User input
		String serverCaptcha = (String) session.getAttribute("captcha");

		if (userCaptcha == null || serverCaptcha == null || !userCaptcha.equalsIgnoreCase(serverCaptcha)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseApi<>(false, "Invalid captcha code.", null));
		}
		
		String loginId = request.getUsername() != null ? request.getUsername() : "";
		String password = request.getPassword() != null ? request.getPassword() : "";
		
		List<Long> bankIds = bankUserRepository.findActiveBankIdsByEmailId(loginId.trim());
		if (bankIds.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseApi<>(false, "You are not authorised to login. Kindly contact to IBA.", null));
		}
		String ip = commonFunction.resolveClientIp(httpRequest);
		String iplocal = commonFunction.getLocalIp();

		UserInfo info = getUserAccess(loginId, password, iplocal);

		String permission = "N";
		if (info != null) {

			if (info.getStatus() < 1) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseApi<>(false,
						"Your account has been expired, please contact the administrator.", null));

			} else {

				int userId = 0;
				userId = info.getUserId();
				permission = info.getPermission() != null ? info.getPermission() : "";
				
				if (userId > 0) {
					detailsService.setUser(request);
					String token = jwtUtil.generateAccessToken(request.getUsername());
					boolean b = false;
					if (permission.equals("Y")) {

						b = true;
					}
					List<ModulePermissionDTO> modules = getModuleListByName(userId, b);
					UserAccessLogEntity accessLogEntity = accessLogService.saveLogin(userId, info.getFirstName(),httpRequest);
					LoginResponse loginResponse = new LoginResponse(token, userId, info.getFirstName(),
							accessLogEntity.getLogId(), modules);
					session.removeAttribute("captcha");
					return ResponseEntity.ok(new ResponseApi<>(true, "Login successful", loginResponse)); // ✅ correct
																											// response
				} else {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
							.body(new ResponseApi<>(false, "Invalid login credentials.", null));
				}

			}

		} else {
			int ccc = getLoginIdMatch(loginId, password);

			String message;
			switch (ccc) {
			case 1:
				message = "Email ID and Password both are incorrect. Please enter correct Email ID and Password.";
				break;
			case 2:
				message = "Email ID is incorrect. Please enter correct Email ID.";
				break;
			case 3:
				message = "Your Password is incorrect. Please enter correct Password.";
				break;
			case 4:
				message = "Your Email ID is not registered. Please contact to FLA Admin Team.";
				break;
			default:
				message = "Invalid username or password.";
			}

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseApi<>(false, message, null));

		}

	}

	private UserInfo getUserAccess(String username, String password, String ip) {
		String newPassword = "";
		try {
			newPassword = commonFunction.cipher(escapeApostrophes(password));

		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Map<String, Object>> results = userLoginRepository.findActiveLogin(username, newPassword);

		UserInfo info = null;
		if (!results.isEmpty()) {
			Map<String, Object> row = results.get(0);

			Integer userId = ((Number) row.get("i_userid")).intValue();
			String firstName = (String) row.get("s_firstname");
			String email = (String) row.get("s_email");
			String mobileNo = (String) row.get("s_mobileno");
			Integer i_status = ((Number) row.get("d_expirydate")).intValue();
			String s_permission = (String) row.get("s_permission");

			info = new UserInfo(userId, firstName, email, mobileNo, i_status, s_permission);
		}
		return info;
	}

	public int getLoginIdMatch(String userName, String password) throws Exception {
		int cc = 0;
		List<Map<String, Object>> result = userLoginRepository.findLoginByEmail(userName);

		if (result.isEmpty()) {
			cc = 4;
		} else {
			for (Map<String, Object> row : result) {
				String e = row.get("s_email") != null ? row.get("s_email").toString() : "";
				String p = row.get("s_password") != null ? row.get("s_password").toString() : "";

				if (!p.isEmpty()) {
					p = commonFunction.decipher(p); // You must implement this method
				}

				if (!e.equals(userName) && !p.equals(password)) {
					cc = 1;
				} else if (!e.equals(userName)) {
					cc = 2;
				} else if (!p.equals(password)) {
					cc = 3;
				}
			}
		}

		return cc;
	}

	public String escapeApostrophes(String input) {
		if (input == null) {
			return "";
		}
		return input.replace("'", "''");
	}

	public ResponseEntity<ResponseApi<String>> forgotpasswordMail(String emailId, HttpServletRequest httpRequest)
			throws Exception {

		Optional<UserLoginEntity> userOpt = userLoginRepository.findByEmailExcludeStatuses(emailId);

		if (!userOpt.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseApi<>(false, "Email not found", null));
		}

		UserLoginEntity user = userOpt.get();
		Integer userId = user.getUserId();

		String ip = commonFunction.resolveClientIp(httpRequest);

		int ccv = sendUserCreationEmail(user, "", userId, ip);

		if (ccv == 1) {
			return ResponseEntity.ok(new ResponseApi<>(true, "Password reset link sent successfully.", null));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseApi<>(false, "Failed to send password reset link.", null));
		}
	}

	private int sendUserCreationEmail(UserLoginEntity userRequest, String password, int uid, String ip)
			throws Exception {
		String usernameN = userRequest.getEmail();
		int ccv = 0;
		if (usernameN != null && !usernameN.isEmpty()) {
			String websitePath = paths;
			String link = websitePath + "forgot-password?emailId=" + commonFunction.cipher(usernameN) + "&userId="
					+ commonFunction.cipher(Integer.toString(uid));
			String mailBody = commonFunction.getForgotMessageCreate(userRequest.getFirstName(), link, websitePath);
			String filePath = commonFunction.createFolder(file_maillog);
			String fname = commonFunction.writeHTMLFile(mailBody, file_maillog + "/" + filePath,
					"np-" + System.currentTimeMillis());
			String[] to = { usernameN };
//			String[] to = { "sunil.saklani@webelementinc.com" };
			String cc[] = new String[0];
			String bcc[] = new String[0];
			String subject = "";
			subject = "IBAESG :: Forgot Password";
			SmtpEntity findLatestSmtpDetail = smtpRepo.findLatestSmtpDetails();

			ccv = mailService.postMailAttach(to, cc, bcc, mailBody, subject, "", "", -1, "", findLatestSmtpDetail);
			String iplocal = commonFunction.getLocalIp();
			if (ccv > 0) {
				String linksentdate = commonFunction.getDateAfter(commonFunction.currDate1(), 4, 24,
						"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
				updateForgotpasswordlink(userRequest.getEmail(), "", linksentdate);
				createMailLog(2, userRequest.getFirstName(), usernameN, "", "", "", subject, filePath + "/" + fname, ip,
						iplocal, 1);

			} else {
				createMailLog(2, userRequest.getFirstName(), usernameN, "", "", "", subject, filePath + "/" + fname, ip,
						iplocal, 2);
			}
		}
		return ccv;
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
		logRepo.save(log);
	}

	@Transactional
	public int updateForgotpasswordlink(String username, String password, String linksentdate) throws Exception {
		Timestamp linkDateTs = linksentdate != null && !linksentdate.isEmpty() ? Timestamp.valueOf(linksentdate) : null;

		return userLoginRepository.updateForgotPasswordLink(username, commonFunction.cipher(password), linkDateTs);
	}

	public ResponseEntity<ResponseApi<String>> forgotpassword(String emailId, String userId, String password)
			throws Exception {
		// Decode inputs
		String decodedEmail = commonFunction.decipher(emailId).trim();
		String decodedUserId = commonFunction.decipher(userId).trim();
		String encodedPassword = commonFunction.cipher(password);

		// Validate if userId is numeric before parsing
		int result = 0;
		try {
			int parsedUserId = Integer.parseInt(decodedUserId);
			result = checkURLValidity(decodedEmail, parsedUserId);
		} catch (NumberFormatException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseApi<>(false, "Invalid User ID in URL", null));
		}

		if (result > 0) {

			Optional<Integer> findUserIdByEmailAndStatus = userLoginRepository.findUserIdByEmailAndStatus(decodedEmail,
					1);
			Integer uId = findUserIdByEmailAndStatus.get();
			if (uId > 0) {
				int count = userLoginRepository.updateForgotPassword(Integer.parseInt(decodedUserId), encodedPassword,
						null);

				if (count == 1) {
					return ResponseEntity.ok(new ResponseApi<>(true, "Password has been created successfully.", null));
				} else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.body(new ResponseApi<>(false, "Something went wrong while updating the password.", null));
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ResponseApi<>(false, "Invalid user details.", null));
			}

		} else {
			// Invalid URL → error response
			String message = "URL is no longer valid or has expired. "
					+ "Please go to the Login Page, enter your email ID, and use the Forgot Password link again.";
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseApi<>(false, message, null));
		}
	}

	public int checkURLValidity(String email, int userId) {
		// Assuming repository returns Optional<Integer>
		return userLoginRepository.checkURLValidity(email, userId).orElse(0);
	}

	public List<ModulePermissionDTO> getModuleListByName(int userId, boolean isFullList) {
		List<Map<String, Object>> rows = userLoginRepository.getModuleListByName(userId);
		List<ModulePermissionDTO> list = new ArrayList<>();

		for (Map<String, Object> row : rows) {
			ModulePermissionDTO dto = new ModulePermissionDTO(userId, getInt(row.get("i_moduleid")),
					getString(row.get("s_name")), getInt(row.get("i_type")), getString(row.get("s_add")),
					getString(row.get("s_edit")), getString(row.get("s_delete")), getString(row.get("s_approve")),
					getString(row.get("s_adminapprove")), getString(row.get("s_view")));
			if (isFullList) {
				dto.setAddPer("Y");
				dto.setEditPer("Y");
				dto.setDeletePer("Y");
				dto.setApprovePer("Y");
				dto.setAdminApprovePer("Y");
				dto.setViewPer("Y");

				list.add(dto); // always include in full list
			} else if (anyPermissionGranted(dto)) {
				list.add(dto);
			}

		}

		return list; // Return actual list
	}

	private boolean anyPermissionGranted(ModulePermissionDTO dto) {
		return "Y".equalsIgnoreCase(dto.getViewPer()) || "Y".equalsIgnoreCase(dto.getAddPer())
				|| "Y".equalsIgnoreCase(dto.getEditPer()) || "Y".equalsIgnoreCase(dto.getDeletePer())
				|| "Y".equalsIgnoreCase(dto.getApprovePer()) || "Y".equalsIgnoreCase(dto.getAdminApprovePer());
	}

	private String getString(Object obj) {
		return obj != null ? obj.toString() : "";
	}

	private Integer getInt(Object obj) {
		return obj instanceof Number ? ((Number) obj).intValue() : 0;
	}

	@Transactional
	public String changePassword(ChangePasswordDTO request) {
//		validateChangePasswordRequest(request);
//
//		AppUser me = CurrentUser.get();


		String oldPassword;
		String newPassword;
		String userId;


		try {
			oldPassword = commonFunction.decipher(request.getOldPassword());
			newPassword = commonFunction.decipher(request.getNewPassword());
			userId = commonFunction.decipher(request.getUserId());
		} catch (Exception e) {
			throw new PasswordValidationException("Invalid encrypted request data.", e);
		}


		validateNewPassword(oldPassword, newPassword);


		String stored = userLoginRepository.findPasswordByUserId(Integer.parseInt(userId));


		if (!passwordService.matches(oldPassword, stored)) {
			throw new PasswordValidationException("Old password is incorrect.");
		}


		int rows = userLoginRepository.updateForgotPassword(Integer.parseInt(userId), request.getNewPassword(), null);


		if (rows != 1) {
			throw new IllegalStateException("Password update failed. Please try again.");
		}


		return "Password changed successfully.";
	}
	
	private void validateNewPassword(String oldPassword, String newPassword) {
		if (newPassword.length() < 8) {
			throw new PasswordValidationException("New password must contain at least 8 characters.");
		}
		if (oldPassword.equals(newPassword)) {
			throw new PasswordValidationException("New password must be different from the old password.");
		}
	}

}
