package com.c4.hero.domain.vacation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


/**
 * <pre>
 * Class Name: DepartmentVacationDTO
 * Description: 부서 휴가 캘린더(월 단위) 구성을 위한 휴가 정보 DTO
 *
 * History
 * 2025/12/16 (이지윤) 최초 작성 및 코딩 컨벤션 적용
 * </pre>
 *
 * 부서 단위 캘린더에 표시할
 * 직원별 휴가 구간(시작/종료 시각)과 휴가 유형 정보를 담습니다.
 *
 * @author 이지윤
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DepartmentVacationDTO {

    /** 휴가 이력 PK (식별자) */
    private Integer vacationLogId;

    /** 휴가 사용 직원 ID */
    private Integer employeeId;

    /** 휴가 사용 직원 이름 */
    private String employeeName;

    /** 휴가 유형 이름 (예: 연차, 반차, 병가 등) */
    private String vacationTypeName;

    /** 휴가 시작 일시 (캘린더/타임라인 표시용) */
    private LocalDate startDate;

    /** 휴가 종료 일시 (캘린더/타임라인 표시용) */
    private LocalDate endDate;
}
