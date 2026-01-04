package com.c4.hero.domain.promotion.repository;

import com.c4.hero.domain.promotion.entity.PersonnelAppointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

/**
 * <pre>
 * Interface Name: PersonnelAppointmentRepository
 * Description: PersonnelAppointment 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/30 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
public interface PersonnelAppointmentRepository extends JpaRepository<PersonnelAppointment, Long> {

    /**
     * 발령일이 특정 날짜(오늘)보다 이전이거나 같으면서, 상태가 대기 중인 발령 건을 페이징하여 조회합니다.
     *
     * @param appointmentDate 기준 날짜 (보통 오늘)
     * @param status 상태
     * @param pageable 페이징 정보
     * @return 처리되지 않은 발령 예약 페이지
     */
    Page<PersonnelAppointment> findAllByAppointmentDateLessThanEqualAndStatus(LocalDate appointmentDate, String status, Pageable pageable);
}
