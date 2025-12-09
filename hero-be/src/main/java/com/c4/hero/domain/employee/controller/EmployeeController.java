package com.c4.hero.domain.employee.controller;

import com.c4.hero.common.response.ApiResponse;
import com.c4.hero.domain.employee.dto.SignupRequestDTO;
import com.c4.hero.domain.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 * Class Name: EmployeeController
 * Description: 직원 관련 API를 처리하는 컨트롤러
 *
 * History
 * 2025/12/09 이승건 최초 작성 (사원 추가 기능 개발)
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * 직원 회원가입
     *
     * @param request 회원가입 요청 정보 DTO
     * @return 성공 시 ApiResponse<Void>
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequestDTO request) {
        employeeService.signup(request);
        return ResponseEntity.ok(ApiResponse.success());
    }
}