package com.c4.hero.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: NotificationRegistDTO
 * Description: 알림 데이터 전송 객체
 *
 * History
 * 2025/12/11 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRegistDTO {
    Integer employeeId;
    String type;
    String title;
    String message;
    String link;
    Integer attendanceId;
    Integer payrollId;
    Integer documentId;
    Integer evaluationId;
}
