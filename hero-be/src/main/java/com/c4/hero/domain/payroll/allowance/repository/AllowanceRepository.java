package com.c4.hero.domain.payroll.allowance.repository;

import com.c4.hero.domain.payroll.allowance.entity.Allowance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * <pre>
 * Repository Name : AllowanceRepository
 * Description     : 수당(Allowance) 마스터 엔티티 관리 리포지토리
 *
 * History
 *   2025/12/22 - 동근 최초 작성
 * </pre>
 *
 * @version 1.0
 * @author 동근
 */

@Repository
public interface AllowanceRepository extends JpaRepository<Allowance, String> {

    /**
     * 사용 여부(activeYn) 기준 수당 목록 조회
     *
     * @param activeYn Y 또는 N
     * @return 수당 목록
     */
    List<Allowance> findAllByActiveYn(String activeYn);

    /**
     * 특정 수당 코드가 존재하는지 확인
     *
     * @param allowanceId 수당 코드
     * @return 존재하면 true
     */
    boolean existsByAllowanceId(String allowanceId);
}
