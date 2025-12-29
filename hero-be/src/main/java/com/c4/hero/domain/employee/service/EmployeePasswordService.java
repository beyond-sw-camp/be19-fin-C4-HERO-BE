package com.c4.hero.domain.employee.service;

import com.c4.hero.domain.employee.dto.request.PasswordChangeRequestDTO;
import com.c4.hero.domain.employee.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}