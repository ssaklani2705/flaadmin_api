package com.webelement.esgadminportal.repo;

import javax.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.webelement.esgadminportal.common.GalleryDTO;
import com.webelement.esgadminportal.entity.GalleryEntity;

public interface GalleryRepo extends JpaRepository<GalleryEntity, Integer> {

	@Query("SELECT new com.webelement.esgadminportal.common.GalleryDTO("
			+ "g.galleryId, g.caption, g.filename, g.status, g.type, g.regDate,gr.name) " + "FROM GalleryEntity g left join GroupEntity gr on g.groupId=gr.groupId "
			+ "WHERE g.galleryId > 0 " + "AND (:statusIndex = 0 OR g.status = :statusIndex) " + "AND ("
			+ "  :search IS NULL OR :search = '' OR " + "  LOWER(g.caption) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(gr.name) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ "  (" + "    LOWER(:search) = 'image' AND g.type = 1" + "  ) OR ("
			+ "    LOWER(:search) = 'video' AND g.type = 2" + "  )" + ") " + "ORDER BY g.status, g.regDate desc")
	Page<GalleryDTO> getAllGalleries(Pageable pageable, @Param("statusIndex") int statusIndex,
			@Param("search") String search);

	@Transactional
	@Modifying
	@Query("UPDATE GalleryEntity u " + "SET u.status = :status " + "WHERE u.galleryId = :galleryId")
	int deleteGallery(@Param("status") int status, @Param("galleryId") int galleryId);

}
