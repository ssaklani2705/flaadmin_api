package com.webelement.esgadminportal.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name = "t_eventgallery")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventGallery {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "i_eventgalleryid", nullable = false)
	private Integer id;

	@Column(name = "i_eventid")
	private Integer eventId;

	@Column(name = "i_type")
	private Short type; // 1 = Image, 2 = Video

	@Lob
	@Column(name = "s_filename")
	private String filename;

	@Column(name = "s_caption")
	private String caption;

	@Column(name = "s_username")
	private String username;

	@Column(name = "i_bankapproval")
	private Short bankApproval;

	@Column(name = "i_approval")
	private Short approval;

	@Column(name = "i_addedby")
	private Integer addedBy;

	@Column(name = "i_userid")
	private Integer userId;

	@Column(name = "i_bankuserid")
	private Integer bankUserId;

	@Column(name = "ts_regdate")
	private LocalDateTime regDate;

	@Column(name = "ts_moddate")
	private LocalDateTime modDate;

}