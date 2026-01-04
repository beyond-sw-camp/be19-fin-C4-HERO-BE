package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.JobTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * <pre>
 * Interface Name: EmployeeJobTitleRepository
 * Description: JobTitle 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/09 (승건) 최초 작성
 * 2025/12/31 (민철) 부서목록 조회 쿼리 메서드
 * </pre>
 *
 * @author 승건, 민철
 * @version 1.1
 */
public interface EmployeeJobTitleRepository extends JpaRepository<JobTitle, Integer> {

    /**
     * 직책명으로 직책 엔티티 조회
     *
     * @param jobTitle 직책명
     * @return Optional<JobTitle>
     */
    Optional<JobTitle> findByJobTitle(String jobTitle);

    @Query("SELECT MAX(jt.jobTitleId) FROM JobTitle jt")
    Integer findMaxJobTitleId();

    List<JobTitle> findByJobTitleIdGreaterThan(int id);
}
