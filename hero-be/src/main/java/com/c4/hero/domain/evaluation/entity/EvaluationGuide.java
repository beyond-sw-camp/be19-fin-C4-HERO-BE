package com.c4.hero.domain.evaluation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: EvaluationGuideEntity
 * Description: tbl_evaluation_guide 테이블과 매칭되는 평가 가이드 엔티티
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Entity
@Table(name = "tbl_evaluation_guide")
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_guide_id")
    private Integer evaluationGuideId;

    @Column(name = "name")
    private String name;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "department_id")
    private Integer departmentId;
}
