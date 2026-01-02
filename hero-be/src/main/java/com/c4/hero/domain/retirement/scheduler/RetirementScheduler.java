package com.c4.hero.domain.retirement.scheduler;

import com.c4.hero.domain.employee.entity.Account;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import com.c4.hero.domain.employee.repository.EmployeeAccountRepository;
import com.c4.hero.domain.employee.repository.EmployeeDepartmentRepository;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.employee.type.EmployeeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * <pre>
 * Class Name: RetirementScheduler
 * Description: 퇴직 관련 자동화 작업을 처리하는 스케줄러 클래스
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
public class RetirementScheduler {

    private final EmployeeRepository employeeRepository;
    private final EmployeeAccountRepository accountRepository;
    private final EmployeeDepartmentRepository departmentRepository;

    /**
     * 매일 자정(00:00:00)에 실행되어 퇴사일이 지난 직원의 상태를 퇴직(RETIRED)으로 변경하고,
     * 관련 계정을 비활성화(DISABLED)하며, 부서장 및 권한 정보를 해제합니다.
     */
    @Scheduled(cron = "0 0 0 * * *")
//    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void processRetirements() {
        log.info("Starting retirement processing scheduler...");

        // 퇴사일이 오늘 또는 과거이면서, 아직 상태가 '재직' 또는 '휴직'인 직원 조회
        List<Employee> employeesToRetire = employeeRepository.findAllByTerminationDateBeforeAndStatusNot(
                LocalDate.now().plusDays(1), // 오늘 날짜까지 포함
                EmployeeStatus.RETIRED
        );

        int employeeCount = 0;
        for (Employee employee : employeesToRetire) {
            // 1. 직원 상태 변경
            employee.changeStatus(EmployeeStatus.RETIRED);
            employeeCount++;

            // 2. 부서장 해제 처리
            List<EmployeeDepartment> managingDepartments = departmentRepository.findByManagerId(employee.getEmployeeId());
            for (EmployeeDepartment dept : managingDepartments) {
                dept.removeManager();
            }

            // 3. 계정 비활성화 및 권한 제거
            accountRepository.findByEmployee(employee).ifPresent(account -> {
                if (account.getAccountStatus() != com.c4.hero.domain.employee.type.AccountStatus.DISABLED) {
                    account.disable();
                    account.clearRoles(); // 모든 권한 제거
                }
            });
        }
        
        log.info("Completed retirement processing. Processed {} employees.", employeeCount);
    }
}
