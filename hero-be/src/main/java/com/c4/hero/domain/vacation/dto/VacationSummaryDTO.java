package com.c4.hero.domain.vacation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 * Class Name: VacationSummaryDTO
 * Description: 휴가 요약 정보(총 부여일수, 사용일수, 잔여일수 등)를 담는 DTO
 *
 * History
 * 2025/12/18 (이지윤) 최초 작성 및 코딩 컨벤션 적용
 * </pre>
 *
 * 휴가 요약 카드(총 연차, 사용 연차, 잔여 연차 등)에 표시할
 * 핵심 데이터를 한 번에 전달하기 위한 DTO입니다.
 *
 * @author 이지윤
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VacationSummaryDTO {

    /** 휴가(연차) 부여 이력/정책 식별자 */
    private Integer leaveId;

    /** 부여된 총 휴가 일수 */
    private Integer grantDays;

    /** 사용한 휴가 일수 */
    private Integer usedDays;

    /** 남은 휴가 일수 */
    private Integer remainingDays;
}
