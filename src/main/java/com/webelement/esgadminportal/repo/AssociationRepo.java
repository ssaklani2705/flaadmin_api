package com.webelement.esgadminportal.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.webelement.esgadminportal.common.AssociationDTO;
import com.webelement.esgadminportal.entity.AssociationEntity;

@Repository
public interface AssociationRepo extends JpaRepository<AssociationEntity, Integer> {

	@Query("SELECT new com.webelement.esgadminportal.common.AssociationDTO(" + "a.id, a.name, a.status) "
			+ "FROM AssociationEntity a " + "WHERE a.status = :status " + "ORDER BY a.name ASC")
	List<AssociationDTO> findByStatusOrderByNameAsc(@Param("status") Short status);

	List<AssociationEntity> findByAssociationTypeAndStatusOrderByNameAsc(Short associationType, Short status);

	boolean existsByNameIgnoreCaseAndStatus(String name, Short status);
}
