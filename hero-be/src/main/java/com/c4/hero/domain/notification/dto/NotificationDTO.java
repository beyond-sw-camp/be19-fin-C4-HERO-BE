package com.c4.hero.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: NotificationDto
 * Description: 알림 데이터 전송 객체
 *
 * History
 * 2025/12/11 (최혜원) 최초 작성
 * </pre>
 *
 * @author 최혜원
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Integer notificationId;
    private String type;
    private String title;
    private String message;
    private String link;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private Integer employeeId;
    private Integer attendanceId;
    private Integer payrollId;
    private Integer documentId;
    private Integer evaluationId;
}