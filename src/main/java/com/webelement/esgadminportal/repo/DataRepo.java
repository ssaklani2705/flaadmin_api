package com.webelement.esgadminportal.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.webelement.esgadminportal.common.DataInfoDTO;
import com.webelement.esgadminportal.entity.DataEntity;

@Repository
public interface DataRepo extends JpaRepository<DataEntity, Integer> {

<<<<<<< HEAD
	@Query("SELECT new com.webelement.esgadminportal.common.DataInfoDTO("
			+ "d.id, d.code, d.state, d.catOfBranch, d.statusOfPerpetrator, d.typeOfAccount, d.secOfAcc, "
			+ "d.areaOfOperation, d.typeOfFraud, d.totalAmount, d.occurrence, d.detection, d.classification, "
			+ "d.reportedToRbi, d.detectionMethod, d.status) " + "FROM DataEntity d "
			+ "WHERE d.id > 0 AND d.bankId = :bankId AND (:statusIndex = 0 OR d.status = :statusIndex) "
			+ "AND (:search IS NULL OR :search = '' OR "
			+ "     LOWER(d.code) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "     LOWER(d.state) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "     LOWER(d.catOfBranch) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "     LOWER(d.statusOfPerpetrator) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "     LOWER(d.typeOfFraud) LIKE LOWER(CONCAT('%', :search, '%'))) " + "ORDER BY d.id DESC")
	Page<DataInfoDTO> findDataDetails(Pageable pageable, @Param("statusIndex") int statusIndex,
			@Param("search") String search,@Param("bankId") int bankId);
=======
//	@Query("SELECT new com.webelement.esgadminportal.common.DataInfoDTO("
//			+ "d.id, d.code, d.state, d.catOfBranch, d.statusOfPerpetrator, d.typeOfAccount, d.secOfAcc, "
//			+ "d.areaOfOperation, d.typeOfFraud, d.totalAmount, d.occurrence, d.detection, d.classification, "
//			+ "d.reportedToRbi, d.detectionMethod, d.status) " + "FROM DataEntity d "
//			+ "WHERE d.id > 0 AND (:statusIndex = 0 OR d.status = :statusIndex) "
//			+ "AND (:search IS NULL OR :search = '' OR "
//			+ "     LOWER(d.code) LIKE LOWER(CONCAT('%', :search, '%')) OR "
//			+ "     LOWER(d.state) LIKE LOWER(CONCAT('%', :search, '%')) OR "
//			+ "     LOWER(d.catOfBranch) LIKE LOWER(CONCAT('%', :search, '%')) OR "
//			+ "     LOWER(d.statusOfPerpetrator) LIKE LOWER(CONCAT('%', :search, '%')) OR "
//			+ "     LOWER(d.typeOfFraud) LIKE LOWER(CONCAT('%', :search, '%'))) " + "ORDER BY d.id DESC")
//	Page<DataInfoDTO> findDataDetails(Pageable pageable, @Param("statusIndex") int statusIndex,
//			@Param("search") String search);
>>>>>>> 4957b560445d3c658928d4c6cd544f41c0daef83

	@EntityGraph(attributePaths = { "bank" })
	@Query(value = "SELECT d FROM DataEntity d LEFT JOIN d.bank b "
	        + "WHERE d.id > 0 AND (:statusIndex = 0 OR d.status = :statusIndex) "
	        + "AND (:search IS NULL OR :search = '' OR "
	        + "     LOWER(d.code) LIKE LOWER(CONCAT('%', :search, '%')) OR "
	        + "     LOWER(d.state) LIKE LOWER(CONCAT('%', :search, '%')) OR "
	        + "     LOWER(d.catOfBranch) LIKE LOWER(CONCAT('%', :search, '%')) OR "
	        + "     LOWER(d.statusOfPerpetrator) LIKE LOWER(CONCAT('%', :search, '%')) OR "
	        + "     LOWER(d.typeOfFraud) LIKE LOWER(CONCAT('%', :search, '%')) OR "
	        + "     LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%'))) "
	        + "ORDER BY d.id DESC")
	Page<DataEntity> findDataDetails(Pageable pageable,
	                                 @Param("statusIndex") int statusIndex,
	                                 @Param("search") String search);

	@Query("SELECT d.status, COUNT(d) " + "FROM DataEntity d " + "WHERE d.bankId = :bankId OR :bankId = 0 "
			+ "GROUP BY d.status " + "ORDER BY d.status")
	List<Object[]> countByStatusAndBankId(@Param("bankId") Integer bankId);
}
