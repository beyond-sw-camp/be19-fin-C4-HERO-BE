package com.c4.hero.domain.payroll.deduction.repository;

import com.c4.hero.domain.payroll.deduction.entity.Deduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 * Repository Name : DeductionRepository
 * Description     : 공제(Deduction) 마스터 엔티티 관리 리포지토리

 * History
 *   2025/12/22 - 동근 최초 작성
 * </pre>
 *
 * @version 1.0
 * @author 동근
 */

@Repository
public interface DeductionRepository extends JpaRepository<Deduction, String> {

    /**
     * activeYn 여부로 공제 목록 조회
     *
     * @param activeYn Y 또는 N
     * @return 공제 목록
     */
    List<Deduction> findAllByActiveYn(String activeYn);

    /**
     * 특정 공제 코드가 존재하는지 확인
     *
     * @param deductionId 공제 코드
     * @return 존재하면 true
     */
    boolean existsByDeductionId(String deductionId);
}
