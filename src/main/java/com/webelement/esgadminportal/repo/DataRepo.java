package com.webelement.esgadminportal.repo;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.webelement.esgadminportal.common.DataInfoDTO;
import com.webelement.esgadminportal.entity.DataEntity;

@Repository
public interface DataRepo extends JpaRepository<DataEntity, Integer> {

//	@Query(value = "SELECT DATE_FORMAT(t.ts_regdate, '%d-%m-%Y %h:%i %p') AS entryDate, " +
//            "CASE WHEN t.i_userid = -1 THEN bu.s_name ELSE ul.s_firstname END AS name, " +
//            "t.s_action AS action, t.i_userid AS userId, t.s_flag AS flag " +
//            "FROM t_transaction t " +
//            "LEFT JOIN t_bankuser bu ON bu.i_bankuserid = t.i_bankuserid " +
//            "LEFT JOIN t_userlogin ul ON ul.i_userid = t.i_userid " +
//            "WHERE t.i_moduleid = :moduleId " +
//            "AND t.s_action NOT LIKE '%Log%' " +
//            "AND (:recordId IS NULL OR t.i_recordid = :recordId) " +
//            "ORDER BY t.ts_regdate DESC, t.i_transactionid DESC", 
//    nativeQuery = true)
//List<Object[]> getTransactionLogs(@Param("moduleId") int moduleId,
//                               @	("recordId") Integer recordId);

	@Modifying
	@Transactional
	@Query("UPDATE DataEntity d SET d.status = 3 WHERE d.id = :dataId AND d.status = 2")
	int approveData(@Param("dataId") Integer dataId);

	@Modifying
	@Transactional
	@Query("UPDATE DataEntity d SET d.status = 4 WHERE d.id = :dataId AND d.status IN (1, 2)")
	int softDeleteData(@Param("dataId") Integer dataId);

	@Query("SELECT new com.webelement.esgadminportal.common.DataInfoDTO("
			+ "d.id, d.code, d.state, d.catOfBranch, d.statusOfPerpetrator, d.typeOfAccount, d.secOfAcc, "
			+ "d.areaOfOperation, d.typeOfFraud, d.totalAmount, d.occurrence, d.detection, d.classification, "
			+ "d.reportedToRbi, d.detectionMethod, d.status,d.addedBy,d.typeOfAccOther,d.areaOther,d.fraudTypeOther,d.detectionOtherSource) " + "FROM DataEntity d "
			+ "WHERE d.id > 0 AND d.bankId = :bankId AND (:statusIndex = 0 OR d.status = :statusIndex) "
			+ "AND (:catOfBranch IS NULL OR :catOfBranch = '' OR d.catOfBranch = :catOfBranch) "
			+ "AND (:search IS NULL OR :search = '' OR "
			+ "     LOWER(d.code) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "     LOWER(d.state) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "     LOWER(d.catOfBranch) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "     LOWER(d.statusOfPerpetrator) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "     LOWER(d.typeOfFraud) LIKE LOWER(CONCAT('%', :search, '%'))) " + "ORDER BY d.id DESC")
	Page<DataInfoDTO> findDataDetails(Pageable pageable, @Param("statusIndex") int statusIndex,
			@Param("search") String search, @Param("bankId") int bankId, @Param("catOfBranch") String catOfBranch);

	@EntityGraph(attributePaths = { "bank" })
	@Query(value = "SELECT d FROM DataEntity d " + "WHERE d.id > 0 AND (:statusIndex = 0 OR d.status = :statusIndex) "
			+ "AND (:bankId = 0 OR d.bankId = :bankId) " + "AND (:search IS NULL OR :search = '' OR "
			+ "     LOWER(d.code) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "     LOWER(d.state) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "     LOWER(d.catOfBranch) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "     LOWER(d.statusOfPerpetrator) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "     LOWER(d.typeOfFraud) LIKE LOWER(CONCAT('%', :search, '%'))) " + "ORDER BY d.id DESC")
	Page<DataEntity> dataAdminIndexList(Pageable pageable, @Param("statusIndex") int statusIndex,
			@Param("search") String search, @Param("bankId") int bankId);

	@Query("SELECT d.status, COUNT(d) " + "FROM DataEntity d " + "WHERE d.bankId = :bankId OR :bankId = 0 "
			+ "GROUP BY d.status " + "ORDER BY d.status")
	List<Object[]> countByStatusAndBankId(@Param("bankId") Integer bankId);
	
}
