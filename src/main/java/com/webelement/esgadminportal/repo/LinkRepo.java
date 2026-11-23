package com.webelement.esgadminportal.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.webelement.esgadminportal.entity.LinkEntity;

public interface LinkRepo extends JpaRepository<LinkEntity, Integer> {

	@Query("SELECT new com.webelement.esgadminportal.entity.LinkEntity(u.linkId,u.name,u.url,u.status,u.sequence) FROM LinkEntity u WHERE u.linkId > 0 AND  (:statusIndex = 0 OR u.status = :statusIndex)  "
			+ "AND (\r\n" + "            :search IS NULL OR :search = '' OR\r\n"
			+ "            LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) \r\n"
			+ "      )  ORDER BY u.status, u.sequence, u.name")
	Page<LinkEntity> findLinkDetails(Pageable pageable, @Param("statusIndex") int statusIndex, String search);

	@Query(value = "SELECT DATE_FORMAT(t.ts_regdate, '%d-%m-%Y %h:%i %p') AS entryDate, REPLACE(CONCAT_WS(' ', u.s_firstname), '  ', ' ') AS name, t.s_action AS action, t.i_userid AS userId, t.s_flag AS flag FROM t_transaction t LEFT JOIN t_userlogin u ON u.i_userid = t.i_userid WHERE t.i_moduleid = :moduleId AND t.s_action NOT LIKE '%Log%' AND (:recordId IS NULL OR t.i_recordid = :recordId)", nativeQuery = true)
	List<Object[]> getTransactionLogs(@Param("moduleId") int moduleId, @Param("recordId") Integer recordId);

	@Query("SELECT new com.webelement.esgadminportal.entity.LinkEntity( " + "u.linkId, u.name, u.url,u.status,u.sequence) "
			+ "FROM LinkEntity u " + "WHERE u.linkId = :linkId")
	LinkEntity getLinkById(@Param("linkId") int linkId);

	@Query("SELECT COUNT(l) FROM LinkEntity l " + "WHERE LOWER(l.name) = LOWER(:name) " + "AND l.status IN (1, 2)")
	int countByNameActive(@Param("name") String name);

	// For update – check active/pending name existence but exclude current linkId
	Optional<LinkEntity> findByNameIgnoreCaseAndLinkIdNotAndStatusIn(String name, Integer linkId, List<Short> statuses);

}
