package com.webelement.esgadminportal.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.webelement.esgadminportal.common.CommonFunction;
import com.webelement.esgadminportal.common.LoginBankResponse;
import com.webelement.esgadminportal.common.LoginRequest;
import com.webelement.esgadminportal.common.LoginResponse;
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.common.UserInfoBank;
import com.webelement.esgadminportal.entity.BankEntity;
import com.webelement.esgadminportal.entity.BankUserEntity;
import com.webelement.esgadminportal.entity.MailLogEntity;
import com.webelement.esgadminportal.entity.SmtpEntity;
import com.webelement.esgadminportal.entity.UserAccessLogEntity;
import com.webelement.esgadminportal.jwttoken.JwtUtil;
import com.webelement.esgadminportal.repo.BankRepo;
import com.webelement.esgadminportal.repo.BankUserRepo;
import com.webelement.esgadminportal.repo.MailLogRepo;
import com.webelement.esgadminportal.repo.SmtpRepo;


@Service
public class AuthBankService {

	@Autowired
	private CommonFunction commonFunction;
//	@Autowired
//	IpAddressEntityRepo ipAddressEntityRepo;
//	@Autowired
//	UserLoginRepository userLoginRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private MyUserDetailsService detailsService;
	
	@Autowired
	private BankUserRepo bankUserRepo;
	@Autowired
	private BankRepo bankRepo;
	@Autowired
	UserAccessLogService accessLogService;
	@Autowired
	MailService mailService;
	
	@Autowired
	SmtpRepo smtpRepo;
	@Autowired
	MailLogRepo logRepo;

	@Value("${ipflag:}")
	private String ipFlag;

	@Value("${paths:}")
	private String paths;
	
	@Value("${file_maillog:}")
	private String file_maillog;
	
	public ResponseEntity<ResponseApi<LoginBankResponse>> login(LoginRequest request, HttpSession session,
	        HttpServletRequest httpRequest) {

	    try {
	        // Captcha validation
	        String userCaptcha = request.getCaptcha();
	        String serverCaptcha = request.getCaptchaAns();
	        if (userCaptcha == null || serverCaptcha == null || !userCaptcha.equalsIgnoreCase(serverCaptcha)) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(new ResponseApi<>(false, "Invalid captcha code.", null));
	        }

	        String loginId = request.getUsername() != null ? request.getUsername() : "";
	        String password = request.getPassword() != null ? request.getPassword() : "";

	       // String decriptPassword = commonFunction.decipher(password);

	        // Check user credentials
	        UserInfoBank info = getUserAccess(loginId, password, commonFunction.getLocalIp());
//	        System.out.println("info : "+info);
	        if (info != null) {
//	            if (info.getStatus() < 1) {
	        	if (info.getStatus() != 1) {
	                return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                        .body(new ResponseApi<>(false,
	                                "Your account has expired. Please contact the administrator.", null));
	            }
	        	
	        	// ✅ Now check bank status
	        	Optional<BankEntity> bankOpt = bankRepo.findById(info.getBankid());

	        	if (!bankOpt.isPresent() || bankOpt.get().getStatus() != 1) {
	        	    return ResponseEntity.status(HttpStatus.FORBIDDEN)
	        	            .body(new ResponseApi<>(false,
	        	                    "Your bank is not active. Please contact the administrator.", null));
	        	}


	            int userId = info.getUserId();
	            if (userId > 0) {
	                detailsService.setUser(request);
	                String token = jwtUtil.generateToken(loginId);
	                
	                UserAccessLogEntity accessLogEntity = accessLogService.saveLogin(userId, info.getFirstName(),httpRequest);
	                LoginBankResponse loginResponse = new LoginBankResponse(token, userId, info.getFirstName(),accessLogEntity.getLogId(),info.getBankid());
	                return ResponseEntity.ok(new ResponseApi<>(true, "Login successful", loginResponse));
	            } else {
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                        .body(new ResponseApi<>(false, "Invalid login credentials.", null));
	            }

	        } else {
	            // Custom error messages based on loginId/password match
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
	                    message = "Your Email ID is not registered. Please contact the FLA Admin Team.";
	                    break;
	                default:
	                    message = "Invalid username or password.";
	            }

	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body(new ResponseApi<>(false, message, null));
	        }

	    } catch (Exception e) {
	        // Log internal error but do not expose details to frontend
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ResponseApi<>(false, "Login failed. Please try again.", null));
	    }
	}

	
	
	private UserInfoBank getUserAccess(String username, String password, String ip) {
		String newPassword = "";
		try {
			newPassword = commonFunction.cipher(escapeApostrophes(password));
//			System.out.println(newPassword+" username : "+username);
			// System.out.println("------------>" + cipher(escapeApostrophes(password)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Map<String, Object>> results = bankUserRepo.findActiveLogin(username, newPassword);
//		System.out.println("results : "+results);
		UserInfoBank info = null;
		if (!results.isEmpty()) {
			Map<String, Object> row = results.get(0);

			Integer userId = ((Number) row.get("i_bankuserid")).intValue();
			String firstName = (String) row.get("s_name");
			String email = (String) row.get("s_email");
			String mobileNo = (String) row.get("s_contactno");
			Integer i_status = ((Number) row.get("i_status")).intValue();
			Integer i_bankid = ((Number) row.get("i_bankid")).intValue();
	
			info = new UserInfoBank(userId, firstName, email, mobileNo, i_status,i_bankid);
		}
		return info;
	}

	public int getLoginIdMatch(String userName, String password) throws Exception {
		int cc = 0;
		List<Map<String, Object>> result = bankUserRepo.findLoginByEmail(userName);

		if (result.isEmpty()) {
			cc = 4; // No such user
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

	private String getString(Object obj) {
		return obj != null ? obj.toString() : "";
	}

	private Integer getInt(Object obj) {
		return obj instanceof Number ? ((Number) obj).intValue() : 0;
	}
	
	
	public ResponseEntity<ResponseApi<String>> forgotpasswordMail(String emailId, HttpServletRequest httpRequest) throws Exception {
//	    System.out.println("emailId : " + emailId);

//	    Optional<BankUserEntity> userOpt = bankUserRepo.findByEmail(emailId);
		 Optional<BankUserEntity> userOpt = bankUserRepo.findByEmailExcludeStatuses(emailId);

	    if (!userOpt.isPresent()) {
	        return ResponseEntity
	                .status(HttpStatus.NOT_FOUND)
	                .body(new ResponseApi<>(false, "Email not found", null));
	    }

	    BankUserEntity user = userOpt.get();
	    Integer userId = user.getBankUserId();
//	    System.out.println("User ID: " + userId);

	    String ip = commonFunction.resolveClientIp(httpRequest);

	    int ccv = sendUserCreationEmail(user, "", userId, ip);

	    if (ccv == 1) {
	        return ResponseEntity.ok(new ResponseApi<>(true, "Password reset link sent successfully.", null));
	    } else {
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ResponseApi<>(false, "Failed to send password reset link.", null));
	    }
	}
	
	
	private int sendUserCreationEmail(BankUserEntity userRequest, String password, int uid, String ip)
			throws Exception {
		String usernameN = userRequest.getEmail();
		int ccv = 0;
		if (usernameN != null && !usernameN.isEmpty()) {
			String websitePath = paths;
//			String link = websitePath + "recoapp/Forgotpassword?emailId=" + commonFunction.cipher(usernameN)
			String link = websitePath + "forgot-password?emailId=" + commonFunction.cipher(usernameN)
					+ "&userId=" + commonFunction.cipher(Integer.toString(uid));
			String mailBody = commonFunction.getForgotMessageCreate(userRequest.getName(), link, websitePath);
			String filePath = commonFunction.createFolder(file_maillog);
			String fname = commonFunction.writeHTMLFile(mailBody, file_maillog + "/" + filePath,
					"np-" + System.currentTimeMillis());
			String[] to = { usernameN };
//			String[] to = { "sunil.saklani@webelementinc.com" };
//			to [0]= "sunil.saklani@webelementinc.com";
			String cc[] = new String[0];
			String bcc[] = new String[0];
			String subject = "";
			subject = "FLA :: Forgot Password";
			// commonFunction.postMailAttach(to, new String[0], new String[0], mailBody,
			// "Welcome", "", "", -1, "");
			SmtpEntity findLatestSmtpDetail = smtpRepo.findLatestSmtpDetails();
//			System.out.println("findLatestSmtpDetail : " + findLatestSmtpDetail);
			ccv = mailService.postMailAttach(to, cc, bcc, mailBody, subject, "", "", -1, "", findLatestSmtpDetail);
			String iplocal = commonFunction.getLocalIp();
			if (ccv > 0) {
				String linksentdate = commonFunction.getDateAfter(commonFunction.currDate1(), 4, 24,
						"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
//				System.out.println("linksentdate : " + linksentdate);
				updateForgotpasswordlink(userRequest.getEmail(), "", linksentdate);

				createMailLog(1, userRequest.getName(), usernameN, "", "", "", subject, filePath + "/" + fname, ip,
						iplocal, 1);
				// "/" + fname, ipAddrStr, iplocal, 1);

			} else {
				createMailLog(1, userRequest.getName(), usernameN, "", "", "", subject, filePath + "/" + fname, ip,
						iplocal, 2);
			}
		}
		return ccv;
	}
	
	@Transactional
	public int updateForgotpasswordlink(String username, String password, String linksentdate) throws Exception {
		Timestamp linkDateTs = linksentdate != null && !linksentdate.isEmpty() ? Timestamp.valueOf(linksentdate) : null;

		return bankUserRepo.updateForgotPasswordLink(username, commonFunction.cipher(password), linkDateTs);
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
	
	public ResponseEntity<ResponseApi<String>> forgotpassword(String emailId, String userId,String password) throws Exception {
        // Decode inputs
        String decodedEmail = commonFunction.decipher(emailId).trim();
        String decodedUserId = commonFunction.decipher(userId).trim();
        String encodedPassword = commonFunction.cipher(password);

//        System.out.println("Decoded Email: " + decodedEmail + ", Decoded UserId: " + decodedUserId+ " encodedPassword = "+encodedPassword);
//        System.out.println("Decoded Email: " + decodedEmail + ", Decoded UserId: " + decodedUserId+ " encodedPassword = "+ commonFunction.cipher(encodedPassword));

        // Validate if userId is numeric before parsing
        int result = 0;
        try {
            int parsedUserId = Integer.parseInt(decodedUserId);
            result = checkURLValidity(decodedEmail, parsedUserId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            		.body(new ResponseApi<>(false, "Invalid User ID in URL", null));
                   // .body(new ResponseApi<>("error", "Invalid User ID in URL", null));
        }

//        System.out.println("URL Validity Result: " + result);

        if (result > 0) {
        	
        	Optional<Integer> findUserIdByEmailAndStatus = bankUserRepo.findUserIdByEmailAndStatus(decodedEmail,(short) 1 );
        	  Integer uId = findUserIdByEmailAndStatus.get();
//	    System.out.println("User ID: " + uId);
//   		    Integer userId = user.getUserId();
//	    if(uId>0) {
//	    	int count =userLoginRepository.updateForgotPassword(Integer.parseInt(decodedUserId), encodedPassword, null);
//	    }else {
//	    	 
//	    }
	    if (uId > 0) {
	        int count = bankUserRepo.updateForgotPassword(Integer.parseInt(decodedUserId), encodedPassword, null);

	        if (count == 1) {
	            return ResponseEntity.ok(
	                new ResponseApi<>(true, "Password has been created successfully.", null)
	            );
	        } else {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ResponseApi<>(false, "Something went wrong while updating the password.", null));
	        }
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	            .body(new ResponseApi<>(false, "Invalid user details.", null));
	    }
         	
//        	return ResponseEntity.ok(new ResponseApi<>(true, "Proceed to forgot password page", null));
        } else {
            // Invalid URL → error response
            String message = "URL is no longer valid or has expired. " +
                    "Please go to the Login Page, enter your email ID, and use the Forgot Password link again.";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseApi<>(false, message, null));
        }
    }
	
	 public int checkURLValidity(String email, int userId) {
	        // Assuming repository returns Optional<Integer>
	        return bankUserRepo
	                .checkURLValidity(email, userId)
	                .orElse(0);
	    }
	 
}
