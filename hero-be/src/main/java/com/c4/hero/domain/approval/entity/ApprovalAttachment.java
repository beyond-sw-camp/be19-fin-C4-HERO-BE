package com.c4.hero.domain.approval.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * Class Name: ApprovalAttachment
 * Description: 결재 문서 첨부파일 엔티티
 *              문서에 첨부된 파일 정보를 관리하며, S3에 저장된 파일의 메타데이터를 보관함
 *
 * History
 * 2025/12/26 (민철) 최초 작성
 * 2025/12/31 (민철) S3 저장 방식으로 변경 (save_path에 S3 키 저장)
 *
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
@Entity
@Table(name = "tbl_approval_attachment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApprovalAttachment {

    /**
     * 첨부파일 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Integer fileId;

    /**
     * 소속 결재 문서 (Many-to-One 관계)
     * LAZY 로딩으로 성능 최적화
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_id")
    private ApprovalDocument document;

    /**
     * 원본 파일명
     * 사용자가 업로드한 파일의 원래 이름
     */
    @Column(name = "origin_name")
    private String originName;

    /**
     * S3 저장 경로 (S3 Key)
     * AWS S3에 저장된 파일의 고유 키 값
     * 예: "approval/2025/12/31/uuid-filename.pdf"
     */
    @Column(name = "save_path")
    private String savePath;

    /**
     * 파일 크기 (Bytes)
     */
    @Column(name = "file_size")
    private Long fileSize;
}