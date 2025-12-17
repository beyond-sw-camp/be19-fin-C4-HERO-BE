/**
 * <pre>
 * Class Name: NotificationSettingsDTO
 * Description: 알림 설정 DTO
 *
 * History
 * 2025/12/17 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
package com.c4.hero.domain.notification.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NotificationSettingsDTO {

    // 기본 정보
    private Integer settingId;
    private Integer employeeId;

    // 알림 타입별 설정
    private Boolean attendanceEnabled;
    private Boolean payrollEnabled;
    private Boolean approvalEnabled;
    private Boolean leaveEnabled;
    private Boolean evaluationEnabled;
    private Boolean systemEnabled;

    // 수신 방법 설정
    private Boolean browserNotification;
    private Boolean emailNotification;
    private Boolean smsNotification;

    // 메타 정보
    private String createdAt;
    private String updatedAt;
}