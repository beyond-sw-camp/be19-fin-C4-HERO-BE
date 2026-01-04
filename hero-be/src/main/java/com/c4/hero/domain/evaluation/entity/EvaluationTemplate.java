package com.c4.hero.domain.evaluation.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: EvaluationTemplateEntity
 * Description: tbl_evaluation_template 테이블과 매칭되는 평가 템플릿 엔티티
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Entity
@Table(name = "tbl_evaluation_template")
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Integer templateId;

    @Column(name = "name")
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "type")
    private Integer type;
}



