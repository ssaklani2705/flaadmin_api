package com.webelement.esgadminportal.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.webelement.esgadminportal.common.CommitteeDTO;
import com.webelement.esgadminportal.entity.CommitteeEntity;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommitteeRepo extends JpaRepository<CommitteeEntity, Integer>{

//	 @Query("SELECT new com.webelement.esgadminportal.common.CommitteeDTO(c.committeeId, c.bankId, c.name, c.designation, c.email, c.sequence, c.status,b.name as bankName) " +
//	           "FROM CommitteeEntity c left join BankEntity b on c.bankId=b.bankId " +
//	           "WHERE c.committeeId > 0 " +
//	           "AND (:statusIndex = 0 OR c.status = :statusIndex) " +
//	           "AND ( :search IS NULL OR :search = '' OR " +
//	           "      LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
//	           "      LOWER(c.designation) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
//	           "      LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
//	           ") " +
//	           "ORDER BY c.sequence")
//	    Page<CommitteeDTO> findCommitteeDetails(Pageable pageable,
//	                                            @Param("statusIndex") int statusIndex,
//	                                            @Param("search") String search);
	
	@Query("SELECT new com.webelement.esgadminportal.common.CommitteeDTO(c.committeeId, c.bankId, c.name, c.designation, c.email, c.sequence, c.status, b.name as bankName) " +
		       "FROM CommitteeEntity c LEFT JOIN BankEntity b ON c.bankId = b.bankId " +
		       "WHERE c.committeeId > 0 " +
		       "AND (:statusIndex = 0 OR c.status = :statusIndex) " +
		       "AND ( :search IS NULL OR :search = '' OR " +
		       "      LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "      LOWER(c.designation) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "      LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "      LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) " + // ✅ Added bank name search
		       ") " +
		       "ORDER BY c.sequence")
		Page<CommitteeDTO> findCommitteeDetails(Pageable pageable,
		                                        @Param("statusIndex") int statusIndex,
		                                        @Param("search") String search);

	 
	 
	 @Query("SELECT new com.webelement.esgadminportal.common.CommitteeDTO( " +
	           "c.committeeId, c.bankId, c.name, c.designation, c.email, c.sequence, c.status,b.name as bankName) " +
	           "FROM CommitteeEntity c left join BankEntity b on c.bankId=b.bankId " +
	           "WHERE c.committeeId = :committeeId")
	    CommitteeDTO getCommitteeById(@Param("committeeId") int committeeId);
	 
	 @Query("SELECT COUNT(c) FROM CommitteeEntity c " +
		       "WHERE LOWER(c.email) = LOWER(:email) " +
		       "AND c.status IN (1, 2)")
		int countByEmailActive(@Param("email") String email);
	 
	 
	 @Query("SELECT COUNT(c) FROM CommitteeEntity c " +
		       "WHERE LOWER(c.email) = LOWER(:email) " +
		       "AND c.status IN (1, 2) and committeeId != :committeeId")
		int countByEmailActiveAndCommitteeId(@Param("email") String email, @Param("committeeId") int committeeId);
	 
	 @Transactional
		@Modifying
		@Query("UPDATE CommitteeEntity u " + "SET u.status = :status " + "WHERE u.committeeId = :committeeId")
		int deleteCommittee(@Param("status") short status, @Param("committeeId") int committeeId);

	
}
