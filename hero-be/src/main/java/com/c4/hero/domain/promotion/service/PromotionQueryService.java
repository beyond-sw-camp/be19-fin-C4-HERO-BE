package com.c4.hero.domain.promotion.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import com.c4.hero.domain.employee.repository.EmployeeDepartmentRepository;
import com.c4.hero.domain.employee.repository.EmployeeGradeRepository;
import com.c4.hero.domain.promotion.dto.PromotionDepartmentDTO;
import com.c4.hero.domain.promotion.dto.PromotionGradeDTO;
import com.c4.hero.domain.promotion.dto.response.PromotionOptionsResponseDTO;
import com.c4.hero.domain.promotion.dto.response.PromotionPlanDetailResponseDTO;
import com.c4.hero.domain.promotion.dto.response.PromotionPlanForReviewResponseDTO;
import com.c4.hero.domain.promotion.dto.response.PromotionPlanResponseDTO;
import com.c4.hero.domain.promotion.mapper.PromotionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 * Class Name: PromotionQueryService
 * Description: 승진 관련 조회 비즈니스 로직을 처리하는 서비스
 *
 * History
 * 2025/12/19 (승건) 최초 작성
 * 2025/12/22 (승건) 추천 가능 승진 계획 조회 로직 추가
 * 2025/12/23 (승건) 심사용 승진 계획 상세 조회 로직 추가
 * 2025/12/24 (승건) 심사용 승진 계획 목록 조회 로직 추가
 * </pre>
 *
 * @author 승건
 * @version 1.3
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionQueryService {

    private final PromotionMapper promotionMapper;
    private final EmployeeDepartmentRepository departmentRepository;
    private final EmployeeGradeRepository gradeRepository;

    /**
     * 승진 계획 목록을 페이징하여 조회합니다.
     *
     * @param isFinished 조회할 계획의 완료 여부 (true: 완료, false: 진행중, null: 전체)
     * @param pageable   페이징 정보 (page, size, sort)
     * @return 페이징된 승진 계획 목록
     */
    public PageResponse<PromotionPlanResponseDTO> getPromotionPlan(Boolean isFinished, Pageable pageable) {
        // 1. 조건에 맞는 전체 데이터 개수 조회
        long totalElements = promotionMapper.countPromotionPlan(isFinished);

        // 2. 데이터가 없으면 빈 페이지 응답 반환
        if (totalElements == 0) {
            return PageResponse.of(Collections.emptyList(), pageable.getPageNumber(), pageable.getPageSize(), 0);
        }

        // 3. 페이징된 데이터 목록 조회
        List<PromotionPlanResponseDTO> content = promotionMapper.selectPromotionPlan(
                isFinished,
                (int) pageable.getOffset(),
                pageable.getPageSize()
        );

        // 4. PageResponse 객체를 생성하여 반환
        return PageResponse.of(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements);
    }

    /**
     * 승진 계획의 상세 정보를 조회합니다.
     *
     * @param promotionId 조회할 승진 계획의 ID
     * @return 승진 계획 상세 정보
     * @throws BusinessException 승진 계획을 찾지 못했을 경우
     */
    public PromotionPlanDetailResponseDTO getPromotionPlanDetail(int promotionId) {
        // 1. Mapper를 통해 상세 정보 조회
        PromotionPlanDetailResponseDTO response = promotionMapper.selectPromotionPlanDetail(promotionId);

        // 2. 조회 결과가 없으면 예외 발생
        if (response == null) {
            throw new BusinessException(ErrorCode.PROMOTION_PLAN_NOT_FOUND);
        }

        return response;
    }

    /**
     * 승진 계획 등록 시 선택 가능한 옵션(부서 트리, 직급 목록)을 조회합니다.
     *
     * @return 승진 계획 옵션 정보
     */
    public PromotionOptionsResponseDTO getPromotionOptions() {
        // 1. 부서 트리 조회
        List<PromotionDepartmentDTO> departmentTree = getDepartmentTree();

        // 2. 직급 목록 조회
        List<PromotionGradeDTO> gradeList = getGradeList();

        return PromotionOptionsResponseDTO.builder()
                .promotionDepartmentDTOList(departmentTree)
                .promotionGradeDTOList(gradeList)
                .build();
    }

    /**
     * 모든 부서를 조회하여 트리 구조로 변환합니다.
     * (발령 대기, 관리자 부서는 제외)
     */
    private List<PromotionDepartmentDTO> getDepartmentTree() {
        // 1. 모든 부서 조회 후, 불필요한 부서(-1, 0) 필터링
        List<EmployeeDepartment> allDepartments = departmentRepository.findAll(Sort.by("departmentId"))
                .stream()
                .filter(dept -> dept.getDepartmentId() > 0)
                .collect(Collectors.toList());

        // 2. DTO 변환 및 Map에 저장 (ID -> DTO)
        Map<Integer, PromotionDepartmentDTO> dtoMap = new HashMap<>();
        for (EmployeeDepartment dept : allDepartments) {
            dtoMap.put(dept.getDepartmentId(), PromotionDepartmentDTO.builder()
                    .departmentId(dept.getDepartmentId())
                    .departmentName(dept.getDepartmentName())
                    .build());
        }

        List<PromotionDepartmentDTO> roots = new ArrayList<>();

        // 3. 트리 구조 조립
        for (EmployeeDepartment dept : allDepartments) {
            PromotionDepartmentDTO currentDTO = dtoMap.get(dept.getDepartmentId());
            Integer parentId = dept.getParentDepartmentId();

            if (parentId == null) {
                // 최상위 부서
                roots.add(currentDTO);
            } else {
                // 하위 부서 -> 부모의 children에 추가
                PromotionDepartmentDTO parentDTO = dtoMap.get(parentId);
                if (parentDTO != null) {
                    parentDTO.addChild(currentDTO);
                }
            }
        }

        return roots;
    }

    /**
     * 모든 직급을 조회하여 DTO 리스트로 변환합니다.
     * (관리자, 최하위 직급 제외)
     */
    private List<PromotionGradeDTO> getGradeList() {
        return gradeRepository.findAll(Sort.by("gradeId")).stream()
                .skip(2) // ID가 가장 작은 2개(관리자, 사원) 제외
                .map(grade -> PromotionGradeDTO.builder()
                        .gradeId(grade.getGradeId())
                        .grade(grade.getGrade())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 팀장이 추천할 수 있는 승진 계획 목록을 조회합니다.
     * 자신의 부서 및 모든 하위 부서에 속한 직원이 후보 대상인 계획만 조회합니다.
     *
     * @param departmentId 팀장의 부서 ID
     * @return 추천 가능한 승진 계획 목록
     */
    public List<PromotionPlanResponseDTO> getRecommendPromotionPlan(Integer departmentId) {
        // 1. 현재 부서 및 모든 하위 부서 ID 목록 조회
        List<Integer> departmentIds = findAllSubDepartmentIds(departmentId);

        // 2. 부서 목록이 비어있으면 빈 리스트 반환
        if (departmentIds.isEmpty()) {
            throw new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND);
        }

        // 3. Mapper를 통해 추천 가능한 승진 계획 조회
        return promotionMapper.selectRecommendPromotionPlan(departmentIds);
    }

    /**
     * 팀장이 추천할 수 있는 승진 계획의 상세 정보를 조회합니다.
     * 자신의 부서 및 모든 하위 부서에 속한 후보자만 포함하여 조회합니다.
     *
     * @param promotionId  승진 계획 ID
     * @param departmentId 팀장의 부서 ID
     * @return 필터링된 승진 계획 상세 정보
     */
    public PromotionPlanDetailResponseDTO getRecommendPromotionPlanDetail(Integer promotionId, Integer departmentId) {
        // 1. 현재 부서 및 모든 하위 부서 ID 목록 조회
        List<Integer> departmentIds = findAllSubDepartmentIds(departmentId);

        // 2. 부서 목록이 비어있으면 예외 처리 (혹은 빈 결과 반환)
        if (departmentIds.isEmpty()) {
            throw new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND);
        }

        // 3. Mapper를 통해 필터링된 상세 정보 조회
        PromotionPlanDetailResponseDTO response = promotionMapper.selectRecommendPromotionPlanDetail(promotionId, departmentIds);

        // 4. 조회 결과가 없으면 예외 발생 (해당 계획에 내 부서원이 한 명도 없는 경우 등)
        if (response == null) {
            throw new BusinessException(ErrorCode.PROMOTION_PLAN_NOT_FOUND);
        }

        return response;
    }

    /**
     * 주어진 부서 ID를 포함한 모든 하위 부서의 ID 목록을 재귀적으로 조회합니다.
     *
     * @param parentDepartmentId 시작 부서 ID
     * @return 해당 부서 및 모든 하위 부서 ID 목록
     */
    private List<Integer> findAllSubDepartmentIds(Integer parentDepartmentId) {
        List<Integer> allDepartmentIds = new ArrayList<>();
        if (parentDepartmentId != null) {
            allDepartmentIds.add(parentDepartmentId);
            // 직속 하위 부서 목록 조회
            List<EmployeeDepartment> subDepartments = departmentRepository.findByParentDepartmentId(parentDepartmentId);
            // 각 하위 부서에 대해 재귀적으로 탐색
            for (EmployeeDepartment subDept : subDepartments) {
                allDepartmentIds.addAll(findAllSubDepartmentIds(subDept.getDepartmentId()));
            }
        }
        return allDepartmentIds;
    }

    /**
     * 심사용 승진 계획 상세 정보를 조회합니다. (승인 현황 포함)
     *
     * @param promotionId 승진 계획 ID
     * @return 심사용 승진 계획 상세 정보
     */
    public PromotionPlanForReviewResponseDTO getPromotionDetailForReview(Integer promotionId) {
        // Mapper를 통해 조회 (Mapper XML에서 approvedCount 계산 필요)
        PromotionPlanForReviewResponseDTO response = promotionMapper.selectPromotionDetailForReview(promotionId);

        if (response == null) {
            throw new BusinessException(ErrorCode.PROMOTION_PLAN_NOT_FOUND);
        }

        return response;
    }

    /**
     * 심사 가능한(추천 마감일이 지난) 승진 계획 목록을 조회합니다.
     *
     * @return 심사 대상 승진 계획 목록
     */
    public List<PromotionPlanResponseDTO> getPromotionPlansForReviewList() {
        return promotionMapper.selectPromotionPlanForReviewList();
    }
}
