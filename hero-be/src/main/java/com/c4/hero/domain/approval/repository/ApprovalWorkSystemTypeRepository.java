package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.attendance.entity.WorkSystemType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <pre>
 * Interface Name : ApprovalWorkSystemTypeRepository
 * Description    : 근무제 템플릿 Repository
 *                  근무변경신청서 작성 시 선택 가능한 근무제 템플릿 목록 조회
 *                  WorkSystemType 엔티티는 attendance 도메인에 속하지만,
 *                  결재 도메인에서 필요한 근무제 정보 조회를 위해 별도 Repository 생성
 *
 * History
 *   2025/12/28 (민철) 최초 작성
 *   2026/01/01 (민철) 클래스 주석 추가
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
public interface ApprovalWorkSystemTypeRepository extends JpaRepository<WorkSystemType, Integer> {
    // findAll() 메서드를 사용하여 전체 근무제 템플릿 목록 조회
    // 예: 표준 근무제, 탄력 근무제, 선택적 근무제
}