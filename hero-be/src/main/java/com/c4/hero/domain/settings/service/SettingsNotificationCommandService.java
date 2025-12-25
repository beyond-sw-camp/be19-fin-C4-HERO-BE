package com.c4.hero.domain.settings.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.auth.security.CustomUserDetails;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.employee.type.EmployeeStatus;
import com.c4.hero.domain.notification.dto.NotificationRegistDTO;
import com.c4.hero.domain.notification.service.NotificationCommandService;
import com.c4.hero.domain.settings.dto.request.SettingsNotificationBroadcastRequestDTO;
import com.c4.hero.domain.settings.dto.request.SettingsNotificationGroupRequestDTO;
import com.c4.hero.domain.settings.dto.request.SettingsNotificationIndividualRequestDTO;
import com.c4.hero.domain.settings.entity.SettingsNotificationHistory;
import com.c4.hero.domain.settings.repository.SettingsNotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <pre>
 * Class Name: SettingsNotificationCommandService
 * Description: 관리자 알림 발송 전용 Command 서비스
 *
 * History
 * 2025/12/24 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SettingsNotificationCommandService {

    private final EmployeeRepository employeeRepository;
    private final NotificationCommandService notificationCommandService;
    private final SettingsNotificationHistoryRepository historyRepository;

    private static final int ADMIN_ID = 0;

    /**
     * 전체 직원 대상 알림 발송
     */
    public void broadcastNotification(SettingsNotificationBroadcastRequestDTO request) {
        log.info("Broadcasting notification: {}", request.getTitle());

        List<Employee> targetEmployees = employeeRepository.findAllByStatusNot(EmployeeStatus.RETIRED);

        log.info("Broadcast target count: {}", targetEmployees.size());

        NotificationSendResult result = sendNotifications(
                targetEmployees, request.getTitle(), request.getMessage(), request.getType(), request.getLink());

        saveHistory(request.getTitle(), request.getMessage(), request.getType(), "ALL", result);
    }

    /**
     * 그룹 대상 알림 발송
     */
    public void sendGroupNotification(SettingsNotificationGroupRequestDTO request) {
        log.info("Sending group notification: {}", request.getTitle());

        List<Employee> targetEmployees = getTargetEmployeesForGroup(request);

        log.info("Group target count: {}", targetEmployees.size());

        NotificationSendResult result = sendNotifications(
                targetEmployees, request.getTitle(), request.getMessage(), request.getType(), request.getLink());

        saveHistory(request.getTitle(), request.getMessage(), request.getType(), "GROUP", result);
    }

    /**
     * 개별 직원 대상 알림 발송
     */
    public void sendIndividualNotification(SettingsNotificationIndividualRequestDTO request) {
        log.info("Sending individual notification: {}", request.getTitle());

        List<Employee> targetEmployees = getTargetEmployeesForIndividual(request);

        log.info("Individual target count: {}", targetEmployees.size());

        NotificationSendResult result = sendNotifications(
                targetEmployees, request.getTitle(), request.getMessage(), request.getType(), request.getLink());

        saveHistory(request.getTitle(), request.getMessage(), request.getType(), "INDIVIDUAL", result);
    }

    /**
     * 그룹 조건에 맞는 직원 조회
     */
    private List<Employee> getTargetEmployeesForGroup(SettingsNotificationGroupRequestDTO request) {
        List<Integer> deptIds = filterEmptyList(request.getDepartmentIds());
        List<Integer> gradeIds = filterEmptyList(request.getGradeIds());
        List<Integer> jobTitleIds = filterEmptyList(request.getJobTitleIds());

        if (deptIds == null && gradeIds == null && jobTitleIds == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "발송 대상 그룹이 선택되지 않았습니다.");
        }

        log.info("Fetching employees - deptIds: {}, gradeIds: {}, jobTitleIds: {}", deptIds, gradeIds, jobTitleIds);

        List<Employee> employees = employeeRepository.findEmployeesByGroupConditions(deptIds, gradeIds, jobTitleIds);

        log.info("Found {} employees matching group conditions", employees.size());

        return employees;
    }

    /**
     * 개별 직원 조회
     */
    private List<Employee> getTargetEmployeesForIndividual(SettingsNotificationIndividualRequestDTO request) {
        if (request.getEmployeeIds() == null || request.getEmployeeIds().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "발송 대상 직원이 선택되지 않았습니다.");
        }

        List<Employee> employees = employeeRepository.findAllById(request.getEmployeeIds());

        log.info("Found {} employees for individual notification", employees.size());

        return employees;
    }

    /**
     * 대상 직원들에게 알림 발송 (NotificationCommandService 재사용)
     */
    private NotificationSendResult sendNotifications(
            List<Employee> employees, String title, String message, String type, String link) {

        int targetCount = employees.size();
        int successCount = 0;
        int failureCount = 0;

        log.info("Starting notification send to {} employees", targetCount);

        for (Employee employee : employees) {
            try {
                NotificationRegistDTO registDTO = NotificationRegistDTO.builder()
                        .employeeId(employee.getEmployeeId())
                        .type(type)
                        .title(title)
                        .message(message)
                        .link(link)
                        .build();

                if (notificationCommandService.registAndSendNotification(registDTO) != null) {
                    successCount++;
                } else {
                    // 알림 설정으로 수신 거부된 경우
                    failureCount++;
                    log.debug("Notification blocked by user settings for employee {}", employee.getEmployeeId());
                }
            } catch (Exception e) {
                log.error("Failed to send notification to employee {}: {}", employee.getEmployeeId(), e.getMessage());
                failureCount++;
            }
        }

        log.info("Notification send completed - Target: {}, Success: {}, Failure: {}",
                targetCount, successCount, failureCount);

        return new NotificationSendResult(targetCount, successCount, failureCount);
    }

    /**
     * 발송 이력 저장
     */
    private void saveHistory(String title, String message, String type, String scope, NotificationSendResult result) {
        Integer senderId = getCurrentSenderId();

        log.info("Saving notification history - Scope: {}, Target: {}, Success: {}, Failure: {}",
                scope, result.getTargetCount(), result.getSuccessCount(), result.getFailureCount());

        SettingsNotificationHistory history = SettingsNotificationHistory.builder()
                .title(title)
                .message(message)
                .type(type)
                .scope(scope)
                .targetCount(result.getTargetCount()) // ⭐ 실제 대상 인원수
                .successCount(result.getSuccessCount())
                .failureCount(result.getFailureCount())
                .senderId(senderId)
                .sentAt(LocalDateTime.now())
                .build();

        historyRepository.save(history);

        log.info("Notification history saved successfully - historyId: {}", history.getHistoryId());
    }

    /**
     * 빈 리스트를 null로 변환
     */
    private List<Integer> filterEmptyList(List<Integer> list) {
        return (list != null && !list.isEmpty()) ? list : null;
    }

    /**
     * 현재 로그인한 사용자 ID 반환
     */
    private Integer getCurrentSenderId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                return userDetails.getEmployeeId();
            }
        }

        log.warn("Could not determine sender ID. Defaulting to ADMIN_ID.");
        return ADMIN_ID;
    }

    /**
     * 발송 결과 내부 클래스
     */
    private static class NotificationSendResult {
        private final int targetCount;
        private final int successCount;
        private final int failureCount;

        public NotificationSendResult(int targetCount, int successCount, int failureCount) {
            this.targetCount = targetCount;
            this.successCount = successCount;
            this.failureCount = failureCount;
        }

        public int getTargetCount() {
            return targetCount;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getFailureCount() {
            return failureCount;
        }
    }
}