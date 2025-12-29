package com.c4.hero.domain.payroll.policy.entity;

import com.c4.hero.domain.payroll.common.type.PayrollTargetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name : PayrollItemPolicyTarget
 * Description : 급여 항목 정책(Item Policy)의 적용 대상 정보를 관리하는 엔티티
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Entity
@Table(name = "tbl_payroll_item_policy_target")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PayrollItemPolicyTarget {

    /** 적용 대상 식별자 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "target_id")
    private Integer targetId;

    /** 소속 항목 정책 ID (Item Policy) */
    @Column(nullable = false)
    private Integer itemPolicyId;

    /** 적용 대상 유형 (사원 / 부서 / 직급 등) */
    @Enumerated(EnumType.STRING)
    @Column(name="target_type", nullable = false, length = 30)
    private PayrollTargetType payrollTargetType;

    /** 대상 값 (사원 ID, 부서 코드, 직급 코드 등) */
    @Column(length = 50)
    private String targetValue;
}
