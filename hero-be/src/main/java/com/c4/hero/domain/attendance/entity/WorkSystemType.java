package com.c4.hero.domain.attendance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Entity Name: WorkSystemType
 * Description: 근무제 유형(예: 기본 근무제, 시차 출퇴근제 등)을 정의하는 엔티티
 *
 * History
 * 2025/12/10 (이지윤) 최초 작성 및 컨벤션 적용
 * </pre>
 *
 * 근무제 템플릿(WorkSystemTemplate) 및 근태(Attendance)에서
 * 참조하는 근무제의 마스터 정보(코드/이름)를 관리합니다.
 *
 * @author 이지윤
 * @version 1.0
 */
@Entity
@Table(name = "tbl_work_system_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class WorkSystemType {

    /** 근무제 유형 PK (식별자) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_system_type_id")
    private Integer workSystemTypeId;

    /** 근무제 유형 이름 (예: 기본 근무제, 시차 출퇴근제 등) */
    @Column(name = "name")
    private String workSystemName;
}
