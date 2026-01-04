package com.c4.hero.domain.evaluation.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * Class Name: EvaluateeEntity
 * Description: tbl_evaluatee 테이블과 매칭되는 평가 대상자 엔티티
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Entity
@Table(name = "tbl_evaluatee")
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Evaluatee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluatee_id")
    private Integer evaluateeId;

    @Column(name = "evaluation_id")
    private Integer evaluationId;

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "status")
    private Integer status;
}
