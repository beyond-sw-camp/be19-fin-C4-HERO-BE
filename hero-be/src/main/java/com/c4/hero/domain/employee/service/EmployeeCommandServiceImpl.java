package com.c4.hero.domain.employee.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.common.s3.S3Service;
import com.c4.hero.common.util.EncryptionUtil;
import com.c4.hero.domain.employee.dto.request.SignupRequestDTO;
import com.c4.hero.domain.employee.entity.*;
import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import com.c4.hero.domain.employee.repository.EmployeeAccountRepository;
import com.c4.hero.domain.employee.repository.EmployeeAccountRoleRepository;
import com.c4.hero.domain.employee.repository.EmployeeDepartmentRepository;
import com.c4.hero.domain.employee.repository.EmployeeDepartmentHistoryRepository;
import com.c4.hero.domain.employee.repository.EmployeeGradeHistoryRepository;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.employee.repository.EmployeeGradeRepository;
import com.c4.hero.domain.employee.repository.EmployeeJobTitleRepository;
import com.c4.hero.domain.employee.repository.EmployeeRoleRepository;
import com.c4.hero.domain.employee.type.AccountStatus;
import com.c4.hero.domain.employee.type.ChangeType;
import com.c4.hero.domain.employee.type.EmployeeStatus;
import com.c4.hero.domain.employee.type.RoleType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <pre>
 * Class Name: EmployeeServiceImpl
 * Description: 직원 관련 API를 처리하는 서비스 계층
 *
 * History
 * 2025/12/09 승건 최초 작성 (사원 추가 로직 처리)
 * 2025/12/15 승건 변경 이력 메소드 추가 및 적용
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmployeeCommandServiceImpl implements EmployeeCommandService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private final EmployeeRepository employeeRepository;
    private final EmployeeDepartmentRepository departmentRepository;
    private final EmployeeGradeRepository gradeRepository;
    private final EmployeeJobTitleRepository jobTitleRepository;
    private final EmployeeAccountRepository accountRepository;
    private final EmployeeRoleRepository roleRepository;
    private final EmployeeAccountRoleRepository accountRoleRepository;
    private final EmployeeDepartmentHistoryRepository employeeDepartmentHistoryRepository;
    private final EmployeeGradeHistoryRepository employeeGradeHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final S3Service s3Service;

    private final EncryptionUtil encryptionUtil;

    @Value("${spring.mail.username}")
    private String defaultMailSenderUsername;

    /**
     * {@inheritDoc}
     * <p>
     * 이 구현체는 새로운 직원 정보를 데이터베이스에 저장하고,
     * 계정 생성 후 임시 비밀번호를 직원의 이메일로 발송합니다.
     * 모든 과정은 트랜잭션 내에서 처리됩니다.
     */
    @Override
    public void signup(SignupRequestDTO request) {
        // 1. 중복 체크 (사번, 이메일, 전화번호)
        byte[] encryptedEmail = encryptionUtil.encrypt(request.getEmail());
        byte[] encryptedPhone = encryptionUtil.encrypt(request.getPhone());

        employeeRepository.findByEmployeeNumberOrEmailOrPhone(
                request.getEmployeeNumber(),
                encryptedEmail,
                encryptedPhone
        ).ifPresent(employee -> {
            if (employee.getEmployeeNumber().equals(request.getEmployeeNumber())) {
                throw new BusinessException(ErrorCode.DUPLICATE_EMPLOYEE_NUMBER);
            }
            if (Arrays.equals(employee.getEmail(), encryptedEmail)) {
                throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
            }
            if (Arrays.equals(employee.getPhone(), encryptedPhone)) {
                throw new BusinessException(ErrorCode.DUPLICATE_PHONE);
            }
        });

        // 2. 이미지 파일 처리 (S3 업로드)
        String imagePath = null;
        MultipartFile imageFile = request.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {
            // 파일 크기 검증
            if (imageFile.getSize() > MAX_FILE_SIZE) {
                throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED);
            }
            imagePath = s3Service.uploadFile(imageFile, "employee");
        }

        // 3. DTO -> Employee 엔티티 변환
        EmployeeDepartment employeeDepartment = departmentRepository.findByDepartmentName(request.getDepartmentName())
                .orElseThrow(() -> new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND));
        Grade grade = gradeRepository.findByGrade(request.getGradeName())
                .orElseThrow(() -> new BusinessException(ErrorCode.GRADE_NOT_FOUND));
        JobTitle jobTitle = jobTitleRepository.findByJobTitle(request.getJobTitleName())
                .orElseThrow(() -> new BusinessException(ErrorCode.JOB_TITLE_NOT_FOUND));

        Employee employee = Employee.builder()
                .employeeNumber(request.getEmployeeNumber())
                .employeeName(request.getEmployeeName())
                .email(encryptedEmail)
                .phone(encryptedPhone)
                .gender(request.getGender())
                .birthDate(request.getBirthDate())
                .hireDate(request.getHireDate())
                .contractType(request.getContractType())
                .imagePath(imagePath)
                .address(request.getAddress() != null ? encryptionUtil.encrypt(request.getAddress()) : null)
                .employeeDepartment(employeeDepartment)
                .baseSalary(request.getBaseSalary())
                .grade(grade)
                .jobTitle(jobTitle)
                .status(EmployeeStatus.ACTIVE)
                .evaluationPoint(BigDecimal.valueOf(0)) // 초기값 0으로 설정
                .build();

        Employee savedEmployee = employeeRepository.save(employee);

        String accountId = request.getEmail().split("@")[0];
        String tempPassword = createRandomPassword();

        Account account = Account.builder()
                .employee(savedEmployee)
                .account(accountId)
                .passwordHash(passwordEncoder.encode(tempPassword))
                .accountStatus(AccountStatus.ACTIVE)
                .passwordChangeRequired(true)
                .build();

        accountRepository.save(account);

        // 5. 권한 부여 로직
        Role employeeRole = roleRepository.findByRole(RoleType.EMPLOYEE)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

        AccountRole accountRole = AccountRole.builder()
                .account(account)
                .role(employeeRole)
                .build();
        accountRoleRepository.save(accountRole);

        // 6. 부서 이력 저장
        addDepartmentHistory(savedEmployee, ChangeType.CREATE, employeeDepartment.getDepartmentName());

        // 7. 직급 이력 저장
        addGradeHistory(savedEmployee, ChangeType.CREATE, grade.getGrade());

        // 8. 이메일 발송 로직 (tempPassword 발송)
        sendTemporaryPasswordEmail(request.getEmail(), accountId, tempPassword);
    }

    /**
     * {@inheritDoc}
     *
     * TODO: changeBy 추가 하기 -> 토큰을 통해 사용자 인식하기
     */
    @Override
    public void addDepartmentHistory(Employee employee, ChangeType changeType, String departmentName) {

        EmployeeDepartmentHistory newHistory =
                EmployeeDepartmentHistory.builder()
                        .employee(employee)
                        .changedBy(null)
                        .changeType(changeType)
                        .departmentName(departmentName)
                        .build();

        employeeDepartmentHistoryRepository.save(newHistory);
    }
    /**
     * 직급 변경 이력 추가
     */
    @Override
    public void addGradeHistory(Employee employee, ChangeType changeType, String gradeName) {
        EmployeeGradeHistory newHistory =
                EmployeeGradeHistory.builder()
                        .employee(employee)
                        .changedBy(null)
                        .changeType(changeType)
                        .gradeName(gradeName)
                        .build();

        employeeGradeHistoryRepository.save(newHistory);
    }

    @Override
    public void updateDepartment(String employeeNumber, String departmentName) {
        Employee employee = employeeRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        EmployeeDepartment newDepartment = departmentRepository.findByDepartmentName(departmentName)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND));

        // 부서 변경
        employee.changeDepartment(newDepartment);

        // 이력 저장
        addDepartmentHistory(employee, ChangeType.TRANSFER, departmentName);
    }

    @Override
    public void updateJobTitle(String employeeNumber, String jobTitleName) {
        Employee employee = employeeRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        JobTitle newJobTitle = jobTitleRepository.findByJobTitle(jobTitleName)
                .orElseThrow(() -> new BusinessException(ErrorCode.JOB_TITLE_NOT_FOUND));

        // 직책 변경
        employee.changeJobTitle(newJobTitle);
        
        // 직책 변경 이력은 현재 별도 테이블이 없으므로 생략하거나 필요 시 추가
    }

    /* =================== private =================== */

    /**
     * 10자리의 랜덤 비밀번호 생성 (영문 대/소문자 + 숫자)
     * @return 랜덤 비밀번호 문자열
     */
    private String createRandomPassword() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        return IntStream.range(0, 10)
                .map(i -> random.nextInt(chars.length()))
                .mapToObj(randomIndex -> String.valueOf(chars.charAt(randomIndex)))
                .collect(Collectors.joining());
    }

    /**
     * 임시 비밀번호 이메일 발송
     * @param toEmail 수신자 이메일 주소
     * @param accountId 계정 ID
     * @param tempPassword 임시 비밀번호
     */
    private void sendTemporaryPasswordEmail(String toEmail, String accountId, String tempPassword) {
        // 회사 대표 이메일 조회 시도
        // Google의 SMTP의 경우 인증된 이메일로만 보내져서 현재 회사 이메일을 불러오는게 무의미함..
        // TODO: 회사 이메일을 불러와서 가능하게 만들어보자(google이 아닌 다른 SMTP를 사용해야 한다.)
        String fromEmail = defaultMailSenderUsername; // Company가 없으면 기본 발신자 사용

        log.info("Final sender email determined: {}", fromEmail);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail); // 최종 결정된 발신자 이메일 설정
            helper.setTo(toEmail);
            helper.setSubject("[Hero 시스템] 임시 비밀번호 안내");
            String emailContent = String.format(
                    "안녕하세요, Hero 시스템입니다.<br><br>" +
                            "귀하의 계정 ID는 <b>%s</b> 입니다.<br>" +
                            "임시 비밀번호는 <b>%s</b> 입니다.<br><br>" +
                            "로그인 후 반드시 비밀번호를 변경해 주세요.<br><br>" +
                            "감사합니다.",
                    accountId, tempPassword
            );
            helper.setText(emailContent, true);

            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new BusinessException(ErrorCode.EMAIL_SEND_FAILED, "이메일 발송에 실패했습니다.");
        }
    }
}
