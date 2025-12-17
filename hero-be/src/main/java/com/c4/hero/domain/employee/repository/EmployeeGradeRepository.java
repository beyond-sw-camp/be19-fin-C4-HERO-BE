package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * <pre>
 * Interface Name: EmployeeGradeRepository
 * Description: Grade 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
public interface EmployeeGradeRepository extends JpaRepository<Grade, Integer> {

    /**
     * 직급명으로 직급 엔티티 조회
     *
     * @param grade 직급명
     * @return Optional<Grade>
     */
    Optional<Grade> findByGrade(String grade);

    @Query("SELECT MAX(g.gradeId) FROM Grade g")
    Integer findMaxGradeId();
}
