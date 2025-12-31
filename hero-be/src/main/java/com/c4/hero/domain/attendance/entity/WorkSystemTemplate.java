package com.c4.hero.domain.attendance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * <pre>
 * Entity Name: WorkSystemTemplate
 * Description: 근무제 유형별 기본 출퇴근 시간(템플릿)을 정의하는 엔티티
 *
 * History
 * 2025/12/10 (이지윤) 최초 작성 및 컨벤션 적용
 * 2025/12/29 (이지윤) 휴게 시간/사유 필드 및 도메인 메서드 추가
 * </pre>
 *
 * 근무제 유형(예: 기본 근무제, 시차 출퇴근제 등)마다
 * 기본으로 적용되는 출근/퇴근 시간 및 휴게 시간 정보를 관리합니다.
 */
@Entity
@Table(name = "tbl_work_system_template")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class WorkSystemTemplate {

    /** 근무제 템플릿 PK (식별자) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_system_template_id")
    private Integer id;

    /** 기본 출근 시간 */
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /** 기본 퇴근 시간 */
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /** 휴게 시간(분 단위, 예: 60 → 60분 휴게) */
    @Column(name = "break_min_minutes", nullable = false)
    private Integer breakMinMinutes;

    /** 근무제 템플릿 설명 또는 사유(옵션) */
    @Column(name = "reason", length = 255)
    private String reason;

    /** 이 템플릿이 속한 근무제 유형 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_system_type_id")
    private WorkSystemType workSystemType;

    /**
     * 근무제 템플릿 수정용 도메인 메서드입니다.
     *
     * <p>
     * Setter 대신 도메인 메서드를 통해 출퇴근/휴게 시간 및 근무제 유형을 변경합니다.
     * </p>
     *
     * @param startTime       변경할 출근 시간
     * @param endTime         변경할 퇴근 시간
     * @param breakMinMinutes 변경할 휴게 시간(분 단위)
     * @param reason          변경 사유/설명
     * @param workSystemType  변경할 근무제 유형 엔티티
     */
    public void update(
            LocalTime startTime,
            LocalTime endTime,
            Integer breakMinMinutes,
            String reason,
            WorkSystemType workSystemType
    ) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.breakMinMinutes = breakMinMinutes;
        this.reason = reason;
        this.workSystemType = workSystemType;
    }

    /**
     * 근무제 템플릿 생성용 정적 팩토리 메서드입니다.
     *
     * @param startTime       출근 시간
     * @param endTime         퇴근 시간
     * @param breakMinMinutes 휴게 시간(분)
     * @param reason          설명/사유
     * @param workSystemType  근무제 유형 엔티티
     * @return 생성된 WorkSystemTemplate 인스턴스
     */
    public static WorkSystemTemplate create(
            LocalTime startTime,
            LocalTime endTime,
            Integer breakMinMinutes,
            String reason,
            WorkSystemType workSystemType
    ) {
        WorkSystemTemplate template = new WorkSystemTemplate();
        template.startTime = startTime;
        template.endTime = endTime;
        template.breakMinMinutes = breakMinMinutes;
        template.reason = reason;
        template.workSystemType = workSystemType;

        return template;
    }
}
