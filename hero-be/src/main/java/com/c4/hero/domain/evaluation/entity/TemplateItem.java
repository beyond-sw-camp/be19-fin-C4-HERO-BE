package com.c4.hero.domain.evaluation.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * <pre>
 * Class Name: TemplateItemEntity
 * Description: tbl_template_item 테이블과 매칭되는 평가 항목 엔티티
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Entity
@Table(name = "tbl_template_item")
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemplateItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "template_id")
    private Integer templateId;

    @Column(name = "item")
    private String item;

    @Column(name = "description")
    private String description;
}
