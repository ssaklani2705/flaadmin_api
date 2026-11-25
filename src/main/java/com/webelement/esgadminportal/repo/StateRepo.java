package com.webelement.esgadminportal.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.webelement.esgadminportal.common.StateDropdownDTO;
import com.webelement.esgadminportal.entity.StateEntity;

@Repository
public interface StateRepo extends JpaRepository<StateEntity, Integer> {

	@Query("SELECT new com.webelement.esgadminportal.common.StateDropdownDTO(s.stateId, s.name) "
			+ "FROM StateEntity s ORDER BY s.stateId ASC")
	List<StateDropdownDTO> getAllStates();
}
