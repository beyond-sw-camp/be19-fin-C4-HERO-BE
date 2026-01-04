package com.c4.hero.domain.vacation.dto;

import com.c4.hero.domain.vacation.type.VacationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * <pre>
 * Class Name: VacationHistoryDTO
 * Description: 휴가 이력(사용 휴가 기록) 정보를 담는 DTO
 *
 * History
 * 2025/12/16 (이지윤) 최초 작성 및 백엔드 코딩 컨벤션 적용
 * </pre>
 *
 * 휴가 그래프/리스트에서 한 행(row)에 해당하는 데이터를 표현하며,
 * 기간, 휴가 유형, 사유, 결재 상태 등의 정보를 포함합니다.
 *
 * @author 이지윤
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VacationHistoryDTO {

    /** 휴가 이력 PK (식별자) */
    private Integer vacationLogId;

    /** 휴가 시작일 (yyyy-MM-dd) */
    private LocalDate startDate;

    /** 휴가 종료일 (yyyy-MM-dd) */
    private LocalDate endDate;

    /** 휴가 유형 이름 (예: 연차, 반차, 병가 등) */
    private String vacationTypeName;

    /** 휴가 사유 */
    private String reason;

    /** 결재 상태 (예: PENDING, APPROVED, REJECTED 등) */

    private VacationStatus approvalStatus;

    /**
     * 휴가 이력 ID를 제외한 필드만 초기화하는 생성자입니다.
     * <p>
     * 주로 JPQL / Native Query의 {@code new} 구문이나
     * 프로젝션 매핑용으로 사용합니다.
     *
     * @param startDate        휴가 시작일
     * @param endDate          휴가 종료일
     * @param vacationTypeName 휴가 유형 이름
     * @param reason           휴가 사유
     * @param approvalStatus   승인 현황
     */
    public VacationHistoryDTO(
            LocalDate startDate,
            LocalDate endDate,
            String vacationTypeName,
            String reason,
            VacationStatus approvalStatus
    ) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.vacationTypeName = vacationTypeName;
        this.reason = reason;
        this.approvalStatus = approvalStatus;
    }
}
