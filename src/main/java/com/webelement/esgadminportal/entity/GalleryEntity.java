package com.webelement.esgadminportal.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "t_gallery")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GalleryEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "i_galleryid")
    private Integer galleryId;

    @Column(name = "i_type")
    private Integer type;

    @Column(name = "s_caption")
    private String caption;

    @Column(name = "s_filename")
    private String filename;

    @Column(name = "i_userid")
    private Integer userId;

    @Column(name = "i_status")
    private Integer status;

    @Column(name = "ts_regdate")
    private LocalDateTime regDate;

    @Column(name = "ts_moddate")
    private LocalDateTime modDate;

}
