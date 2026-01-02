package com.c4.hero.domain.retirement.scheduler;

import com.c4.hero.common.util.EncryptionUtil;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.repository.EmployeeAccountRepository;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * <pre>
 * Class Name: EmployeeDataCleanupScheduler
 * Description: 퇴사자 개인정보 파기(삭제)를 담당하는 스케줄러
 *
 * History
 * 2025/12/31 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeDataCleanupScheduler {

    private final EmployeeRepository employeeRepository;
    private final EmployeeAccountRepository accountRepository;
    private final EncryptionUtil encryptionUtil;

    /**
     * 매일 자정(00:00:00)에 실행되어 개인정보 보관 기간이 만료된 직원의 데이터를 익명화합니다.
     */
    @Scheduled(cron = "0 0 0 * * *")
//    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void anonymizeExpiredEmployeeData() {
        log.info("Starting expired employee data anonymization scheduler...");

        // 보관 만료일(retentionExpireAt)이 오늘 또는 과거인 직원 조회
        List<Employee> expiredEmployees = employeeRepository.findAllByRetentionExpireAtBefore(LocalDate.now().plusDays(1));

        int count = 0;
        for (Employee employee : expiredEmployees) {
            try {
                // 1. 계정 정보 삭제
                accountRepository.findByEmployee(employee).ifPresent(accountRepository::delete);

                // 2. 직원 개인정보 익명화
                employee.anonymize(encryptionUtil);
                
                // 3. (선택) 실제 이미지 파일 삭제 로직 추가
                // if (employee.getImagePath() != null) { FileUtil.deleteFile(employee.getImagePath()); }
                // if (employee.getSealImageUrl() != null) { FileUtil.deleteFile(employee.getSealImageUrl()); }
                
                count++;
                log.info("Anonymized expired employee data - employeeNumber: {}", employee.getEmployeeNumber());
            } catch (Exception e) {
                log.error("Failed to anonymize employee data - employeeNumber: {}", employee.getEmployeeNumber(), e);
            }
        }

        log.info("Completed expired employee data anonymization. Processed {} employees.", count);
    }
}
