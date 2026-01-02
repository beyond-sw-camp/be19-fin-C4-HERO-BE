package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalResignType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <pre>
 * Interface Name : ApprovalResignTypeRepository
 * Description    : 퇴직 사유 Repository
 *                  사직서 작성 시 선택 가능한 퇴직 사유 목록 조회
 *
 * History
 *   2025/12/28 (민철) 최초 작성
 *   2026/01/01 (민철) 클래스 주석 추가
 * </pre>
 *
 * @author 민철
 * @version 1.1
 */
public interface ApprovalResignTypeRepository extends JpaRepository<ApprovalResignType, Integer> {
    // findAll() 메서드를 사용하여 전체 퇴직 사유 목록 조회
}