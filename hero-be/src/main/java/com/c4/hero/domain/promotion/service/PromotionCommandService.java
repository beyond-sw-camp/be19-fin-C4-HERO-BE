package com.c4.hero.domain.promotion.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.common.util.DateUtil;
import com.c4.hero.domain.approval.dto.ApprovalLineDTO;
import com.c4.hero.domain.approval.dto.request.ApprovalRequestDTO;
import com.c4.hero.domain.approval.entity.ApprovalTemplate;
import com.c4.hero.domain.approval.repository.ApprovalTemplateRepository;
import com.c4.hero.domain.approval.service.ApprovalCommandService;
import com.c4.hero.domain.auth.security.CustomUserDetails;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import com.c4.hero.domain.employee.entity.Grade;
import com.c4.hero.domain.employee.repository.EmployeeDepartmentRepository;
import com.c4.hero.domain.employee.repository.EmployeeGradeRepository;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.employee.service.EmployeeCommandService;
import com.c4.hero.domain.employee.type.ChangeType;
import com.c4.hero.domain.promotion.dto.PromotionDetailPlanDTO;
import com.c4.hero.domain.promotion.dto.request.DirectPromotionRequestDTO;
import com.c4.hero.domain.promotion.dto.request.PromotionNominationRequestDTO;
import com.c4.hero.domain.promotion.dto.request.PromotionPlanRequestDTO;
import com.c4.hero.domain.promotion.dto.request.PromotionReviewRequestDTO;
import com.c4.hero.domain.promotion.entity.PromotionCandidate;
import com.c4.hero.domain.promotion.type.PromotionCandidateStatus;
import com.c4.hero.domain.promotion.entity.PromotionDetail;
import com.c4.hero.domain.promotion.entity.PromotionPlan;
import com.c4.hero.domain.promotion.repository.PromotionCandidateRepository;
import com.c4.hero.domain.promotion.repository.PromotionDetailRepository;
import com.c4.hero.domain.promotion.repository.PromotionPlanRepository;
import com.c4.hero.domain.settings.entity.SettingsApprovalLine;
import com.c4.hero.domain.settings.repository.SettingsApprovalLineRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * 2025/12/27 (승건) 1차 심사 통과 시 결재 상신 로직 추가 (기본 결재선 적용)
 * 2025/12/28 (승건) 즉시 승진 로직 추가
 * </pre>
 *
 * @author 승건
 * @version 1.4
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PromotionCommandService {

    private final EmployeeCommandService employeeCommandService;
    private final PromotionPlanRepository promotionPlanRepository;
    private final PromotionDetailRepository promotionDetailRepository;
    private final PromotionCandidateRepository promotionCandidateRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeGradeRepository gradeRepository;
    private final EmployeeDepartmentRepository departmentRepository;
    private final ApprovalCommandService approvalCommandService;
    private final ObjectMapper objectMapper;
    private final ApprovalTemplateRepository templateRepository;
    private final SettingsApprovalLineRepository settingsApprovalLineRepository;

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
     * @param userDetails 현재 로그인한 사용자 정보
     * @param request 심사 요청 정보
     */
    public void reviewCandidate(CustomUserDetails userDetails, PromotionReviewRequestDTO request) {
        // 1. 후보자 조회
        PromotionCandidate candidate = promotionCandidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMOTION_CANDIDATE_NOT_FOUND));

        // 2. 상태 체크 (대기 상태가 아니면 예외 발생)
        if (candidate.getStatus() != PromotionCandidateStatus.WAITING) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 심사가 완료된 후보자입니다.");
        }

        // 3. 승인(심사 통과) 요청인 경우
        if (Boolean.TRUE.equals(request.getIsPassed())) {
            // 3-1. TO 체크
            PromotionDetail detail = candidate.getPromotionDetail();

            // 현재 통과된(심사 통과 + 최종 승인) 인원 수 조회
            long passedCount = promotionCandidateRepository.countByPromotionDetailAndStatusIn(
                    detail,
                    List.of(PromotionCandidateStatus.REVIEW_PASSED, PromotionCandidateStatus.FINAL_APPROVED)
            );
            if (passedCount >= detail.getQuotaCount()) {
                throw new BusinessException(ErrorCode.PROMOTION_QUOTA_EXCEEDED);
            }

            // 3-2. 결재 상신
            try {
                createRegularPromotionApproval(userDetails, candidate);
            } catch (JsonProcessingException e) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "결재 상신 데이터 생성에 실패했습니다.");
            }
        }

        // 4. 심사 결과 반영
        candidate.review(request.getIsPassed(), request.getComment());
    }

    /**
     * 정기 승진 심사를 위한 결재 문서를 생성합니다.
     *
     * @param userDetails 기안자 정보
     * @param candidate 승진 후보자 정보
     * @throws JsonProcessingException JSON 변환 실패 시
     */
    private void createRegularPromotionApproval(CustomUserDetails userDetails, PromotionCandidate candidate) throws JsonProcessingException {
        LocalDateTime now = LocalDateTime.now();
        PromotionPlan plan = candidate.getPromotionDetail().getPromotionPlan();
        Employee employee = candidate.getEmployee();

        if(employee.getEmployeeId() == 1) {
            log.info("관리자의 직급은 변경 불가능 합니다.");
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "관리자의 직급은 변경 불가능 합니다.");
        }

        Integer targetGradeId = candidate.getPromotionDetail().getGradeId();
        Grade newGrade = gradeRepository.findById(targetGradeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GRADE_NOT_FOUND));

        // 결재선 설정
        List<ApprovalLineDTO> approvalLines = createApprovalLines(userDetails);

        // 상세 정보(details) JSON 생성
        Map<String, Object> detailsMap = new HashMap<>();
        detailsMap.put("promotionType", "REGULAR");
        detailsMap.put("candidateId", candidate.getCandidateId());
        detailsMap.put("changeType", "승진");
        detailsMap.put("employeeId", employee.getEmployeeId());
        detailsMap.put("employeeName", employee.getEmployeeName());
        detailsMap.put("effectiveDate", plan.getAppointmentAt().toString());
        detailsMap.put("auditTrail", now.format(DateUtil.YYYY_MM_DD));
        detailsMap.put("departmentBefore", employee.getEmployeeDepartment().getDepartmentName());
        detailsMap.put("departmentAfter", employee.getEmployeeDepartment().getDepartmentName());
        detailsMap.put("gradeBefore", employee.getGrade().getGrade());
        detailsMap.put("gradeAfter", newGrade.getGrade());
        detailsMap.put("jobtitleBefore", employee.getJobTitle().getJobTitle());
        detailsMap.put("jobtitleAfter", employee.getJobTitle().getJobTitle());
        detailsMap.put("status", "재직");
        detailsMap.put("reason", candidate.getRejectionReason());
        String detailsJson = objectMapper.writeValueAsString(detailsMap);

        // 결재 요청 DTO 생성
        ApprovalRequestDTO approvalRequest = ApprovalRequestDTO.builder()
                .formType("personnelappointment")
                .documentType("인사")
                .title(plan.getPlanName() + " - " + employee.getEmployeeName())
                .drafter(userDetails.getEmployeeName())
                .department(userDetails.getDepartmentName())
                .grade(userDetails.getGradeName())
                .draftDate(now.format(DateUtil.YYYY_MM_DD))
                .submittedAt(now.format(DateUtil.YYYY_MM_DD_HH_MM_SS))
                .details(detailsJson)
                .lines(approvalLines)
                .build();

        // 결재 문서 생성 호출
        approvalCommandService.createDocument(userDetails.getEmployeeId(), approvalRequest, null, "DRAFT");
    }

    /**
     * 특정 직원을 즉시 승진시키기 위한 결재를 상신합니다.
     *
     * @param userDetails 기안자 정보
     * @param request 즉시 승진 요청 정보
     */
    public void promoteDirectly(CustomUserDetails userDetails, DirectPromotionRequestDTO request) {
        LocalDateTime now = LocalDateTime.now();
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));
        Grade newGrade = gradeRepository.findById(request.getTargetGradeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.GRADE_NOT_FOUND));

        if(employee.getEmployeeId() == 1) {
            log.info("관리자의 직급은 변경 불가능 합니다.");
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "관리자의 직급은 변경 불가능 합니다.");
        }

        // 결재선 설정
        List<ApprovalLineDTO> approvalLines = createApprovalLines(userDetails);
        // 상세 정보(details) JSON 생성
        Map<String, Object> detailsMap = new HashMap<>();
        detailsMap.put("promotionType", "SPECIAL");
        detailsMap.put("changeType", "특별승진");
        detailsMap.put("employeeId", employee.getEmployeeId());
        detailsMap.put("employeeName", employee.getEmployeeName());
        detailsMap.put("effectiveDate", now.toLocalDate().toString()); // 발령일은 즉시
        detailsMap.put("auditTrail", now.format(DateUtil.YYYY_MM_DD));
        detailsMap.put("departmentBefore", employee.getEmployeeDepartment().getDepartmentName());
        detailsMap.put("departmentAfter", employee.getEmployeeDepartment().getDepartmentName());
        detailsMap.put("gradeBefore", employee.getGrade().getGrade());
        detailsMap.put("gradeAfter", newGrade.getGrade());
        detailsMap.put("jobtitleBefore", employee.getJobTitle().getJobTitle());
        detailsMap.put("jobtitleAfter", employee.getJobTitle().getJobTitle());
        detailsMap.put("status", "재직");
        detailsMap.put("reason", request.getReason()); // 특별 승진 사유
        String detailsJson;

        detailsJson = objectMapper.writeValueAsString(detailsMap);

        // 결재 요청 DTO 생성
        ApprovalRequestDTO approvalRequest = ApprovalRequestDTO.builder()
                .formType("personnelappointment")
                .documentType("인사")
                .title("[특별승진] " + employee.getEmployeeName() + " " + newGrade.getGrade() + " 승진 건")
                .drafter(userDetails.getEmployeeName())
                .department(userDetails.getDepartmentName())
                .grade(userDetails.getGradeName())
                .draftDate(now.format(DateUtil.YYYY_MM_DD))
                .submittedAt(now.format(DateUtil.YYYY_MM_DD_HH_MM_SS))
                .details(detailsJson)
                .lines(approvalLines)
                .build();
        // 결재 문서 생성 호출
        approvalCommandService.createDocument(userDetails.getEmployeeId(), approvalRequest, null, "DRAFT");
    }

    /**
     * 결재선을 생성합니다. (기본 결재선 우선)
     *
     * @param userDetails 기안자 정보
     * @return 생성된 결재선 목록
     */
    private List<ApprovalLineDTO> createApprovalLines(CustomUserDetails userDetails) {
        List<ApprovalLineDTO> approvalLines = new ArrayList<>();
        approvalLines.add(ApprovalLineDTO.builder().seq(1).approverId(userDetails.getEmployeeId()).build()); // 기안자

        ApprovalTemplate template = templateRepository.findByTemplateKey("personnelappointment");
        if (template != null) {
            List<SettingsApprovalLine> defaultLines = settingsApprovalLineRepository.findByTemplate_TemplateId(template.getTemplateId());
            defaultLines.sort(Comparator.comparing(SettingsApprovalLine::getSeq));

            if (!defaultLines.isEmpty()) {
                for (SettingsApprovalLine line : defaultLines) {
                    if (line.getSeq() > 1) {
                        Integer approverId = departmentRepository.findById(line.getDepartmentId())
                                .map(EmployeeDepartment::getManagerId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND, "기본 결재선의 부서 또는 부서장을 찾을 수 없습니다."));
                        approvalLines.add(ApprovalLineDTO.builder().seq(line.getSeq()).approverId(approverId).build());
                    }
                }
                return approvalLines;
            }
        }

        // 기본 결재선이 없으면 기안자의 부서장으로 설정
        Integer approverId = 1; // 기본값: 관리자
        if (userDetails.getDepartmentId() != null) {
            EmployeeDepartment department = departmentRepository.findById(userDetails.getDepartmentId()).orElse(null);
            if (department != null && department.getManagerId() != null && !department.getManagerId().equals(userDetails.getEmployeeId())) {
                approverId = department.getManagerId();
            }
        }
        approvalLines.add(ApprovalLineDTO.builder().seq(2).approverId(approverId).build());
        return approvalLines;
    }

    /**
     * 승진 후보자를 최종 승인합니다. (정기 승진)
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

    /**
     * 특별 승진을 최종 확정합니다.
     *
     * @param employeeId 승진 대상 직원 ID
     * @param targetGradeId 목표 직급 ID
     * @param reason 승진 사유
     */
    public void confirmDirectPromotion(Integer employeeId, Integer targetGradeId, String reason) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));
        Grade newGrade = gradeRepository.findById(targetGradeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GRADE_NOT_FOUND));

        employee.changeGrade(newGrade);
        employeeCommandService.addGradeHistory(employee, ChangeType.PROMOTION, newGrade.getGrade());
    }
}
