package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.JobTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * <pre>
 * Interface Name: EmployeeJobTitleRepository
 * Description: JobTitle 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
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
}
