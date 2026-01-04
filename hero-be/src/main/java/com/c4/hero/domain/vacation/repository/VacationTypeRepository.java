package com.c4.hero.domain.vacation.repository;

import com.c4.hero.domain.vacation.entity.VacationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <pre>
 * Repository Name: VacationTypeRepository
 * Description    : 휴가 유형(VacationType) 엔티티에 대한 JPA Repository
 *
 * - 휴가 유형 기본 정보(연차, 반차, 병가 등)에 대한 CRUD 처리
 * - 기본 메서드(findAll, findById, save, delete 등)는 JpaRepository에서 제공
 *
 * History
 * 2025/12/29 (이지윤) 최초 작성 및 컨벤션 적용
 * </pre>
 */
@Repository
public interface VacationTypeRepository extends JpaRepository<VacationType, Integer> {
    // 추가적인 조회 메서드가 필요할 경우 여기에 정의합니다.
}
