package com.c4.hero.domain.promotion.scheduler;

import com.c4.hero.domain.promotion.entity.PersonnelAppointment;
import com.c4.hero.domain.promotion.repository.PersonnelAppointmentRepository;
import com.c4.hero.domain.promotion.service.PersonnelAppointmentService;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Class Name: PersonnelAppointmentScheduler
 * Description: 예약된 인사 발령을 처리하는 스케줄러
 *
 * History
 * 2025/12/30 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PersonnelAppointmentScheduler {

    private final PersonnelAppointmentRepository personnelAppointmentRepository;
    private final PersonnelAppointmentService personnelAppointmentService;
    private final ObjectMapper objectMapper;

    /**
     * 매일 자정(00:00:00)에 실행되어 발령일이 오늘이거나 과거인 처리 대기 중인 인사 발령을 처리합니다.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
//    @Scheduled(fixedRate = 10000)  // 주기적 실행
    @Transactional
    public void processAppointments() {
        LocalDate today = LocalDate.now();
        log.info("Starting personnel appointment processing for date <= {}", today);

        List<PersonnelAppointment> appointments = personnelAppointmentRepository.findAllByAppointmentDateLessThanEqualAndStatus(today, "WAITING");

        for (PersonnelAppointment appointment : appointments) {
            try {
                Map<String, Object> details = objectMapper.readValue(appointment.getDetails(), new TypeReference<>() {});
                personnelAppointmentService.processAppointment(details);
                
                appointment.complete();
                log.info("✅ 인사 발령 처리 성공 - id: {}, employee: {}", appointment.getAppointmentId(), appointment.getEmployeeNumber());
            } catch (Exception e) {
                log.error("❌ 인사 발령 처리 실패 - id: {}, employee: {}", appointment.getAppointmentId(), appointment.getEmployeeNumber(), e);
                appointment.fail(e.getMessage());
            }
        }

        log.info("Completed personnel appointment processing. Total: {}", appointments.size());
    }
}
