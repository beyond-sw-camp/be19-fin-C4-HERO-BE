package com.c4.hero.domain.promotion.repository;

import com.c4.hero.domain.promotion.entity.PromotionCandidate;
import com.c4.hero.domain.promotion.type.PromotionCandidateStatus;
import com.c4.hero.domain.promotion.entity.PromotionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

/**
 * <pre>
 * Interface Name: PromotionCandidateRepository
 * Description: PromotionCandidate 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/19 (승건) 최초 작성
 * 2025/12/22 (승건) 상태별 카운트 메서드 추가
 * </pre>
 *
 * @author 승건
 * @version 1.1
 */
public interface PromotionCandidateRepository extends JpaRepository<PromotionCandidate, Integer> {

    /**
     * 특정 승진 상세 계획에 속하고, 지정된 상태 목록 중 하나에 해당하는 후보자 수를 반환합니다.
     *
     * @param promotionDetail 승진 상세 계획
     * @param statuses        포함할 상태 목록
     * @return 조건에 맞는 후보자 수
     */
    long countByPromotionDetailAndStatusIn(PromotionDetail promotionDetail, Collection<PromotionCandidateStatus> statuses);

    /**
     * 사번과 상태로 승진 후보자를 조회합니다.
     *
     * @param employeeNumber 사번
     * @param status         상태
     * @return 승진 후보자 (Optional)
     */
    Optional<PromotionCandidate> findByEmployee_EmployeeNumberAndStatus(String employeeNumber, PromotionCandidateStatus status);
}
