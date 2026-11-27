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
import com.webelement.esgadminportal.common.ResponseApi;
import com.webelement.esgadminportal.common.UserInfoDTO;
import com.webelement.esgadminportal.common.ValidateData;
import com.webelement.esgadminportal.entity.BankUserEntity;
import com.webelement.esgadminportal.entity.MailLogEntity;
import com.webelement.esgadminportal.entity.SmtpEntity;
import com.webelement.esgadminportal.entity.TransactionEntity;
import com.webelement.esgadminportal.repo.BankUserRepo;
import com.webelement.esgadminportal.repo.MailLogRepo;
import com.webelement.esgadminportal.repo.SmtpRepo;
import com.webelement.esgadminportal.repo.UserLoginRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankUserService {
	private final BankUserRepo bankUserRepo;
	
	private final CommonFunction commonFunction;
	
	private final SmtpRepo smtpRepo;
	
	private final MailService mailService;
	
	private final MailLogRepo mailLogRepo;
	
	private final ValidateData validateData;

	
	private final UserLoginRepository userLoginRepository;
	
	@Value("${file_maillog:}")
	private String file_maillog;

	@Value("${paths_esgportal:}")
	private String paths;

	public Page<UserInfoDTO> findBasicUserInfo(int page, int size, int statusIndex, String search) {
		return bankUserRepo.findBasicUserInfo(PageRequest.of(page, size), statusIndex, search);
	}

	public UserInfoDTO getUserById(int bankUserId) {
		UserInfoDTO user = bankUserRepo.getBankUserById(bankUserId);

//		if (user != null) {
//			// Append transaction history
//			List<TransactionEntity> history = getTransactionLogs(2, bankUserId);
//			System.out.println("history : " + history);
//			user.setTransactionhistory(history);
//		}

		return user;
	}

	public List<TransactionEntity> getTransactionLogs(int moduleId, Integer recordId) {
		// List<Object[]> results = bankUserRepo.getTransactionLogs(moduleId, recordId);
		List<Object[]> results = userLoginRepository.getTransactionLogs(moduleId, recordId);
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

	public ResponseEntity<?> saveBankUserDetail(BankUserEntity bankUserEntity, HttpServletRequest httpRequest)
			throws Exception {

//		System.out.println("bankUserEntity "+bankUserEntity);

		// Sanitize
		String cleanName = validateData.replacename(bankUserEntity.getName());
		if (cleanName.isEmpty()) {
			throw new RuntimeException("Bank name is invalid or empty");
		}
		bankUserEntity.setName(cleanName); // Important: update bank entity with sanitized name

		boolean isEdit = bankUserEntity.getBankUserId() > 0;
		System.out.println("isEdit : " + isEdit);
		if (!isEdit) {
		
			int isDuplicateEmail = bankUserRepo.checkDuplicacy(0, bankUserEntity.getEmail());
			if (isDuplicateEmail == 1) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(new ResponseApi<>(false, "User already exists", "Exist"));
			}
		} else {
			// EDIT logic - check if email belongs to another user
			int isDuplicateEmail = bankUserRepo.checkDuplicacy(bankUserEntity.getBankUserId(),
					bankUserEntity.getEmail());
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
			uid = createUser(bankUserEntity, password, 0);

			// Optionally send welcome/reset email
			String ip = commonFunction.resolveClientIp(httpRequest);
			String subject = "ESG PORTAL :: Login Credentials";
			int type = 1;
			int ccv = sendUserCreationEmail(bankUserEntity, password, uid, ip, subject, type);
			commonFunction.createHistoryAccess(bankUserEntity.getUserId(), commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "User Added", 2, uid, -1);
		} else {
			// Update existing user
			uid = updateUser(bankUserEntity);
			commonFunction.createHistoryAccess(bankUserEntity.getUserId(), commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "User Updated", 2, uid, -1);
		}

		return ResponseEntity.ok(new ResponseApi<>(true,
				isEdit ? "Bank User updated successfully" : "Bank User added successfully", "Success"));
	}

	// Bank User Creation
	public int createUser(BankUserEntity bankUserEntity, String password, int loginuserId) {
		try {
			BankUserEntity bankUser = new BankUserEntity();

			bankUser.setName(bankUserEntity.getName());
			bankUser.setContactNo(bankUserEntity.getContactNo());
			bankUser.setEmail(bankUserEntity.getEmail());
			bankUser.setBankId(bankUserEntity.getBankId());
			bankUser.setUserId(bankUserEntity.getUserId());
			bankUser.setPassword(commonFunction.cipher(password)); // keep your encryption logic
			bankUser.setStatus((short) 1); // status is hardcoded as in JDBC
			String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			Timestamp timestamp = Timestamp.valueOf(dateStr);
			bankUser.setRegDate(timestamp);
			bankUser.setModDate(timestamp);

			BankUserEntity savedBankUser = bankUserRepo.save(bankUser);
			return savedBankUser.getBankUserId(); // JPA auto-fills the generated ID

		} catch (Exception e) {
			e.printStackTrace(); // or use a logger
			return 0;
		}
	}

	// Update Bank User Details
	private int updateUser(BankUserEntity request) {
		// Fetch existing user, update only necessary fields
		BankUserEntity existing = bankUserRepo.findById(request.getBankUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));
		existing.setName(request.getName());
		existing.setContactNo(request.getContactNo());
		existing.setBankId(request.getBankId());
//		existing.setEmail(request.getEmail());
		existing.setStatus(request.getStatus());
		bankUserRepo.save(existing);
		return existing.getBankUserId();
	}

	private int sendUserCreationEmail(BankUserEntity bankUserEntity, String password, int uid, String ip,
			String subject, int type) throws Exception {
		String usernameN = bankUserEntity.getEmail();
		int ccv = 0;
		if (usernameN != null && !usernameN.isEmpty()) {
			String websitePath = paths;
			String link = websitePath + "forgot-password?emailId=" + commonFunction.cipher(usernameN) + "&userId="
					+ commonFunction.cipher(Integer.toString(uid));

			String mailBody = commonFunction.getBankUserForgotMessageCreate(bankUserEntity.getName(), link,
					websitePath);
			String filePath = commonFunction.createFolder(file_maillog);
			String fname = commonFunction.writeHTMLFile(mailBody, file_maillog + "/" + filePath,
					"np-" + System.currentTimeMillis());
			String[] to = { usernameN };
			String cc[] = new String[0];
			String bcc[] = new String[0];

			SmtpEntity findLatestSmtpDetail = smtpRepo.findLatestSmtpDetails();
			System.out.println("findLatestSmtpDetail : " + findLatestSmtpDetail);
			ccv = mailService.postMailAttach(to, cc, bcc, mailBody, subject, "", "", -1, "", findLatestSmtpDetail);
			String iplocal = commonFunction.getLocalIp();
			if (ccv > 0) {
				String linksentdate = commonFunction.getDateAfter(commonFunction.currDate1(), 4, 24,
						"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
				System.out.println("linksentdate : " + linksentdate);
				updateForgotpasswordlink(bankUserEntity.getEmail(), "", linksentdate);

				createMailLog(type, bankUserEntity.getName(), usernameN, "", "", "", subject, filePath + "/" + fname,
						ip, iplocal, 1);
				// "/" + fname, ipAddrStr, iplocal, 1);

			} else {
				createMailLog(type, bankUserEntity.getName(), usernameN, "", "", "", subject, filePath + "/" + fname,
						ip, iplocal, 2);
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
		mailLogRepo.save(log);

	}

	public ResponseEntity<ResponseApi<String>> deleteBankUser(Integer bankUserId, Integer userId,
			HttpServletRequest httpRequest) {
		Optional<BankUserEntity> existingBankUser = bankUserRepo.findById(bankUserId);
		if (!existingBankUser.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ResponseApi<>(false, "Bank User not found", null));
		}
		int updatedRows = bankUserRepo.deleteBankUser((short) 3, bankUserId);
		if (updatedRows > 0) {
			commonFunction.createHistoryAccess(userId, commonFunction.resolveClientIp(httpRequest),
					commonFunction.getLocalIp(), "User Deleted", 2, bankUserId, -1);
			return ResponseEntity.ok(new ResponseApi<>(true, "Bank User deleted successfully", null));
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseApi<>(false, "Failed to delete bank user", null));
		}
	}

	public List<UserInfoDTO> getUsersByBankId(int bankId) {
		return bankUserRepo.findUsersByBankId(bankId);
	}
}
