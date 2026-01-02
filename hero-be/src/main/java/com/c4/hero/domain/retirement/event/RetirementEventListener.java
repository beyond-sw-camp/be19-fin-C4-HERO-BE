package com.c4.hero.domain.retirement.event;

import com.c4.hero.domain.approval.event.ApprovalCompletedEvent;
import com.c4.hero.domain.retirement.service.RetirementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Map;

/**
 * <pre>
 * Class Name: RetirementEventListener
 * Description: 퇴사 결재 완료 이벤트를 처리하는 리스너 클래스
 *
 * History
 * 2025/12/30 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RetirementEventListener {

    private final RetirementService retirementService;
    private final ObjectMapper objectMapper;

    /**
     * 결재 완료 이벤트를 수신하여 퇴사 관련 후처리를 수행합니다.
     * 템플릿 키가 "resign"인 경우에만 동작합니다.
     *
     * @param event 결재 완료 이벤트
     */
    @EventListener
    @Transactional
    public void handleApprovalCompleted(ApprovalCompletedEvent event) {
        if (!"resign".equals(event.getTemplateKey())) {
            return;
        }

        log.info("🎉 퇴사 결재 완료 이벤트 수신 - docId: {}", event.getDocId());

        try {
            // 1. JSON 데이터 파싱
            Map<String, Object> details = objectMapper.readValue(event.getDetails(), new TypeReference<>() {});
            
            String employeeNumber = (String) details.get("employeeNumber");
            String dateStr = (String) details.get("terminationDate");
            Integer terminationReason = (Integer) details.get("terminateReason");
            String terminationReasonDetail = (String) details.get("terminateReasonDetail");

            if (employeeNumber == null || dateStr == null || terminationReason == null) {
                log.error("❌ 퇴사 처리 실패 - 필수 정보 누락. docId: {}", event.getDocId());
                return;
            }

            LocalDate terminationDate = LocalDate.parse(dateStr);

            // 2. 퇴사 승인 처리 서비스 호출
            // 퇴사일이 미래라도 terminationDate를 미리 설정해두면 Scheduler가 처리하므로 즉시 호출해도 됨.
            retirementService.processRetirementApproval(
                    employeeNumber,
                    terminationDate,
                    terminationReason,
                    terminationReasonDetail
            );

            log.info("✅ 퇴사 처리 완료 (예정일 설정) - 사번: {}, 퇴사일: {}", employeeNumber, terminationDate);

        } catch (Exception e) {
            log.error("❌ 퇴사 결재 완료 처리 중 오류 발생 - docId: {}", event.getDocId(), e);
            throw new RuntimeException("퇴사 결재 완료 처리 중 오류 발생", e);
        }
    }
}
