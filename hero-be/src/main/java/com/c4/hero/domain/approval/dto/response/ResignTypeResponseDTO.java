package com.c4.hero.domain.approval.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: ResignTypeResponseDTO
 * Description: 사직서 작성 시 퇴직 사유 목록 조회 응답 DTO
 *
 * History
 * 2025/12/28 (민철) 최초작성
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResignTypeResponseDTO {

    /**
     * 퇴직 사유 ID
     */
    private Integer resignTypeId;

    /**
     * 퇴직 사유명 (예: 개인 사정, 이직, 건강, 가족 사정, 기타)
     */
    private String resignTypeName;
}