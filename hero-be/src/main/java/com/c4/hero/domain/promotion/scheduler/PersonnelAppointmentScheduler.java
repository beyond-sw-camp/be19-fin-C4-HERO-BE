package com.c4.hero.domain.promotion.scheduler;

import com.c4.hero.domain.promotion.entity.PersonnelAppointment;
import com.c4.hero.domain.promotion.repository.PersonnelAppointmentRepository;
import com.c4.hero.domain.promotion.service.PersonnelAppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * <pre>
 * Class Name: PersonnelAppointmentScheduler
 * Description: 예약된 인사 발령을 처리하는 스케줄러
 *
 * History
 * 2025/12/30 (승건) 최초 작성
 * 2025/01/02 (승건) ShedLock, Paging, 트랜잭션 분리 적용
 * </pre>
 *
 * @author 승건
 * @version 1.1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PersonnelAppointmentScheduler {

    private static final int PAGE_SIZE = 100;
    private final PersonnelAppointmentRepository personnelAppointmentRepository;
    private final PersonnelAppointmentService personnelAppointmentService;

    /**
     * 매일 자정(00:00:00)에 실행되어 발령일이 오늘이거나 과거인 처리 대기 중인 인사 발령을 처리합니다.
     * ShedLock을 사용하여 여러 서버 인스턴스에서 동시에 실행되는 것을 방지합니다.
     * 대용량 데이터를 처리하기 위해 페이징을 사용합니다.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @SchedulerLock(name = "processPersonnelAppointments", lockAtLeastFor = "PT1M", lockAtMostFor = "PT5M")
    public void processAppointments() {
        log.info("Starting personnel appointment processing scheduler...");
        LocalDate today = LocalDate.now();
        Pageable pageable = PageRequest.of(0, PAGE_SIZE);
        Page<PersonnelAppointment> page;
        int processedCount = 0;

        do {
            page = personnelAppointmentRepository.findAllByAppointmentDateLessThanEqualAndStatus(today, "WAITING", pageable);
            
            for (PersonnelAppointment appointment : page.getContent()) {
                // 개별 처리를 위한 서비스 메서드 호출 (내부적으로 REQUIRES_NEW 트랜잭션)
                personnelAppointmentService.processSingleAppointmentInTransaction(appointment);
                processedCount++;
            }
            
            pageable = page.nextPageable();
        } while (page.hasNext());

        log.info("Completed personnel appointment processing. Total processed: {}", processedCount);
    }
}
