package com.webelement.esgadminportal.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.webelement.esgadminportal.entity.UserTransactionEntity;

public interface UserTransactionRepo extends JpaRepository<UserTransactionEntity, Integer> {

	@Query(value = "SELECT DATE_FORMAT(t.ts_regdate, '%d-%m-%Y %h:%i %p') AS entryDate, REPLACE(CONCAT_WS(' ', u.s_name), '  ', ' ') AS name, t.s_action AS action, t.i_userid AS userId, t.s_flag AS flag FROM t_user_transaction t LEFT JOIN t_bankuser u ON u.i_bankuserid = t.i_userid WHERE t.i_moduleid = :moduleId AND t.s_action NOT LIKE '%Log%' AND (:recordId IS NULL OR t.i_recordid = :recordId)", nativeQuery = true)
	List<Object[]> getTransactionLogs(@Param("moduleId") int moduleId, @Param("recordId") Integer recordId);

}
