package com.webelement.esgadminportal.repo;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.webelement.esgadminportal.common.EventAndWebinarDTO;
import com.webelement.esgadminportal.entity.EventEntity;

public interface EventRepo extends JpaRepository<EventEntity, Integer> {

	@Query("SELECT new com.webelement.esgadminportal.common.EventAndWebinarDTO("
			+ "e.iEventId, e.sTitle, e.sLocation, e.sDescription, e.dDate, e.iApproval, "
			+ "e.iBankApproval, e.iUserId, e.iBankUserId, e.sUsername, e.iType, e.iBankId, "
			+ "COALESCE(b.name, 'IBA'), e.status, e.time as eventtime, " + "e.durationHours, e.durationMinutes) "
			+ "FROM EventEntity e " + "LEFT JOIN BankEntity b ON e.iBankId = b.bankId "
			+ "WHERE (:statusIndex = 0 OR e.status = :statusIndex) " + "AND (" + "  :search IS NULL OR :search = '' OR "
			+ "  LOWER(e.sTitle) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "  FUNCTION('DATE_FORMAT', e.dDate, '%d-%m-%Y') LIKE CONCAT('%', :search, '%') OR "
			+ "  LOWER(e.sDescription) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "  ( (e.iType = 1 AND LOWER(:search) LIKE '%event%') OR "
			+ "    (e.iType = 2 AND LOWER(:search) LIKE '%webinar%') ) " + ") " + "ORDER BY e.status, e.dDate DESC")

	Page<EventAndWebinarDTO> findEvents(Pageable pageable, @Param("statusIndex") int statusIndex,
			@Param("search") String search);

	@Query("SELECT new com.webelement.esgadminportal.common.EventAndWebinarDTO("
			+ "e.iEventId, e.sTitle, e.sLocation, e.sDescription, e.dDate, e.iApproval, "
			+ "e.iBankApproval, e.iUserId, e.iBankUserId, e.sUsername, e.iType, e.iBankId, "
			+ "COALESCE(b.name, 'IBA'), e.status,e.time as eventtime,e.sShortDesc as shortDesc,e.sFilename as filename,e.sLink as link,e.durationHours as durationHours,e.durationMinutes as durationMinutes) "
			+ "FROM EventEntity e " + "LEFT JOIN BankEntity b ON e.iBankId = b.bankId " + "WHERE e.iEventId = :eventId")
	EventAndWebinarDTO getEventAndWebinarByEventId(@Param("eventId") int eventId);

	@Transactional
	@Modifying
	@Query("UPDATE EventEntity u " + "SET u.status = :status " + "WHERE u.iEventId = :eventId")
	int deleteEvent(@Param("status") short status, @Param("eventId") int bankId);

	@Transactional
	@Modifying
	@Query("UPDATE EventEntity u " + "SET u.iApproval = :approval " + "WHERE u.iEventId = :eventId")
	int approveEvent(@Param("approval") int approval, @Param("eventId") int bankId);

	// Using @Query for case-insensitive check and specific status
	@Query("SELECT COUNT(e) FROM EventEntity e " + "WHERE LOWER(e.sTitle) = LOWER(:title) " + "AND e.status IN (1, 2)")
	int countByTitleActive(@Param("title") String title);

	@Query("SELECT COUNT(e) FROM EventEntity e " + "WHERE LOWER(e.sTitle) = LOWER(:title) " + "AND e.status IN (1, 2) "
			+ "AND e.iEventId <> :eventId")
	int countByTitleActiveAndIEventId(@Param("title") String title, @Param("eventId") int eventId);

}
