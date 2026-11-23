package com.webelement.esgadminportal.common;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GalleryDTO {

	private Integer galleryId;
	private String caption;
	private String filename;
	private Integer status;
	private Integer type;
	private LocalDateTime regDate;
	private String name;

}