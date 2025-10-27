package com.webelement.esgadminportal.repo;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.webelement.esgadminportal.common.KnowledgeDTO;
import com.webelement.esgadminportal.entity.KnowledgeEntity;

public interface KnowledgeRepo extends JpaRepository<KnowledgeEntity, Integer> {

	@Query("SELECT new com.webelement.esgadminportal.common.KnowledgeDTO("
			+ "k.knowledge, k.bankId, k.name, k.filename, k.bankApproval, k.approval, "
			+ "k.userId, k.bankUserId, k.regDate, k.modDate, k.username, "
			+ "COALESCE(b.name, 'IBA'),k.source,k.status,k.addedBy,COALESCE(b.name, 'IBA') as addedByName) " + // <- Add
																												// bank
																												// name
																												// via
																												// JOIN
			"FROM KnowledgeEntity k " + "LEFT JOIN BankEntity b ON k.bankId = b.bankId " + // <- JOIN condition
			"WHERE (:statusIndex = 0 OR k.status = :statusIndex) " + "AND (" + "  :search IS NULL OR :search = '' OR "
			+ "  LOWER(k.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
//		       "  FUNCTION('TO_CHAR', k.regDate, 'DD-MM-YYYY') LIKE CONCAT('%', :search, '%')" +
			"  FUNCTION('DATE_FORMAT', k.regDate, '%d-%m-%Y') LIKE CONCAT('%', :search, '%') " + ") "
			+ "ORDER BY k.status, k.regDate DESC")
	Page<KnowledgeDTO> findKnowledgeDetails(Pageable pageable, @Param("statusIndex") int statusIndex,
			@Param("search") String search);

	@Query("SELECT new com.webelement.esgadminportal.common.KnowledgeDTO("
			+ "k.knowledge, k.bankId, k.name, k.filename, k.bankApproval, k.approval, "
			+ "k.userId, k.bankUserId, k.regDate, k.modDate, k.username, "
			+ "COALESCE(b.name, 'IBA'),k.source,k.status,k.addedBy,COALESCE(b.name, 'IBA') as addedByName) " + // <- Add
																												// bank
																												// name
																												// via
																												// JOIN
			"FROM KnowledgeEntity k " + "LEFT JOIN BankEntity b ON k.bankId = b.bankId "
			+ "WHERE k.knowledge = :knowledgeId")
	KnowledgeDTO getKnowledgeById(@Param("knowledgeId") int knowledgeId);

	@Transactional
	@Modifying
	@Query("UPDATE KnowledgeEntity u " + "SET u.approval = :approval " + "WHERE u.knowledge = :knowledgeId")
	int approveKnowledge(@Param("approval") int approval, @Param("knowledgeId") int knowledgeId);

	@Transactional
	@Modifying
	@Query("UPDATE KnowledgeEntity u " + "SET u.status = :status " + "WHERE u.knowledge = :knowledgeId")
	int deleteKnowledge(@Param("status") short status, @Param("knowledgeId") int knowledgeId);

}
