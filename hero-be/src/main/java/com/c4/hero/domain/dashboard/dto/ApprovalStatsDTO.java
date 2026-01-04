package com.c4.hero.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * <pre>
 * Class Name  : ApprovalStatsDTO
 * Description : 결재 현황 DTO
 *
 * History
 * 2025/12/26 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalStatsDTO {

    /** 결재 대기 */
    private Integer pendingCount;

    /** 결재 완료 */
    private Integer approvedCount;

    /** 결재 반려 */
    private Integer rejectedCount;
}