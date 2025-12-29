package com.c4.hero.domain.payroll.policy.dto.response;

/**
 * <pre>
 * Class Name : PolicyConfigResponse
 * Description : 급여 정책 공통 설정 조회 응답 DTO
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record PolicyConfigResponseDTO(

        /** 설정 키 (예: TAX_RATE, INSURANCE_APPLY_YN 등) */
        String configKey,

        /** 값 유형 (NUMBER, STRING, BOOLEAN 등) */
        String valueType,

        /** 설정 값 */
        String configValue,

        /** 설정 설명 */
        String description,

        /** 활성 여부 (Y/N) */
        String activeYn
) {}
