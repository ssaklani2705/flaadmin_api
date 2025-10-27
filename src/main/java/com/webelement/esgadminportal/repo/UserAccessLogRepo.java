package com.webelement.esgadminportal.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.webelement.esgadminportal.common.UserAccessLogDTO;
import com.webelement.esgadminportal.entity.UserAccessLogEntity;

public interface UserAccessLogRepo extends JpaRepository<UserAccessLogEntity, Integer> {

	// Find log by session id (useful for updating logout time)
	Optional<UserAccessLogEntity> findByUserIdAndStatus(Integer userId, Integer status);

	Optional<UserAccessLogEntity> findByLogId(Integer logId);

	@Query("SELECT new com.webelement.esgadminportal.common.UserAccessLogDTO("
			+ "u.logId, u.userId, u.ipAddress, u.loginTime, u.logoutTime, "
			+ "u.status, u.userName, u.ibankUserId, COALESCE(b.name, 'IBA')) " + "FROM UserAccessLogEntity u "
			+ "LEFT JOIN BankUserEntity bu ON u.ibankUserId = bu.bankUserId "
			+ "LEFT JOIN BankEntity b ON bu.bankId = b.bankId " + "WHERE (:search IS NULL OR :search = '' OR "
			+ "LOWER(u.ipAddress) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "LOWER(COALESCE(b.name, 'IBA')) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "CAST(u.loginTime AS string) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "CAST(u.logoutTime AS string) LIKE LOWER(CONCAT('%', :search, '%'))) " + "ORDER BY u.loginTime DESC")
	Page<UserAccessLogDTO> findUserAccessDetails(Pageable pageable, @Param("search") String search);

}
