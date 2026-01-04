package com.c4.hero.domain.evaluation.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * Class Name: SelectedItemEntity
 * Description: tbl_selected_item 테이블과 매칭되는 선택된 평가 항목 엔티티
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Entity
@Table(name = "tbl_selected_item")
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SelectedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "selected_item_id")
    private Integer selectedItemId;

    @Column(name = "evaluation_id")
    private Integer evaluationId;

    @Column(name = "item_id")
    private Integer itemId;
}
