package com.c4.hero.domain.notification.entity;

import com.c4.hero.domain.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: Notification
 * Description: 알림 엔티티
 *
 * History
 * 2025/12/11 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */

@Entity
@Table(name = "tbl_notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;

    @Column(name = "type", nullable = false, length = 30)
    private String type;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "link", length = 255)
    private String link;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    // 알림을 받을 직원 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // 연관 FK들 - 필요에 따라 추가
    @Column(name = "attendance_id")
    private Integer attendanceId;

    @Column(name = "payroll_id")
    private Integer payrollId;

    @Column(name = "document_id")
    private Integer documentId;

    @Column(name = "evaluation_id")
    private Integer evaluationId;

    // 생성 시 시간 자동 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isRead == null) {
            this.isRead = false;
        }
    }
}