package com.c4.hero.domain.evaluation.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * Class Name: FormItemEntity
 * Description: tbl_form_item 테이블과 매칭되는 평가서 항목 엔티티
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Entity
@Table(name = "tbl_form_item")
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "form_item_id")
    private Integer formItemId;

    @Column(name = "form_id")
    private Integer formId;

    @Column(name = "selected_item_id")
    private Integer selectedItemId;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "description")
    private String description;
}
