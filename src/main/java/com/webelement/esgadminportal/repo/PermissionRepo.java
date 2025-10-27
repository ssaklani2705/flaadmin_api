package com.webelement.esgadminportal.repo;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.webelement.esgadminportal.entity.PermissionEntity;

public interface PermissionRepo extends JpaRepository<PermissionEntity, Integer>{

	 @Transactional
	    @Modifying
	    @Query("DELETE FROM PermissionEntity p WHERE p.userId = :userId")
	    void deleteByUserId(@Param("userId") Integer userId);
	
}
