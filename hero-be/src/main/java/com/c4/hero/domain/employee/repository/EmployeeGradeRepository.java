package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * <pre>
 * Interface Name: EmployeeGradeRepository
 * Description: Grade 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/09 (승건) 최초 작성
 * 2025/12/31 (민철) 직급목록 조회 쿼리 메서드
 * </pre>
 *
 * @author 승건, 민철
 * @version 1.1
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

    List<Grade> findByGradeIdGreaterThan(int id);
}
