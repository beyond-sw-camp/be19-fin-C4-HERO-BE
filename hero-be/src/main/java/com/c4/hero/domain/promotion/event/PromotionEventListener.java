package com.c4.hero.domain.promotion.event;

import com.c4.hero.domain.approval.event.ApprovalCompletedEvent;
import com.c4.hero.domain.approval.event.ApprovalRejectedEvent;
import com.c4.hero.domain.promotion.dto.request.PromotionReviewRequestDTO;
import com.c4.hero.domain.promotion.service.PromotionCommandService;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionEventListener {
    private final PromotionCommandService promotionCommandService;
    private final ObjectMapper objectMapper;

    @EventListener
    @Transactional
    public void handleApprovalCompleted(ApprovalCompletedEvent event) {
        if (!"personnelappointment".equals(event.getTemplateKey())) {
            return;
        }

        log.info("🎉 인사발령 결재 완료 이벤트 수신 - docId: {}", event.getDocId());

        try {
            // 1. JSON 데이터 파싱
            Map<String, Object> details = objectMapper.readValue(event.getDetails(), new TypeReference<>() {});
            String promotionType = (String) details.get("promotionType");

            if ("SPECIAL".equals(promotionType)) {
                // 특별 승진 처리
                Integer employeeId = (Integer) details.get("employeeId");
                Integer targetGradeId = (Integer) details.get("gradeAfter");
                String reason = (String) details.get("reason");

                if (employeeId == null || targetGradeId == null) {
                    log.error("❌ 특별 승진 처리 실패 - 필수 정보 누락. docId: {}", event.getDocId());
                    return;
                }

                promotionCommandService.confirmDirectPromotion(employeeId, targetGradeId, reason);
                log.info("✅ 특별 승진 발령 처리 완료 - employeeId: {}, targetGradeId: {}", employeeId, targetGradeId);

            } else {
                // 정기 승진 처리 (REGULAR 또는 null)
                Integer candidateId = (Integer) details.get("candidateId");
                if (candidateId == null) {
                    log.error("❌ 정기 승진 처리 실패 - candidateId를 찾을 수 없음. docId: {}", event.getDocId());
                    return;
                }

                PromotionReviewRequestDTO requestDTO = PromotionReviewRequestDTO.builder()
                        .candidateId(candidateId)
                        .isPassed(true)
                        .build();

                promotionCommandService.confirmFinalApproval(requestDTO);
                log.info("✅ 정기 승진 발령 처리 완료 - candidateId: {}", candidateId);
            }

        } catch (Exception e) {
            log.error("❌ 인사발령 처리 중 오류 발생 - docId: {}", event.getDocId(), e);
            throw new RuntimeException("인사발령 처리 중 오류 발생", e);
        }
    }

    @EventListener
    @Transactional
    public void handleApprovalRejected(ApprovalRejectedEvent event) {
        if (!"personnelappointment".equals(event.getTemplateKey())) {
            return;
        }

        log.info("🚨 인사발령 결재 반려 이벤트 수신 - docId: {}", event.getDocId());

        try {
            Map<String, Object> details = objectMapper.readValue(event.getDetails(), new TypeReference<>() {});
            String promotionType = (String) details.get("promotionType");

            if ("SPECIAL".equals(promotionType)) {
                // 특별 승진 반려 - 별도 처리 필요 없음 (DB에 남는 데이터가 없으므로)
                log.info("ℹ️ 특별 승진 결재 반려됨 - 별도 처리 없음");
            } else {
                // 정기 승진 반려 - 후보자 상태 변경 필요
                Integer candidateId = (Integer) details.get("candidateId");
                if (candidateId != null) {
                    PromotionReviewRequestDTO requestDTO = PromotionReviewRequestDTO.builder()
                            .candidateId(candidateId)
                            .isPassed(false)
                            .comment(event.getComment())
                            .build();
                    promotionCommandService.confirmFinalApproval(requestDTO);
                    log.info("✅ 정기 승진 반려 처리 완료 - candidateId: {}", candidateId);
                }
            }
        } catch (Exception e) {
            log.error("❌ 인사발령 반려 처리 중 오류 발생 - docId: {}", event.getDocId(), e);
            throw new RuntimeException("인사발령 반려 처리 중 오류 발생", e);
        }
    }
}
