package com.c4.hero.domain.retirement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: ExitReasonDTO
 * Description: 퇴사 사유 정보를 전달하는 DTO 클래스
 *
 * History
 * 2025/12/30 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExitReasonDTO {

    /** 퇴사 사유 ID */
    private Integer exitReasonId;

    /** 사유 명칭 */
    private String reasonName;

    /** 사유 유형 (자발적/비자발적 등) */
    private String reasonType;

    /** 상세 설명 */
    private String detailDescription;
}
