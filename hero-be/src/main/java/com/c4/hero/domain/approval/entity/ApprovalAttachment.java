package com.c4.hero.domain.approval.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_approval_attachment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApprovalAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Integer fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_id")
    private ApprovalDocument document;

    @Column(name = "origin_name")
    private String originName;

    @Column(name = "save_path")
    private String savePath;

    @Column(name = "file_size")
    private Long fileSize;
}