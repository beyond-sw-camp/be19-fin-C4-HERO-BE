package com.c4.hero.domain.promotion.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import com.c4.hero.domain.employee.entity.Grade;
import com.c4.hero.domain.employee.repository.EmployeeDepartmentRepository;
import com.c4.hero.domain.employee.repository.EmployeeGradeRepository;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.employee.service.EmployeeCommandService;
import com.c4.hero.domain.employee.type.ChangeType;
import com.c4.hero.domain.promotion.dto.PromotionDetailPlanDTO;
import com.c4.hero.domain.promotion.dto.request.PromotionNominationRequestDTO;
import com.c4.hero.domain.promotion.dto.request.PromotionPlanRequestDTO;
import com.c4.hero.domain.promotion.dto.request.PromotionReviewRequestDTO;
import com.c4.hero.domain.promotion.entity.PromotionCandidate;
import com.c4.hero.domain.promotion.type.PromotionCandidateStatus;
import com.c4.hero.domain.promotion.entity.PromotionDetail;
import com.c4.hero.domain.promotion.entity.PromotionPlan;
import com.c4.hero.domain.promotion.repotiroy.PromotionCandidateRepository;
import com.c4.hero.domain.promotion.repotiroy.PromotionDetailRepository;
import com.c4.hero.domain.promotion.repotiroy.PromotionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <pre>
 * Class Name: PromotionCommandService
 * Description: 승진 관련 CUD(생성, 수정, 삭제) 비즈니스 로직을 처리하는 서비스
 *
 * History
 * 2025/12/19 (승건) 최초 작성
 * 2025/12/22 (승건) 후보자 추천 및 추천 취소 로직 추가
 * 2025/12/24 (승건) 심사 로직 분리 및 최종 승인 시 직급 변경 로직 추가
 * </pre>
 *
 * @author 승건
 * @version 1.2
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PromotionCommandService {

    private final EmployeeCommandService employeeCommandService;

    private final PromotionPlanRepository promotionPlanRepository;
    private final PromotionDetailRepository promotionDetailRepository;
    private final PromotionCandidateRepository promotionCandidateRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeGradeRepository gradeRepository;
    private final EmployeeDepartmentRepository departmentRepository;

    private final ModelMapper modelMapper;

    /**
     * 새로운 승진 계획을 등록하고, 조건에 맞는 후보자를 자동으로 등록합니다.
     *
     * @param request 등록할 승진 계획 정보
     */
    public void registerPromotionPlan(PromotionPlanRequestDTO request) {
        // 1. 요청 값 유효성 검증
        validatePromotionPlan(request);

        // 2. 승진 계획 엔티티 생성 및 저장
        PromotionPlan promotionPlan = PromotionPlan.builder()
                .planName(request.getPlanName())
                .nominationDeadlineAt(request.getNominationDeadlineAt())
                .appointmentAt(request.getAppointmentAt())
                .planContent(request.getPlanContent())
                .build();
        PromotionPlan savedPlan = promotionPlanRepository.save(promotionPlan);

        // 3. 상세 계획 저장 및 후보자 자동 등록
        for (PromotionDetailPlanDTO detailDTO : request.getDetailPlan()) {
            PromotionDetail promotionDetail = PromotionDetail.builder()
                    .promotionPlan(savedPlan)
                    .departmentId(detailDTO.getDepartmentId())
                    .gradeId(detailDTO.getGradeId())
                    .quotaCount(detailDTO.getQuotaCount())
                    .build();
            PromotionDetail savedDetail = promotionDetailRepository.save(promotionDetail);

            // 4. 조건에 맞는 후보자 자동 등록
            autoRegisterCandidates(savedDetail);
        }
    }

    /**
     * 승진 후보자를 추천합니다.
     *
     * @param nominatorId 추천인 ID
     * @param request     추천 요청 DTO
     */
    public void nominateCandidate(Integer nominatorId, PromotionNominationRequestDTO request) {
        // 1. 후보자 조회
        PromotionCandidate candidate = promotionCandidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMOTION_CANDIDATE_NOT_FOUND));

        // 2. 승진 계획 마감일 체크
        validateNominationPeriod(candidate.getPromotionDetail().getPromotionPlan());

        // 3. 자기 추천 방지
        if (candidate.getEmployee().getEmployeeId().equals(nominatorId)) {
            throw new BusinessException(ErrorCode.PROMOTION_SELF_NOMINATION_NOT_ALLOWED);
        }

        // 4. 추천인 조회
        Employee nominator = employeeRepository.findById(nominatorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        // 5. 추천 정보 업데이트
        candidate.nominate(nominator, request.getNominationReason());
    }

    /**
     * 승진 후보자 추천을 취소합니다.
     *
     * @param candidateId 후보자 ID
     * @param nominatorId 취소 요청자(추천인) ID
     */
    public void cancelNomination(Integer candidateId, Integer nominatorId) {
        // 1. 후보자 조회
        PromotionCandidate candidate = promotionCandidateRepository.findById(candidateId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMOTION_CANDIDATE_NOT_FOUND));

        // 2. 승진 계획 마감일 체크
        validateNominationPeriod(candidate.getPromotionDetail().getPromotionPlan());

        // 3. 권한 체크 (본인이 추천한 건인지)
        if (candidate.getNominator() == null || !candidate.getNominator().getEmployeeId().equals(nominatorId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "본인이 추천한 후보자만 취소할 수 있습니다.");
        }

        // 4. 추천 취소
        candidate.cancelNomination();
    }

    /**
     * 승진 계획 요청 값의 비즈니스 규칙을 검증합니다.
     *
     * @param request 검증할 승진 계획 요청
     */
    private void validatePromotionPlan(PromotionPlanRequestDTO request) {
        // 추천 마감일은 발령일보다 이전이어야 함
        if (request.getNominationDeadlineAt().isAfter(request.getAppointmentAt())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "추천 마감일은 발령일보다 이전이어야 합니다.");
        }
        // 상세 계획은 최소 1개 이상 포함되어야 함
        if (CollectionUtils.isEmpty(request.getDetailPlan())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "승진 상세 계획은 최소 1개 이상 포함되어야 합니다.");
        }
    }

    /**
     * 추천 가능한 기간인지 검증합니다.
     *
     * @param plan 승진 계획
     */
    private void validateNominationPeriod(PromotionPlan plan) {
        LocalDate now = LocalDate.now();
        if (now.isAfter(plan.getNominationDeadlineAt())) {
            throw new BusinessException(ErrorCode.PROMOTION_NOMINATION_PERIOD_EXPIRED, "추천 기간이 마감되었습니다.");
        }
        if (now.isAfter(plan.getAppointmentAt())) {
            throw new BusinessException(ErrorCode.PROMOTION_PLAN_FINISHED, "이미 완료된 승진 계획입니다.");
        }
    }

    /**
     * 승진 상세 계획에 따라 조건에 맞는 후보자를 자동으로 찾아 등록합니다.
     *
     * @param promotionDetail 후보자를 등록할 승진 상세 계획
     */
    private void autoRegisterCandidates(PromotionDetail promotionDetail) {
        Integer targetGradeId = promotionDetail.getGradeId();

        // 1. 모든 직급 정보 조회 및 정렬
        List<Grade> allGrades = gradeRepository.findAll(Sort.by("gradeId"));

        // 2. 승진 대상 직급의 바로 아래 직급과 필요 포인트 탐색
        Grade candidateGrade = null;
        Integer requiredPoint = null;

        for (int i = 0; i < allGrades.size(); i++) {
            Grade currentGrade = allGrades.get(i);
            if (currentGrade.getGradeId().equals(targetGradeId)) {
                requiredPoint = currentGrade.getRequiredPoint();
                if (i > 1) { // index 0(관리자), 1(사원)은 승진 후보가 될 수 없음
                    candidateGrade = allGrades.get(i - 1);
                }
                break;
            }
        }

        // 3. 예외 처리
        if (requiredPoint == null) {
            throw new BusinessException(ErrorCode.GRADE_NOT_FOUND, "승진 대상 직급 정보를 찾을 수 없습니다.");
        }
        if (candidateGrade == null) {
            throw new BusinessException(ErrorCode.INVALID_PROMOTION_TARGET_GRADE, "해당 직급으로는 승진 계획을 생성할 수 없습니다.");
        }

        // 4. 대상 부서 및 모든 하위 부서 ID 조회
        List<Integer> departmentIds = findAllSubDepartmentIds(promotionDetail.getDepartmentId());

        // 5. 조건에 맞는 승진 후보 직원 조회
        List<Employee> candidates = employeeRepository.findPromotionCandidates(
                departmentIds,
                candidateGrade.getGradeId(),
                requiredPoint
        );

        // 6. 조회된 후보자들을 PromotionCandidate 엔티티로 변환하여 저장
        if (!CollectionUtils.isEmpty(candidates)) {
            List<PromotionCandidate> promotionCandidates = candidates.stream()
                    .map(employee -> PromotionCandidate.builder()
                            .promotionDetail(promotionDetail)
                            .employee(employee)
                            .evaluationPoint(employee.getEvaluationPoint())
                            .status(PromotionCandidateStatus.WAITING)
                            .build())
                    .collect(Collectors.toList());
            promotionCandidateRepository.saveAll(promotionCandidates);
        }
    }

    /**
     * 지정된 부서 ID와 그 아래 모든 하위 부서의 ID 목록을 재귀적으로 조회합니다.
     *
     * @param departmentId 최상위 부서 ID
     * @return 자기 자신을 포함한 모든 하위 부서 ID 목록
     */
    private List<Integer> findAllSubDepartmentIds(Integer departmentId) {
        List<Integer> allSubDepartments = new ArrayList<>();
        allSubDepartments.add(departmentId); // 자기 자신 포함

        List<EmployeeDepartment> directChildren = departmentRepository.findByParentDepartmentId(departmentId);
        List<Integer> directChildrenIds = directChildren.stream()
                .map(EmployeeDepartment::getDepartmentId)
                .toList();

        for (Integer childId : directChildrenIds) {
            allSubDepartments.addAll(findAllSubDepartmentIds(childId));
        }

        return allSubDepartments;
    }

    /**
     * 승진 후보자를 1차 심사합니다. (승인 또는 반려)
     * 대기(WAITING) 상태인 후보자만 처리 가능합니다.
     *
     * @param request 심사 요청 정보
     */
    public void reviewCandidate(PromotionReviewRequestDTO request) {
        // 1. 후보자 조회
        PromotionCandidate candidate = promotionCandidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMOTION_CANDIDATE_NOT_FOUND));

        // 2. 상태 체크 (대기 상태가 아니면 예외 발생)
        if (candidate.getStatus() != PromotionCandidateStatus.WAITING) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 심사가 완료된 후보자입니다.");
        }

        // 3. 승인(심사 통과) 요청인 경우 TO 체크
        if (Boolean.TRUE.equals(request.getIsPassed())) {
            PromotionDetail detail = candidate.getPromotionDetail();

            // 현재 통과된(심사 통과 + 최종 승인) 인원 수 조회
            long passedCount = promotionCandidateRepository.countByPromotionDetailAndStatusIn(
                    detail,
                    List.of(PromotionCandidateStatus.REVIEW_PASSED, PromotionCandidateStatus.FINAL_APPROVED)
            );

            if (passedCount >= detail.getQuotaCount()) {
                throw new BusinessException(ErrorCode.PROMOTION_QUOTA_EXCEEDED);
            }
        }

        // 4. 심사 결과 반영
        candidate.review(request.getIsPassed(), request.getComment());
    }

    /**
     * 승진 후보자를 최종 승인합니다.
     * 심사 통과(REVIEW_PASSED) 상태인 후보자만 처리 가능합니다.
     * 최종 승인 시 해당 직원의 직급이 실제로 변경됩니다.
     *
     * @param request 심사 요청 정보
     */
    public void confirmFinalApproval(PromotionReviewRequestDTO request) {
        // 1. 후보자 조회
        PromotionCandidate candidate = promotionCandidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMOTION_CANDIDATE_NOT_FOUND));

        // 2. 상태 체크 (심사 통과 상태가 아니면 예외 발생)
        if (candidate.getStatus() != PromotionCandidateStatus.REVIEW_PASSED) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "1차 심사를 통과한 후보자만 최종 승인할 수 있습니다.");
        }

        // 3. 최종 승인 또는 반려 처리 (상태 변경)
        candidate.confirmFinalApproval(request.getIsPassed(), request.getComment());

        // 4. 승인인 경우 실제 직급 변경
        if (Boolean.TRUE.equals(request.getIsPassed())) {
            Integer targetGradeId = candidate.getPromotionDetail().getGradeId();
            Grade newGrade = gradeRepository.findById(targetGradeId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.GRADE_NOT_FOUND));


            candidate.getEmployee().changeGrade(newGrade);
            employeeCommandService.addGradeHistory(candidate.getEmployee(), ChangeType.PROMOTION, candidate.getEmployee().getGrade().getGrade());
        }
    }

}
