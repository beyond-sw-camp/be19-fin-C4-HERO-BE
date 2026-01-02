package com.c4.hero.domain.promotion.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: PersonnelAppointment
 * Description: 인사 발령 예약 정보를 담는 엔티티
 *
 * History
 * 2025/12/31 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Entity
@Table(name = "tbl_personnel_appointment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonnelAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @Column(name = "doc_id", nullable = false)
    private Integer docId;

    @Column(name = "employee_number", nullable = false)
    private String employeeNumber;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "change_type", nullable = false)
    private String changeType; // 특별승진, 정기승진, 부서이동 등

    @Column(name = "details", columnDefinition = "TEXT", nullable = false)
    private String details; // JSON 형태의 상세 변경 내용

    @Column(name = "status", nullable = false)
    private String status; // WAITING, COMPLETED, FAILED

    @Column(name = "error_message")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Builder
    public PersonnelAppointment(Integer docId, String employeeNumber, LocalDate appointmentDate, String changeType, String details, String status) {
        this.docId = docId;
        this.employeeNumber = employeeNumber;
        this.appointmentDate = appointmentDate;
        this.changeType = changeType;
        this.details = details;
        this.status = status;
    }

    public void complete() {
        this.status = "COMPLETED";
        this.processedAt = LocalDateTime.now();
    }

    public void fail(String errorMessage) {
        this.status = "FAILED";
        this.errorMessage = errorMessage;
        this.processedAt = LocalDateTime.now();
    }
}
