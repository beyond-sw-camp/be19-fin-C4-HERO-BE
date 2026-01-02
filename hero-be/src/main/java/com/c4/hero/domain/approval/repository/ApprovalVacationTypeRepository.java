package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.vacation.entity.VacationType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <pre>
 * Interface Name : ApprovalVacationTypeRepository
 * Description    : 휴가 종류 Repository
 *                  휴가신청서 작성 시 선택 가능한 휴가 종류 목록 조회
 *                  VacationType 엔티티는 vacation 도메인에 속하지만,
 *                  결재 도메인에서 필요한 휴가 종류 조회를 위해 별도 Repository 생성
 *
 * History
 *   2025/12/28 (민철) 최초 작성
 *   2026/01/01 (민철) 클래스 주석 추가
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
public interface ApprovalVacationTypeRepository extends JpaRepository<VacationType, Integer> {
    // findAll() 메서드를 사용하여 전체 휴가 종류 목록 조회
    // 예: 연차, 병가, ....
}