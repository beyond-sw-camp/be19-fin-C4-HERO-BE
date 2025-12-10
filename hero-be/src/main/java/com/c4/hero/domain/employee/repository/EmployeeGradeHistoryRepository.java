package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.EmployeeGradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <pre>
 * Interface Name: EmployeeGradeHistoryRepository
 * Description: EmployeeGradeHistory 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
public interface EmployeeGradeHistoryRepository extends JpaRepository<EmployeeGradeHistory, Integer> {
}
