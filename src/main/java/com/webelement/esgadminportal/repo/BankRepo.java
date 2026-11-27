package com.webelement.esgadminportal.repo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.webelement.esgadminportal.common.BankInfoDTO;
import com.webelement.esgadminportal.entity.BankEntity;

public interface BankRepo extends JpaRepository<BankEntity, Integer> {

	@Query(value = "SELECT new com.webelement.esgadminportal.common.BankInfoDTO("
			+ "u.bankId, u.name, u.status, u.associationId, assc.name) " + "FROM BankEntity u "
			+ "LEFT JOIN u.association assc " + "WHERE u.bankId > 0 "
			+ "AND (:statusIndex = 0 OR u.status = :statusIndex) "
			+ "AND (:associationId = 0 OR u.associationId = :associationId) " + "AND (:search IS NULL OR :search = '' "
			+ "     OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))) " + "ORDER BY assc.name, u.name")
	Page<BankInfoDTO> findBankDetails(Pageable pageable, @Param("statusIndex") int statusIndex,
			@Param("search") String search, @Param("associationId") int associationId);

	@Query(value = "SELECT DATE_FORMAT(t.ts_regdate, '%d-%m-%Y %h:%i %p') AS entryDate, REPLACE(CONCAT_WS(' ', u.s_firstname), '  ', ' ') AS name, t.s_action AS action, t.i_userid AS userId, t.s_flag AS flag FROM t_transaction t LEFT JOIN t_userlogin u ON u.i_userid = t.i_userid WHERE t.i_moduleid = :moduleId AND t.s_action NOT LIKE '%Log%' AND (:recordId IS NULL OR t.i_recordid = :recordId)", nativeQuery = true)
	List<Object[]> getTransactionLogs(@Param("moduleId") int moduleId, @Param("recordId") Integer recordId);

	@Query(value = "SELECT DATE_FORMAT(t.ts_regdate, '%d-%m-%Y %h:%i %p') AS entryDate, "
			+ "REPLACE(CONCAT_WS(' ', CASE WHEN t.i_userid = -1 THEN bu.s_name ELSE ul.s_firstname END), '  ', ' ') AS name, "
			+ "t.s_action AS action, t.i_userid AS userId, t.s_flag AS flag " + "FROM t_transaction t "
			+ "LEFT JOIN t_bankuser bu ON bu.i_bankuserid = t.i_bankuserid "
			+ "LEFT JOIN t_userlogin ul ON ul.i_userid = t.i_userid " + "WHERE t.i_moduleid = :moduleId "
			+ "AND t.s_action NOT LIKE '%Log%' "
			+ "AND (:recordId IS NULL OR t.i_recordid = :recordId) ORDER BY t.ts_regdate DESC, t.i_transactionid DESC", nativeQuery = true)
	List<Object[]> getTransactionLogsEventAndKnow(@Param("moduleId") int moduleId, @Param("recordId") Integer recordId);

	@Query("SELECT new com.webelement.esgadminportal.common.BankInfoDTO( "
			+ "u.bankId, u.name, u.status, u.associationId, assc.name) " + "FROM BankEntity u "
			+ "LEFT JOIN u.association assc " + "WHERE u.bankId = :bankId")
	BankInfoDTO getBankById(@Param("bankId") int bankId);

	Optional<BankEntity> findByNameIgnoreCase(String name);

	@Query("SELECT COUNT(b) FROM BankEntity b " + "WHERE LOWER(b.name) = LOWER(:name) " + "AND b.status IN (1, 2)")
	int countByNameActive(@Param("name") String name);

	Optional<BankEntity> findByNameIgnoreCaseAndBankIdNot(String name, Integer bankId);

	@Query("SELECT new com.webelement.esgadminportal.common.BankInfoDTO( " + "u.bankId, u.name) " + "FROM BankEntity u "
			+ "WHERE u.status = 1")
	List<BankInfoDTO> findAllActiveBank();

	@Transactional
	@Modifying
	@Query("UPDATE BankEntity u " + "SET u.status = :status " + "WHERE u.bankId = :bankId")
	int deleteBank(@Param("status") short status, @Param("bankId") int bankId);

	@EntityGraph(attributePaths = { "association" })
	Optional<BankEntity> findByBankId(Integer bankId);

}
