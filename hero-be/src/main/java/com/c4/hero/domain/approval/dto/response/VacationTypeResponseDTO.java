package com.c4.hero.domain.approval.dto.response;

import lombok.*;

/**
 * <pre>
 * Class Name: VacationTypeResponseDTO
 * Description: 휴가신청서 작성 시 휴가 종류 목록 조회 응답 DTO
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
@AllArgsConstructor
@NoArgsConstructor
public class VacationTypeResponseDTO {

    /**
     * 휴가 종류 ID
     */
    Integer vacationTypeId;

    /**
     * 휴가 종류명 (예: 연차, 반차(오전), 반차(오후), 병가, 공가, 경조사)
     */
    String vacationTypeName;
}