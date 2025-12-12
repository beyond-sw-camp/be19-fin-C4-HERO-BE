package com.c4.hero.domain.evaluation.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: EvaluationEntity
 * Description: tbl_evaluation 테이블과 매칭되는 평가 엔티티
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Entity
@Table(name = "tbl_evaluation")
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_id")
    private Integer evaluationId;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "template_id")
    private Integer templateId;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private Integer status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "total_rank")
    private String totalRank;

    @Column(name = "total_score")
    private Float totalScore;

    @Column(name = "evaluation_guide_id")
    private Integer evaluationGuideId;

    @Column(name = "evaluation_period_id")
    private Integer evaluationPeriodId;
}
