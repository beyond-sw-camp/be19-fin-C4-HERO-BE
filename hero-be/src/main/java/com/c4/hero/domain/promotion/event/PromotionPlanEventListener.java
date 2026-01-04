package com.c4.hero.domain.promotion.event;

import com.c4.hero.domain.approval.event.ApprovalCompletedEvent;
import com.c4.hero.domain.promotion.dto.PromotionDetailPlanDTO;
import com.c4.hero.domain.promotion.dto.request.PromotionPlanRequestDTO;
import com.c4.hero.domain.promotion.service.PromotionCommandService;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionPlanEventListener {
    private final PromotionCommandService promotionCommandService;
    private final ObjectMapper objectMapper;

    @EventListener
    @Transactional
    public void handleApprovalCompleted(ApprovalCompletedEvent event) {
        if (!"promotionplan".equals(event.getTemplateKey())) {
            return;
        }

        log.info("🎉 승진 계획 결재 완료 이벤트 수신 - docId: {}", event.getDocId());

        try {
            // 1. JSON 문자열을 JsonNode로 파싱
            JsonNode rootNode = objectMapper.readTree(event.getDetails());

            // 2. PromotionPlanRequestDTO 매핑
            // event의 title을 planName으로 사용하기 위해 전달
            PromotionPlanRequestDTO requestDTO = mapToPromotionPlanRequestDTO(rootNode, event.getTitle());

            // 3. 서비스 호출
            promotionCommandService.registerPromotionPlan(requestDTO);
        } catch (Exception e) {
            log.error("❌ 승진 계획 처리 중 오류 발생 - docId: {}", event.getDocId(), e);
            throw new RuntimeException("승진 계획 처리 중 오류 발생", e);
        }
    }

    private PromotionPlanRequestDTO mapToPromotionPlanRequestDTO(JsonNode rootNode, String title) {
        // 날짜 포맷터 (필요 시 수정)
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

        // event의 title을 우선적으로 planName으로 사용
        String planName = title;
        if (planName == null || planName.isBlank()) {
            planName = getText(rootNode, "planName");
        }

        String planContent = getText(rootNode, "planContent");
        
        LocalDate nominationDeadlineAt = null;
        if (rootNode.has("nominationDeadlineAt") && !rootNode.get("nominationDeadlineAt").isNull()) {
             nominationDeadlineAt = LocalDate.parse(getText(rootNode, "nominationDeadlineAt"), formatter);
        }

        LocalDate appointmentAt = null;
        if (rootNode.has("appointmentAt") && !rootNode.get("appointmentAt").isNull()) {
            appointmentAt = LocalDate.parse(getText(rootNode, "appointmentAt"), formatter);
        }

        List<PromotionDetailPlanDTO> detailPlans = new ArrayList<>();
        if (rootNode.has("detailPlan") && rootNode.get("detailPlan").isArray()) {
            for (JsonNode detailNode : rootNode.get("detailPlan")) {
                detailPlans.add(mapToDetailPlanDTO(detailNode));
            }
        }

        return PromotionPlanRequestDTO.builder()
                .planName(planName)
                .planContent(planContent)
                .nominationDeadlineAt(nominationDeadlineAt)
                .appointmentAt(appointmentAt)
                .detailPlan(detailPlans)
                .build();
    }

    private PromotionDetailPlanDTO mapToDetailPlanDTO(JsonNode detailNode) {
        Integer departmentId = getInt(detailNode, "departmentId");
        Integer gradeId = getInt(detailNode, "gradeId");
        Integer quotaCount = getInt(detailNode, "quotaCount");

        return PromotionDetailPlanDTO.builder()
                .departmentId(departmentId)
                .gradeId(gradeId)
                .quotaCount(quotaCount)
                .candidateList(new ArrayList<>()) // 빈 리스트로 초기화
                .build();
    }

    private String getText(JsonNode node, String fieldName) {
        return node.has(fieldName) && !node.get(fieldName).isNull() ? node.get(fieldName).asText() : null;
    }

    private Integer getInt(JsonNode node, String fieldName) {
        return node.has(fieldName) && !node.get(fieldName).isNull() ? node.get(fieldName).asInt() : null;
    }
}
