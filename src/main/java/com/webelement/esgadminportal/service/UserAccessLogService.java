package com.webelement.esgadminportal.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.webelement.esgadminportal.common.CommonFunction;
import com.webelement.esgadminportal.common.UserAccessLogDTO;
import com.webelement.esgadminportal.entity.UserAccessLogEntity;
import com.webelement.esgadminportal.repo.UserAccessLogRepo;

@Service
public class UserAccessLogService {

	@Autowired
	private UserAccessLogRepo accessLogRepo;
	@Autowired
	private CommonFunction commonFunction;

	/**
	 * Save login log
	 */
	public UserAccessLogEntity saveLogin(Integer userId, String username) {
		UserAccessLogEntity log = new UserAccessLogEntity();
		log.setUserId(userId);
		log.setIpAddress(commonFunction.getLocalIp());
		log.setLoginTime(LocalDateTime.now());
		log.setStatus(0); // active session
		log.setIbankUserId(-1);
		log.setUserName(username);

		return accessLogRepo.save(log);
	}

	/**
	 * Update logout time
	 */
	public void updateLogout(int sessionId) {
		// Find the active session (status = 0)
		Optional<UserAccessLogEntity> optionalLog = accessLogRepo.findByLogId(sessionId);

		if (optionalLog.isPresent()) {
			UserAccessLogEntity log = optionalLog.get();
			log.setLogoutTime(LocalDateTime.now());
			log.setStatus(1); // mark session as logged out
			accessLogRepo.save(log);
		}
	}

	public Page<UserAccessLogDTO> getUserAccessDetails(int page, int size, String search) {
		Pageable pageable = PageRequest.of(page, size);
		return accessLogRepo.findUserAccessDetails(pageable, search);
	}

}
