package com.c4.hero.domain.employee.service;

import com.c4.hero.domain.employee.dto.request.PasswordChangeRequestDTO;
import com.c4.hero.domain.employee.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.common.util.EncryptionUtil;
import com.c4.hero.domain.auth.security.JwtUtil;
import com.c4.hero.domain.employee.entity.Account;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.repository.EmployeeAccountRepository;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <pre>
 * Class Name: EmployeePasswordService
 * Description: 직원 비밀번호 관리 서비스
 *
 * History
 * 2025/12/28 (혜원) 최초 작성
 * 2025/12/29 (승건) 비밀번호 찾기 및 최초 로그인 시 비밀번호 변경 로직 추가
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeePasswordService {

    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;
    private final EmployeeAccountRepository accountRepository;
    private final JwtUtil jwtUtil;
    private final JavaMailSender mailSender;
    private final EncryptionUtil encryptionUtil;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 비밀번호 변경
     *
     * @param employeeId 직원 ID
     * @param requestDTO 비밀번호 변경 요청 정보
     * @throws IllegalArgumentException 현재 비밀번호가 일치하지 않는 경우
     */
    @Transactional
    public void changePassword(Integer employeeId, PasswordChangeRequestDTO requestDTO) {
        log.info("비밀번호 변경 시작 - employeeId: {}", employeeId);

        // 현재 비밀번호 확인 (tbl_account에서 조회)
        String currentEncodedPassword = employeeMapper.findPasswordHashByEmployeeId(employeeId);

        if (currentEncodedPassword == null) {
            log.error("계정 정보를 찾을 수 없음 - employeeId: {}", employeeId);
            throw new IllegalArgumentException("계정 정보를 찾을 수 없습니다.");
        }

        // 현재 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(requestDTO.getCurrentPassword(), currentEncodedPassword)) {
            log.error("현재 비밀번호 불일치 - employeeId: {}", employeeId);
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화
        String newEncodedPassword = passwordEncoder.encode(requestDTO.getNewPassword());

        // 비밀번호 업데이트 (tbl_account)
        int updated = employeeMapper.updatePasswordHash(employeeId, newEncodedPassword);

        if (updated == 0) {
            log.error("비밀번호 변경 실패 - employeeId: {}", employeeId);
            throw new RuntimeException("비밀번호 변경에 실패했습니다.");
        }

        log.info("비밀번호 변경 성공 - employeeId: {}", employeeId);
    }

    /**
     * 비밀번호 찾기 시 본인 인증 후 정해진 이메일로 메일 발송
     *
     * @param employeeNumber 직원 사번
     * @param email tbl_employee에 담긴 email
     */
    @Transactional
    public void issueAndSendPasswordResetToken(String employeeNumber, String email) {
        // 1. 직원 정보 확인
        Employee employee = employeeRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        // 2. 이메일 일치 여부 확인
        if (!email.equals(encryptionUtil.decrypt(employee.getEmail()))) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이메일 정보가 일치하지 않습니다.");
        }

        // 3. 비밀번호 재설정 토큰 생성
        String token = jwtUtil.createPasswordResetToken(employee.getEmployeeNumber());

        // 4. 이메일 발송
        sendPasswordResetEmail(email, token);
    }

    /**
     * 비밀번호 찾기 시 본인 인증 후 정해진 이메일로 메일 발송
     *
     * @param token 메일로 발송된 토큰
     * @param newPassword 새로 정한 비밀번호
     */
    @Transactional
    public void resetPasswordWithToken(String token, String newPassword) {
        // 1. 토큰 검증
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "유효하지 않은 토큰입니다.");
        }
        // 추가 검증: 토큰 타입이 PASSWORD_RESET인지 확인하면 더 안전함

        // 2. 토큰에서 사번 추출
        String employeeNumber = jwtUtil.getUsername(token);

        // 3. 계정 조회 및 비밀번호 변경
        Account account = accountRepository.findByEmployee_EmployeeNumber(employeeNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        account.changePassword(passwordEncoder.encode(newPassword));
    }

    private void sendPasswordResetEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("[Hero 시스템] 비밀번호 재설정 안내");

            // 주의: 프론트엔드 URL로 변경 필요!!
            String resetLink = "http://hero-hr.site/reset-password?token=" + token;

            String emailContent = String.format(
                    "안녕하세요, Hero 시스템입니다.<br><br>" +
                            "비밀번호를 재설정하려면 아래 링크를 클릭하세요.<br>" +
                            "링크는 10분간 유효합니다.<br><br>" +
                            "<a href=\"%s\">비밀번호 재설정하기</a><br><br>" +
                            "감사합니다.",
                    resetLink
            );
            helper.setText(emailContent, true);

            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new BusinessException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
}