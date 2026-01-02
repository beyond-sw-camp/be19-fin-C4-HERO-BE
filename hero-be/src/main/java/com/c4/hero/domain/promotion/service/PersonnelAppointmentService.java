package com.c4.hero.domain.promotion.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.employee.entity.Grade;
import com.c4.hero.domain.employee.repository.EmployeeGradeRepository;
import com.c4.hero.domain.employee.service.EmployeeCommandService;
import com.c4.hero.domain.promotion.dto.request.PromotionReviewRequestDTO;
import com.c4.hero.domain.promotion.entity.PromotionCandidate;
import com.c4.hero.domain.promotion.repository.PromotionCandidateRepository;
import com.c4.hero.domain.promotion.type.PromotionCandidateStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * <pre>
 * Class Name: PersonnelAppointmentService
 * Description: 인사 발령 처리 로직을 담당하는 서비스
 *
 * History
 * 2025/12/31 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonnelAppointmentService {

    private final PromotionCommandService promotionCommandService;
    private final EmployeeCommandService employeeCommandService;
    private final EmployeeGradeRepository gradeRepository;
    private final PromotionCandidateRepository promotionCandidateRepository;

    /**
     * 인사 발령 상세 내용을 기반으로 실제 시스템에 반영합니다.
     *
     * @param details 발령 상세 내용 (JSON Map)
     */
    @Transactional
    public void processAppointment(Map<String, Object> details) {
        String promotionType = (String) details.get("changeType");
        String employeeNumber = (String) details.get("employeeNumber");

        if (employeeNumber == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "employeeNumber is missing");
        }

        // 1. 부서 변경 처리
        String departmentBefore = (String) details.get("departmentBefore");
        String departmentAfter = (String) details.get("departmentAfter");

        if (departmentAfter != null && !departmentAfter.isEmpty() && !departmentAfter.equals(departmentBefore)) {
            employeeCommandService.updateDepartment(employeeNumber, departmentAfter);
            log.info("   - 부서 변경: {} -> {}", departmentBefore, departmentAfter);
        }

        // 2. 직책 변경 처리
        String jobTitleBefore = (String) details.get("jobTitleBefore");
        String jobTitleAfter = (String) details.get("jobTitleAfter");

        if (jobTitleAfter != null && !jobTitleAfter.isEmpty() && !jobTitleAfter.equals(jobTitleBefore)) {
            employeeCommandService.updateJobTitle(employeeNumber, jobTitleAfter);
            log.info("   - 직책 변경: {} -> {}", jobTitleBefore, jobTitleAfter);
        }

        // 3. 승진(직급 변경) 처리
        if ("특별승진".equals(promotionType)) {
            String gradeBeforeName = (String) details.get("gradeBefore");
            String gradeAfterName = (String) details.get("gradeAfter");
            String reason = (String) details.get("reason");

            if (gradeAfterName != null && !gradeAfterName.equals(gradeBeforeName)) {
                Optional<Grade> gradeOptional = gradeRepository.findByGrade(gradeAfterName);
                if (gradeOptional.isEmpty()) {
                    throw new BusinessException(ErrorCode.GRADE_NOT_FOUND);
                }
                Integer targetGradeId = gradeOptional.get().getGradeId();
                promotionCommandService.confirmDirectPromotion(employeeNumber, targetGradeId, reason);
                log.info("   - 특별 승진: {} -> {}", gradeBeforeName, gradeAfterName);
            }

        } else if ("승진".equals(promotionType)) {
            Integer candidateId = promotionCandidateRepository.findByEmployee_EmployeeNumberAndStatus(employeeNumber, PromotionCandidateStatus.REVIEW_PASSED)
                    .map(PromotionCandidate::getCandidateId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PROMOTION_CANDIDATE_NOT_FOUND));

            PromotionReviewRequestDTO requestDTO = PromotionReviewRequestDTO.builder()
                    .candidateId(candidateId)
                    .isPassed(true)
                    .build();

            promotionCommandService.confirmFinalApproval(requestDTO);
            log.info("   - 정기 승진 확정");
        }
    }
}
