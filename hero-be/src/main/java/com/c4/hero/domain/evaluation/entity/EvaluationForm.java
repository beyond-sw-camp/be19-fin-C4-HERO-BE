package com.c4.hero.domain.evaluation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: EvaluationFormEntity
 * Description: tbl_evaluation_form 테이블과 매칭되는 평가서 엔티티
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Entity
@Table(name = "tbl_evaluation_form")
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_id")
    private Integer formId;

    @Column(name = "evaluation_id")
    private Integer evaluationId;

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "total")
    private String total;

    @Column(name = "total_rank")
    private String totalRank;

    @Column(name = "total_score")
    private Float totalScore;
}
