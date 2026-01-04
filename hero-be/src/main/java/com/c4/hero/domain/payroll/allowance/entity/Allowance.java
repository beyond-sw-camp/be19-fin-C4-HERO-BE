package com.c4.hero.domain.payroll.allowance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 * Entity Name : Allowance
 * Description : 급여 수당 마스터 엔티티
 *
 * 역할
 *  - 회사에서 사용하는 수당 항목(연장근무수당, 야간수당 등)을 관리하는 마스터 테이블
 *
 * History
 *   2025/12/22 - 동근 최초 작성 (화면 CRUD 연결을 위한 상태 변경 로직 추가)
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */

@Entity
@Table(name = "tbl_allowance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Allowance {

    /**
     * 수당 코드 (PK)
     * 예: OVERTIME, NIGHT, MEAL 등
     */
    @Id
    @Column(name = "allowance_id", length = 30)
    private String allowanceId;

    /**
     * 화면 표시용 이름(수당 명)
     */
    @Column(nullable = false, length = 100)
    private String allowanceName;

    /**
     * 부가 설명
     */
    private String description;

    /**
     * 기본 수당액 (없으면 0)
     */
    private Integer defaultAmount;

    /**
     * 과세 여부 (Y/N)
     */
    @Column(nullable = false, length = 1)
    private String taxableYn;

    /**
     * 사용 여부 (Y/N)
     */
    @Column(nullable = false, length = 1)
    private String activeYn;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // ===== 비즈니스 메서드 =====
    /**
     * 수당 항목 수정
     *
     * @param name          수당명
     * @param description   설명
     * @param defaultAmount 기본지급액
     * @param taxableYn     과세 여부(Y/N)
     */
    public void update(String name, String description, Integer defaultAmount, String taxableYn) {
        this.allowanceName = name;
        this.description = description;
        this.defaultAmount = defaultAmount;
        this.taxableYn = taxableYn;
    }

    /**
     * 수당 비활성화 (activeYn = N)
     */
    public void deactivate() {
        this.activeYn = "N";
    }

    /**
     * 수당 활성화 (activeYn = Y)
     */
    public void activate() {
        this.activeYn = "Y";
    }
}