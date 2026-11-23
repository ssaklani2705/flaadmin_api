package com.webelement.esgadminportal.repo;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.webelement.esgadminportal.common.UserInfoDTO;
import com.webelement.esgadminportal.entity.BankUserEntity;

public interface BankUserRepo extends JpaRepository<BankUserEntity, Integer> {

	@Query(value = "SELECT i_bankuserid,i_bankid, s_name, s_email, s_contactno, i_status FROM t_bankuser WHERE s_email =:email AND s_password =:password ", nativeQuery = true)
	List<Map<String, Object>> findActiveLogin(@Param("email") String email, @Param("password") String password);

	@Query(value = "SELECT s_email, s_password FROM t_userlogin WHERE i_status = 1 AND s_email = :email", nativeQuery = true)
	List<Map<String, Object>> findLoginByEmail(@Param("email") String email);

	@Query("SELECT new com.webelement.esgadminportal.common.UserInfoDTO(u.bankUserId,u.name, u.contactNo, u.email,u.status,b.name as bankName) FROM BankUserEntity u left join BankEntity b on u.bankId=b.bankId WHERE u.bankUserId > 0 AND  (:statusIndex = 0 OR u.status = :statusIndex)  "
			+ "AND (\r\n" + "            :search IS NULL OR :search = '' OR\r\n"
			+ "            LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR\r\n"
			+ "            LOWER(u.contactNo) LIKE LOWER(CONCAT('%', :search, '%')) OR\r\n"
			+ "            LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) OR\r\n"
			+ "            LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))\r\n"
			+ "      )  ORDER BY u.status, u.name")
	Page<UserInfoDTO> findBasicUserInfo(Pageable pageable, @Param("statusIndex") int statusIndex, String search);

	@Query("SELECT new com.webelement.esgadminportal.common.UserInfoDTO( "
			+ "u.bankUserId, u.name, u.contactNo, u.email, u.status,b.name as bankName,t.name as bankType,u.bankId) "
			+ "FROM BankUserEntity u " + "LEFT JOIN BankEntity b ON u.bankId = b.bankId "
			+ "LEFT JOIN TypeEntity t ON t.typeId = b.typeId " + "WHERE u.bankUserId = :bankUserId")
	UserInfoDTO getBankUserById(@Param("bankUserId") int bankUserId);

	@Query(value = "SELECT DATE_FORMAT(t.ts_regdate, '%d-%m-%Y %h:%i %p') AS entryDate, REPLACE(CONCAT_WS(' ', u.s_name), '  ', ' ') AS name, t.s_action AS action, t.i_userid AS userId, t.s_flag AS flag FROM t_transaction t LEFT JOIN t_bankuser u ON u.i_bankuserid = t.i_userid WHERE t.i_moduleid = :moduleId AND t.s_action NOT LIKE '%Log%' AND (:recordId IS NULL OR t.i_recordid = :recordId)", nativeQuery = true)
	List<Object[]> getTransactionLogs(@Param("moduleId") int moduleId, @Param("recordId") Integer recordId);

	@Query(value = "SELECT  COUNT(*) as count FROM t_bankuser WHERE s_email = :email AND i_status IN (1,2) AND  (:i_bankuserid = 0 OR i_bankuserid != :i_bankuserid)", nativeQuery = true)
	int checkDuplicacy(@Param("i_bankuserid") int i_bankuserid, @Param("email") String email);

	@Transactional
	@Modifying
	@Query("UPDATE BankUserEntity u " + "SET u.password = :password, " + "    u.sentLinkDate = :linksentdate, "
			+ "    u.modDate = CURRENT_TIMESTAMP " + "WHERE u.email = :username")
	int updateForgotPasswordLink(@Param("username") String username, @Param("password") String password,
			@Param("linksentdate") Timestamp linksentdate);

	@Transactional
	@Modifying
	@Query("UPDATE BankUserEntity u " + "SET u.status = :status " + "WHERE u.bankUserId = :bankUserId")
	int deleteBankUser(@Param("status") short status, @Param("bankUserId") int bankUserId);

	@Query("SELECT new com.webelement.esgadminportal.common.UserInfoDTO(u.bankUserId, u.name, u.contactNo, u.email, u.status, b.name as bankName) "
			+ "FROM BankUserEntity u LEFT JOIN BankEntity b ON u.bankId = b.bankId "
			+ "WHERE u.bankId = :bankId AND u.status <> 3 ORDER BY u.status, u.name")
	List<UserInfoDTO> findUsersByBankId(@Param("bankId") int bankId);
}
