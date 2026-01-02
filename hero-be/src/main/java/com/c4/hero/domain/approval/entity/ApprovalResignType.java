package com.c4.hero.domain.approval.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: ApprovalResignType
 * Description: 퇴직 사유 엔티티
 *              사직서 작성 시 선택 가능한 퇴직 사유 목록을 관리
 *
 * History
 * 2025/12/28 (민철) 최초 작성
 * 2026/01/01 (민철) 클래스 주석 및 필드 주석 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
@Entity
@Table(name = "tbl_exit_reason")
@Getter
@NoArgsConstructor
public class ApprovalResignType {

    /**
     * 퇴직 사유 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exit_reason_id")
    private Integer resignTypeId;

    /**
     * 퇴직 사유명
     * 예: 개인 사정, 이직, 건강, 가족 사정, 기타
     */
    @Column(name = "reason_name")
    private String resignTypeName;
}