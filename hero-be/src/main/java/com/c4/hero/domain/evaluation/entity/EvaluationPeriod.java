package com.c4.hero.domain.evaluation.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: EvaluationPeriodEntity
 * Description: tbl_evaluation_period 테이블과 매칭되는 평가 기간 엔티티
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Entity
@Table(name = "tbl_evaluation_period")
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "evaluation_period_id")
    private Integer evaluationPeriodId;

    @Column(name = "name")
    private String name;

    @Column(name = "start")
    private LocalDateTime start;

    @Column(name = "end")
    private LocalDateTime end;

    @Column(name = "template_id")
    private Integer templateId;
}
