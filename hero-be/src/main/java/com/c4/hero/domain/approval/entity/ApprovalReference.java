package com.c4.hero.domain.approval.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name  : ApprovalReference
 * Description : 결재 참조자 엔티티
 *               문서의 참조자 정보를 관리 (결재에는 참여하지 않지만 알림을 받는 직원)
 *
 * History
 *   2025/12/26 (민철) 최초 작성
 *   2026/01/01 (민철) 필드 주석 추가
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
@Entity
@Table(name = "tbl_approval_reference")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ApprovalReference {

    /**
     * 참조자 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ref_id")
    private Integer refId;

    /**
     * 문서 ID (Foreign Key)
     * 어떤 문서의 참조자인지 지정
     */
    @Column(name = "doc_id", nullable = false)
    private Integer docId;

    /**
     * 참조 직원 ID (Foreign Key)
     * 참조로 지정된 직원의 ID
     * 결재 완료 시 알림을 받음
     */
    @Column(name = "emp_id", nullable = false)
    private Integer empId;

    /**
     * 생성일시
     * 참조자가 추가된 시간 (자동 설정)
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정일시
     * 참조자 정보가 마지막으로 수정된 시간 (자동 갱신)
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}