package com.webelement.esgadminportal.repo;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.webelement.esgadminportal.common.BankInfoDTO;
import com.webelement.esgadminportal.common.GroupDTO;
import com.webelement.esgadminportal.common.GroupInfoDTO;
import com.webelement.esgadminportal.entity.GroupEntity;

public interface GroupRepo extends JpaRepository<GroupEntity, Integer>{
	
	@Query(
		    "SELECT new com.webelement.esgadminportal.common.GroupInfoDTO(" +
		    " g.groupId, g.name, g.status, g.date ) " +
		    "FROM GroupEntity g " +
		    "WHERE g.groupId > 0 " +
		    "AND (:statusIndex = 0 OR g.status = :statusIndex) " +
		    "AND ( " +
		    "     :search IS NULL OR :search = '' OR " +
		    "     LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%'))  OR ( :search IS NULL OR DATE_FORMAT(g.date, '%d-%m-%Y') = :search )    " +
		    ") " +
		    "ORDER BY g.status, g.name"
		)
		Page<GroupInfoDTO> findGroupDetails(
		        Pageable pageable,
		        @Param("statusIndex") int statusIndex,
		        @Param("search") String search
		);

	
	@Query("SELECT new com.webelement.esgadminportal.common.GroupInfoDTO( "
			+ " g.groupId, g.name, g.status, g.date) " + "FROM GroupEntity g "
			+ "WHERE g.groupId = :groupId")
	GroupInfoDTO getGroupById(@Param("groupId") int groupId);
	
	
	
	// For ADD
    @Query("SELECT COUNT(g) FROM GroupEntity g WHERE LOWER(g.name) = LOWER(:name) AND g.status IN (1, 2)")
    int countByNameActive(@Param("name") String name);

    // For UPDATE (exclude same ID)
    @Query("SELECT COUNT(g) FROM GroupEntity g WHERE LOWER(g.name) = LOWER(:name) AND g.groupId <> :groupId AND g.status IN (1, 2)")
    int countByNameActiveAndNotId(
            @Param("name") String name,
            @Param("groupId") Integer groupId
    );
    
	@Transactional
	@Modifying
	@Query("UPDATE GroupEntity g " + "SET g.status = :status " + "WHERE g.groupId = :groupId")
	int deleteGroup(@Param("status") short status, @Param("groupId") int groupId);
    
	
	   @Query("SELECT new com.webelement.esgadminportal.common.GroupDTO(g.groupId, g.name) FROM GroupEntity g WHERE g.status = 1 ORDER BY g.name")
	    List<GroupDTO> findActiveGroups();
	
}
