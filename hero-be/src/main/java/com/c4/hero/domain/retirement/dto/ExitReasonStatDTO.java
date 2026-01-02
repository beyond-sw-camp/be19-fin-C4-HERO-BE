package com.c4.hero.domain.retirement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: ExitReasonStatDTO
 * Description: 퇴사 사유별 통계 정보를 전달하는 DTO 클래스
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
public class ExitReasonStatDTO {

    /** 사유 명칭 */
    private String reasonName;

    /** 해당 사유로 퇴사한 인원 수 */
    private Long count;
}
