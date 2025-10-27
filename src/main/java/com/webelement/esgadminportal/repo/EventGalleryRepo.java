package com.webelement.esgadminportal.repo;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.webelement.esgadminportal.entity.EventGallery;

public interface EventGalleryRepo extends JpaRepository<EventGallery, Integer> {

	// Fetch all gallery items by addedBy
	List<EventGallery> findByAddedByAndEventId(Integer addedBy, Integer eventId);

	List<EventGallery> findByEventId(Integer eventId);

	@Transactional
	@Modifying
	@Query("UPDATE EventGallery u " + "SET u.approval = :approval " + "WHERE u.id = :id")
	int approveEventGallery(@Param("approval") short approval, @Param("id") int id);

}
