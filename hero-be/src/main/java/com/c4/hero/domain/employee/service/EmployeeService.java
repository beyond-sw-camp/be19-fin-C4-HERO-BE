package com.c4.hero.domain.employee.service;

import com.c4.hero.domain.employee.dto.SignupRequestDTO;

/**
 * <pre>
 * Class Name: EmployeeService
 * Description: 직원 관련 비즈니스 로직을 정의하는 인터페이스
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */

public interface EmployeeService {

    /**
     * 직원 회원가입 처리
     *
     * @param request 회원가입 요청 DTO
     */
    void signup(SignupRequestDTO request);
}
