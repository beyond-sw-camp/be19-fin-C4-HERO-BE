package com.c4.hero.domain.evaluation.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * Class Name: ItemScoreEntity
 * Description: tbl_item_score 테이블과 매칭되는 평가서 항목 점수 엔티티
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Entity
@Table(name = "tbl_item_score")
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_score_id")
    private Integer itemScoreId;

    @Column(name = "form_item_id")
    private Integer formItemId;

    @Column(name = "score")
    private Integer score;

    @Column(name = "description")
    private String description;

    @Column(name = "rank")
    private String rank;
}
