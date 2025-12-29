package com.c4.hero.domain.payroll.policy.dto.request;

/**
 * <pre>
 * Class Name : PolicyConfigUpsertRequest
 * Description : 급여 정책 공통 설정 생성/수정 요청 DTO
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public record PolicyConfigUpsertRequestDTO(

        /** 설정 키 */
        String configKey,

        /** 값 유형 (필수) */
        String valueType,

        /** 설정 값 (텍스트 기반, LONGTEXT) */
        String configValue,

        /** 설정 설명 (선택) */
        String description,

        /** 활성 여부 (null이면 기본값 Y) */
        String activeYn
) {}