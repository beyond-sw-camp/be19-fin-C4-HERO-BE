package com.c4.hero.domain.employee.controller;

import com.c4.hero.common.response.ApiResponse;
import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.auth.security.JwtUtil;
import com.c4.hero.domain.employee.dto.request.PasswordChangeRequestDTO;
import com.c4.hero.domain.employee.dto.response.DepartmentHistoryResponseDTO;
import com.c4.hero.domain.employee.dto.response.EmployeeDetailResponseDTO;
import com.c4.hero.domain.employee.dto.request.EmployeeSearchDTO;
import com.c4.hero.domain.employee.dto.request.EmployeeSelfUpdateRequestDTO;
import com.c4.hero.domain.employee.dto.request.EmployeeUpdateRequestDTO;
import com.c4.hero.domain.employee.dto.response.EmployeeListResponseDTO;
import com.c4.hero.domain.employee.dto.response.GradeHistoryResponseDTO;
import com.c4.hero.domain.employee.dto.response.LoginHistoryResponseDTO;
import com.c4.hero.domain.employee.dto.request.SignupRequestDTO;
import com.c4.hero.domain.employee.dto.response.MyInfoResponseDTO;
import com.c4.hero.domain.employee.service.EmployeePasswordService;
import com.c4.hero.domain.employee.service.EmployeeCommandService;
import com.c4.hero.domain.employee.service.EmployeeQueryService;
import com.c4.hero.domain.employee.service.EmployeeCommandService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    private final EmployeeCommandService employeeCommandService;
    private final EmployeePasswordService employeePasswordService;
    private final EmployeeQueryService employeeQueryService;

    private final JwtUtil jwtUtil;
    /**
     * 직원 회원가입
     *
     * @param request 회원가입 요청 정보 DTO
     * @return 성공 시 ApiResponse<Void>
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequestDTO request) {
        employeeCommandService.signup(request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 직원 정보 검색 및 페이징 조회(재직자들만)
     *
     * @param searchDTO 검색 및 페이징 조건
     * @return 페이징된 직원 정보
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeListResponseDTO>>> search(@ModelAttribute EmployeeSearchDTO searchDTO) {
        PageResponse<EmployeeListResponseDTO> result = employeeQueryService.getEmployees(searchDTO);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 직원 상세 정보 조회(단건)
     *
     * @param 직원 번호(db 인조키)
     * @return 직원의 상세 정보
     */
    @GetMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeDetailResponseDTO>> getEmployeeDetail(@PathVariable Integer employeeId) {
        EmployeeDetailResponseDTO result = employeeQueryService.findById(employeeId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 내 정보 조회 (로그인한 사용자)
     *
     * @param request HttpServletRequest(여기서 액세서 토큰을 빼서 사용)
     * @return 본인 정보
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyInfoResponseDTO>> getMyInfo(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Integer currentEmployeeId = jwtUtil.getEmployeeId(token);
        MyInfoResponseDTO result = employeeQueryService.getMyInfo(currentEmployeeId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 직원 정보 수정 (인사 직원 사용)
     */
    @PutMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<Void>> updateEmployee(HttpServletRequest request, @PathVariable Integer employeeId, @RequestBody EmployeeUpdateRequestDTO requestDTO) {
        String token = jwtUtil.resolveToken(request);
        // TODO: 토큰에서 사용자 정보(관리자 권한) 확인
        // employeeCommandService.updateEmployee(employeeId, requestDTO);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 직원 정보 수정 (본인 사용)
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Void>> selfUpdateEmployee(HttpServletRequest request, @RequestBody EmployeeSelfUpdateRequestDTO requestDTO) {
        String token = jwtUtil.resolveToken(request);
        Integer currentEmployeeId = jwtUtil.getEmployeeId(token);
        // employeeCommandService.selfUpdateEmployee(currentEmployeeId, requestDTO);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 퇴사한 사람들
     */
    @GetMapping("/leave")
    public ResponseEntity<ApiResponse<EmployeeListResponseDTO>> getResignedEmployees() {

        //
        return null;
    }

    /**
     * 직원 퇴사 처리
     */
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<Void>> terminateEmployee(@PathVariable Integer employeeId) {
        // employeeCommandService.terminateEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 로그인 로그 확인
     */
    @GetMapping("/login-log/{employeeId}")
    public ResponseEntity<ApiResponse<List<LoginHistoryResponseDTO>>> getLoginHistory(@PathVariable Integer employeeId) {
        // List<LoginHistoryResponseDTO> history = employeeQueryService.getLoginHistory(employeeId);
        // return ResponseEntity.ok(ApiResponse.success(history));
        return null; // 임시
    }

    /**
     * 부서 이동 로그 확인
     */
    @GetMapping("/department-log/{employeeId}")
    public ResponseEntity<ApiResponse<List<DepartmentHistoryResponseDTO>>> getDepartmentHistory(@PathVariable Integer employeeId) {
        // List<DepartmentHistoryResponseDTO> history = employeeQueryService.getDepartmentHistory(employeeId);
        // return ResponseEntity.ok(ApiResponse.success(history));
        return null; // 임시
    }

    /**
     * 직급 로그 확인
     */
    @GetMapping("/grade-log/{employeeId}")
    public ResponseEntity<ApiResponse<List<GradeHistoryResponseDTO>>> getGradeHistory(@PathVariable Integer employeeId) {
        // List<GradeHistoryResponseDTO> history = employeeQueryService.getGradeHistory(employeeId);
        // return ResponseEntity.ok(ApiResponse.success(history));
        return null; // 임시
    }

    /**
     * 비밀번호 변경 인증을 위한 로직
     * 정해진 메일로 인증 번호 보내고
     * 인증되면 처리
     *
     * EmployeePasswordChangeServicec에서 처리
     */
    @PostMapping("/change-password-auth")
    public ResponseEntity<ApiResponse<Void>> authChangePassword(@Valid @RequestBody SignupRequestDTO request) {
        // passwordChangeService.sendAuthCode(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 비밀번호 재설정
     *
     * EmployeePasswordChangeServicec에서 처리
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody PasswordChangeRequestDTO request) {
        // passwordChangeService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
