package com.webelement.esgadminportal.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.webelement.esgadminportal.common.TypeDTO;
import com.webelement.esgadminportal.entity.TypeEntity;

public interface TypeRepo extends JpaRepository<TypeEntity, Integer> {

	List<TypeEntity> findByStatus(Short status);

	@Query("SELECT new com.webelement.esgadminportal.common.TypeDTO( " + "u.typeId, u.name) " + "FROM TypeEntity u "
			+ "WHERE u.status = 1")
	List<TypeDTO> findAllActiveBankType();

}
