package com.c4.hero.domain.settings.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * Class Name: SettingsNotificationGroupRequestDTO
 * Description: 특정 그룹 대상 알림 발송 요청 DTO
 *
 * History
 * 2025/12/22 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SettingsNotificationGroupRequestDTO {

    /**
     * 알림 제목
     */
    private String title;

    /**
     * 알림 메시지
     */
    private String message;

    /**
     * 알림 타입
     */
    private String type;

    /**
     * 연결 링크 (optional)
     */
    private String link;

    /**
     * 대상 부서 ID 목록
     */
    private List<Integer> departmentIds;

    /**
     * 대상 직급 ID 목록
     */
    private List<Integer> gradeIds;

    /**
     * 대상 직책 ID 목록
     */
    private List<Integer> jobTitleIds;
}