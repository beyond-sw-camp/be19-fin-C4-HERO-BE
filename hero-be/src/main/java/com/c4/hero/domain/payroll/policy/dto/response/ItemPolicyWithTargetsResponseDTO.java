package com.c4.hero.domain.payroll.policy.dto.response;

import java.util.List;

/**
 * <pre>
 * Class Name : ItemPolicyWithTargetsResponse
 * Description : 급여 항목 정책(Item Policy)과 적용 대상(Target)을 함께 반환하는 조회 응답 DTO
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record ItemPolicyWithTargetsResponseDTO(
        /** 항목 정책 정보 */
        ItemPolicyResponseDTO item,

        /** 항목 정책에 연결된 적용 대상 목록 */
        List<ItemPolicyTargetResponseDTO> targets
) {}
