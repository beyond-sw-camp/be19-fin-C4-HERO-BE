package com.c4.hero.domain.evaluation.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * Class Name: CriteriaEntity
 * Description: tbl_criteria 테이블과 매칭되는 평가 기준 엔티티
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Entity
@Table(name = "tbl_criteria")
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Criteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "criteria_id")
    private Integer criteriaId;

    @Column(name = "rank")
    private String rank;

    @Column(name = "description")
    private String desription;

    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "min_score")
    private Integer minScore;

    @Column(name = "max_score")
    private Integer maxScore;
}
