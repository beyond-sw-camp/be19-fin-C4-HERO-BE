package com.c4.hero.domain.payroll.policy.dto.response;

import com.c4.hero.domain.payroll.common.type.ItemType;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Class Name : PolicyDetailResponse
 * Description : 급여 정책 상세 조회 응답 DTO
 *
 * 용도
 *  - 급여 정책 상세 화면/API에서 필요한 모든 정보를 한 번에 반환
 *  - 정책 기본 정보, 공통 설정, 항목 정책(수당/공제) 및 적용 대상 정보를 포함
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record PolicyDetailResponseDTO(
        /** 급여 정책 기본 정보 */
        PolicyResponseDTO policy,

        /** 정책 공통 설정 목록 */
        List<PolicyConfigResponseDTO> configs,

        /** 항목 정책 목록 (수당/공제별 그룹) */
        Map<ItemType, List<ItemPolicyWithTargetsResponseDTO>> items
) {}
