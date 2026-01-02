package com.c4.hero.domain.promotion.event;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.approval.event.ApprovalCompletedEvent;
import com.c4.hero.domain.approval.event.ApprovalRejectedEvent;
import com.c4.hero.domain.promotion.dto.request.PromotionReviewRequestDTO;
import com.c4.hero.domain.promotion.entity.PersonnelAppointment;
import com.c4.hero.domain.promotion.entity.PromotionCandidate;
import com.c4.hero.domain.promotion.repository.PersonnelAppointmentRepository;
import com.c4.hero.domain.promotion.repository.PromotionCandidateRepository;
import com.c4.hero.domain.promotion.service.PersonnelAppointmentService;
import com.c4.hero.domain.promotion.service.PromotionCommandService;
import com.c4.hero.domain.promotion.type.PromotionCandidateStatus;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersonnelAppointmentEventListener {
    private final PromotionCommandService promotionCommandService;
    private final PersonnelAppointmentService personnelAppointmentService;
    private final PromotionCandidateRepository promotionCandidateRepository;
    private final PersonnelAppointmentRepository personnelAppointmentRepository;
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
            String promotionType = (String) details.get("changeType");
            String employeeNumber = (String) details.get("employeeNumber");
            String appointmentDateStr = (String) details.get("effectiveDate");

            if (employeeNumber == null || appointmentDateStr == null) {
                log.error("❌ 인사발령 처리 실패 - 필수 정보 누락. docId: {}", event.getDocId());
                return;
            }

            LocalDate appointmentDate = LocalDate.parse(appointmentDateStr);
            LocalDate today = LocalDate.now();

            // 2. 발령일이 오늘 또는 과거이면 즉시 처리
            if (!appointmentDate.isAfter(today)) {
                log.info("발령일이 오늘 또는 과거이므로 즉시 처리합니다. - employeeNumber: {}, date: {}", employeeNumber, appointmentDate);
                personnelAppointmentService.processAppointment(details);
                
                // 이력 관리를 위해 COMPLETED 상태로 저장
                PersonnelAppointment appointment = PersonnelAppointment.builder()
                        .docId(event.getDocId())
                        .employeeNumber(employeeNumber)
                        .appointmentDate(appointmentDate)
                        .changeType(promotionType != null ? promotionType : "GENERAL")
                        .details(event.getDetails())
                        .status("COMPLETED") // 즉시 처리되었으므로 COMPLETED
                        .build();
                personnelAppointmentRepository.save(appointment);
                appointment.complete(); // 처리 시각 기록

            } else {
                // 3. 발령일이 미래이면 예약
                log.info("발령일이 미래이므로 예약을 저장합니다. - employeeNumber: {}, date: {}", employeeNumber, appointmentDate);
                PersonnelAppointment appointment = PersonnelAppointment.builder()
                        .docId(event.getDocId())
                        .employeeNumber(employeeNumber)
                        .appointmentDate(appointmentDate)
                        .changeType(promotionType != null ? promotionType : "GENERAL")
                        .details(event.getDetails())
                        .status("WAITING")
                        .build();
                personnelAppointmentRepository.save(appointment);
            }

        } catch (Exception e) {
            log.error("❌ 인사발령 예약/처리 중 오류 발생 - docId: {}", event.getDocId(), e);
            throw new RuntimeException("인사발령 예약/처리 중 오류 발생", e);
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
            String promotionType = (String) details.get("changeType");

            if ("특별승진".equals(promotionType)) {
                // 특별 승진 반려 - 별도 처리 필요 없음 (DB에 남는 데이터가 없으므로)
                log.info("ℹ️ 특별 승진 결재 반려됨 - 별도 처리 없음");
            } else if ("승진".equals(promotionType)) {
                // 정기 승진 반려 - 후보자 상태 변경 필요
                String employeeNumber = (String) details.get("employeeNumber");
                if (employeeNumber != null) {
                    Integer candidateId = promotionCandidateRepository.findByEmployee_EmployeeNumberAndStatus(employeeNumber, PromotionCandidateStatus.REVIEW_PASSED)
                            .map(PromotionCandidate::getCandidateId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.PROMOTION_CANDIDATE_NOT_FOUND));

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
