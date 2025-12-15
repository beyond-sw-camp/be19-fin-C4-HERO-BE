package com.c4.hero.domain.employee.service;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.employee.dto.response.EmployeeDetailResponseDTO;
import com.c4.hero.domain.employee.dto.request.EmployeeSearchDTO;
import com.c4.hero.domain.employee.dto.request.SignupRequestDTO;


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

public interface EmployeeCommandService {
    /**
     * 새로운 직원을 등록합니다.
     *
     * @param request 등록할 직원의 정보
     * @throws com.c4.hero.common.exception.BusinessException 중복된 사번, 이메일, 전화번호가 존재할 경우
     */
    void signup(SignupRequestDTO request);
}
