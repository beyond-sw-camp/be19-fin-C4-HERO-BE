package com.c4.hero.domain.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 * Class Name: Grade
 * Description: 직급 정보를 담는 엔티티 클래스 (예: 사원, 대리, 과장)
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */

@Builder
@Entity
@Table(name = "tbl_grade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

    /**
     * 직급 ID (PK)
     */
    @Id
    @Column(name = "grade_id")
    private Integer gradeId;

    /**
     * 직급명 (예: 사원, 대리, 과장)
     */
    @Column(name = "grade", nullable = false, length = 10)
    private String grade;

    @Column(name = "required_point")
    private Integer requiredPoint;

    @Builder
    public Grade(String grade, Integer requiredPoint) {
        this.grade = grade;
        this.requiredPoint = requiredPoint;
    }

}
