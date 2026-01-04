package com.c4.hero.domain.employee.controller;

import com.c4.hero.common.response.CustomResponse;
import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.auth.security.CustomUserDetails;
import com.c4.hero.domain.auth.security.JwtUtil;
import com.c4.hero.domain.employee.dto.request.ContactUpdateRequestDTO;
import com.c4.hero.domain.employee.dto.request.SealTextUpdateRequestDTO;
import com.c4.hero.domain.employee.dto.response.EmployeeProfileResponseDTO;
import com.c4.hero.domain.employee.dto.response.EmployeeSearchOptionsResponseDTO;
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
import com.c4.hero.domain.employee.service.EmployeeProfileQueryService;
import com.c4.hero.domain.employee.service.EmployeeQueryService;
import com.c4.hero.domain.employee.service.EmployeeSealService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <pre>
 * Class Name: EmployeeController
 * Description: 직원 관련 API를 처리하는 컨트롤러
 *
 * History
 * 2025/12/09 (승건) 최초 작성 (사원 추가 기능 개발)
 * 2025/12/28 (혜원) 프로필 관련 API 추가
 * </pre>
 *
 * @author 승건
 * @version 2.0
 */
@Slf4j
@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeCommandService employeeCommandService;
    private final EmployeePasswordService employeePasswordService;
    private final EmployeeQueryService employeeQueryService;
    private final EmployeeProfileQueryService employeeProfileQueryService;
    private final EmployeeSealService employeeSealService;

    private final JwtUtil jwtUtil;
    /**
     * 직원 회원가입
     *
     * @param request 회원가입 요청 정보 DTO
     * @return 성공 시 ApiResponse<Void>
     */
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomResponse<Void>> signup(
            @Valid @ModelAttribute SignupRequestDTO request
    ) {
        employeeCommandService.signup(request);
        return ResponseEntity.ok(CustomResponse.success());
    }

    /**
     * 직원 정보 검색 및 페이징 조회(재직자들만)
     *
     * @param searchDTO 검색 및 페이징 조건
     * @return 페이징된 직원 정보
     */
    @GetMapping("/search")
    public ResponseEntity<CustomResponse<PageResponse<EmployeeListResponseDTO>>> search(@ModelAttribute EmployeeSearchDTO searchDTO) {
        PageResponse<EmployeeListResponseDTO> result = employeeQueryService.getEmployees(searchDTO);
        return ResponseEntity.ok(CustomResponse.success(result));
    }


    /**
     * 직원 검색에 사용될 옵션(부서, 직급, 직책 목록)을 조회합니다.
     *
     * @return 검색 옵션 목록
     */
    @GetMapping("/search-options")
    public ResponseEntity<CustomResponse<EmployeeSearchOptionsResponseDTO>> getEmployeeSearchOptions() {
        EmployeeSearchOptionsResponseDTO result = employeeQueryService.getEmployeeSearchOptions();
        return ResponseEntity.ok(CustomResponse.success(result));
    }


    /**
     * 직원 상세 정보 조회(단건)
     *
     * @param employeeId 번호(db 인조키)
     * @return 직원의 상세 정보
     */
    @GetMapping("/{employeeId}")
    public ResponseEntity<CustomResponse<EmployeeDetailResponseDTO>> getEmployeeDetail(@PathVariable Integer employeeId) {
        EmployeeDetailResponseDTO result = employeeQueryService.findById(employeeId);
        return ResponseEntity.ok(CustomResponse.success(result));
    }

    /**
     * 내 정보 조회 (로그인한 사용자)
     *
     * @param request HttpServletRequest(여기서 액세서 토큰을 빼서 사용)
     * @return 본인 정보
     */
    @GetMapping("/me")
    public ResponseEntity<CustomResponse<MyInfoResponseDTO>> getMyInfo(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Integer currentEmployeeId = jwtUtil.getEmployeeId(token);
        MyInfoResponseDTO result = employeeQueryService.getMyInfo(currentEmployeeId);
        return ResponseEntity.ok(CustomResponse.success(result));
    }

    /**
     * 직원 정보 수정 (인사 직원 사용)
     */
    @PutMapping("/{employeeId}")
    public ResponseEntity<CustomResponse<Void>> updateEmployee(HttpServletRequest request, @PathVariable Integer employeeId, @RequestBody EmployeeUpdateRequestDTO requestDTO) {
        String token = jwtUtil.resolveToken(request);
        // TODO: 토큰에서 사용자 정보(관리자 권한) 확인
        // employeeCommandService.updateEmployee(employeeId, requestDTO);
        return ResponseEntity.ok(CustomResponse.success());
    }

    /**
     * 직원 정보 수정 (본인 사용)
     */
    @PutMapping("/me")
    public ResponseEntity<CustomResponse<Void>> selfUpdateEmployee(HttpServletRequest request, @RequestBody EmployeeSelfUpdateRequestDTO requestDTO) {
        String token = jwtUtil.resolveToken(request);
        Integer currentEmployeeId = jwtUtil.getEmployeeId(token);
        // employeeCommandService.selfUpdateEmployee(currentEmployeeId, requestDTO);
        return ResponseEntity.ok(CustomResponse.success());
    }

    /**
     * 퇴사한 사람들
     */
    @GetMapping("/leave")
    public ResponseEntity<CustomResponse<EmployeeListResponseDTO>> getResignedEmployees() {

        //
        return null;
    }

    /**
     * 직원 퇴사 처리
     */
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<CustomResponse<Void>> terminateEmployee(@PathVariable Integer employeeId) {
        // employeeCommandService.terminateEmployee(employeeId);
        return ResponseEntity.ok(CustomResponse.success());
    }

    /**
     * 로그인 로그 확인
     */
    @GetMapping("/login-log/{employeeId}")
    public ResponseEntity<CustomResponse<List<LoginHistoryResponseDTO>>> getLoginHistory(@PathVariable Integer employeeId) {
        // List<LoginHistoryResponseDTO> history = employeeQueryService.getLoginHistory(employeeId);
        // return ResponseEntity.ok(ApiResponse.success(history));
        return null; // 임시
    }

    /**
     * 부서 이동 로그 확인
     */
    @GetMapping("/department-log/{employeeId}")
    public ResponseEntity<CustomResponse<List<DepartmentHistoryResponseDTO>>> getDepartmentHistory(@PathVariable Integer employeeId) {
        // List<DepartmentHistoryResponseDTO> history = employeeQueryService.getDepartmentHistory(employeeId);
        // return ResponseEntity.ok(ApiResponse.success(history));
        return null; // 임시
    }

    /**
     * 직급 로그 확인
     */
    @GetMapping("/grade-log/{employeeId}")
    public ResponseEntity<CustomResponse<List<GradeHistoryResponseDTO>>> getGradeHistory(@PathVariable Integer employeeId) {
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
    public ResponseEntity<CustomResponse<Void>> authChangePassword(@Valid @RequestBody SignupRequestDTO request) {
        // passwordChangeService.sendAuthCode(request.getEmail());
        return ResponseEntity.ok(CustomResponse.success());
    }

    /**
     * 비밀번호 재설정
     *
     * EmployeePasswordChangeServicec에서 처리
     */
    @PostMapping("/change-password")
    public ResponseEntity<CustomResponse<Void>> changePassword(@Valid @RequestBody PasswordChangeRequestDTO request) {
        // passwordChangeService.changePassword(request);
        return ResponseEntity.ok(CustomResponse.success());
    }

    /**
     * 직원 프로필 관련
     * @author 혜원
     * @since 2025/12/28 */

    /**
     * 현재 로그인한 사용자의 프로필 조회
     *
     * @param userDetails Security Context에서 자동 주입
     * @return 본인 프로필 정보
     */
    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필 정보 조회 (직인 Presigned URL 포함)")
    @GetMapping("/profile")
    public ResponseEntity<CustomResponse<EmployeeProfileResponseDTO>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("프로필 조회 - employeeId: {}", employeeId);

        EmployeeProfileResponseDTO profile = employeeProfileQueryService.getProfileByEmployeeId(employeeId);
        return ResponseEntity.ok(CustomResponse.success(profile));
    }

    /**
     * 사원번호로 프로필 조회
     *
     * @param employeeNumber 사원번호
     * @return 직원 프로필 정보
     */
    @Operation(summary = "사원번호로 프로필 조회", description = "사원번호를 통한 직원 프로필 조회")
    @GetMapping("/number/{employeeNumber}")
    public ResponseEntity<CustomResponse<EmployeeProfileResponseDTO>> getEmployeeProfileByNumber(
            @PathVariable String employeeNumber) {

        log.info("사원번호로 프로필 조회 - employeeNumber: {}", employeeNumber);

        EmployeeProfileResponseDTO profile = employeeProfileQueryService.getProfileByEmployeeNumber(employeeNumber);
        return ResponseEntity.ok(CustomResponse.success(profile));
    }

    // ==================== 연락처 정보 ====================

    /**
     * 연락처 정보 수정
     *
     * @param userDetails Security Context에서 자동 주입
     * @param requestDTO 수정할 연락처 정보
     * @return 성공 응답
     */
    @Operation(summary = "연락처 정보 수정", description = "현재 로그인한 사용자의 연락처 정보 수정 (AES 암호화)")
    @PutMapping("/contact")
    public ResponseEntity<CustomResponse<Void>> updateContactInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ContactUpdateRequestDTO requestDTO) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("연락처 정보 수정 - employeeId: {}", employeeId);

        employeeProfileQueryService.updateContactInfo(employeeId, requestDTO);
        return ResponseEntity.ok(CustomResponse.success());
    }

    // ==================== 비밀번호 ====================

    /**
     * 비밀번호 변경
     *
     * @param userDetails Security Context에서 자동 주입
     * @param requestDTO 비밀번호 변경 정보
     * @return 성공 응답
     */
    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 확인 후 새 비밀번호로 변경 (BCrypt 암호화)")
    @PutMapping("/password")
    public ResponseEntity<CustomResponse<Void>> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PasswordChangeRequestDTO requestDTO) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("비밀번호 변경 - employeeId: {}", employeeId);

        employeePasswordService.changePassword(employeeId, requestDTO);
        return ResponseEntity.ok(CustomResponse.success());
    }

    // ==================== 직인 관리 ====================

    /**
     * 텍스트 직인 업데이트
     *
     * @param userDetails Security Context에서 자동 주입
     * @param requestDTO 텍스트 직인 정보
     * @return 성공 응답
     */
    @Operation(summary = "텍스트 직인 저장", description = "텍스트를 이미지로 생성하여 S3에 업로드")
    @PutMapping("/seal/text")
    public ResponseEntity<CustomResponse<Void>> updateSealText(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SealTextUpdateRequestDTO requestDTO) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("텍스트 직인 업데이트 - employeeId: {}, text: {}", employeeId, requestDTO.getSealText());

        employeeSealService.updateSealText(employeeId, requestDTO);
        return ResponseEntity.ok(CustomResponse.success());
    }

    /**
     * 이미지 직인 업로드
     *
     * @param userDetails Security Context에서 자동 주입
     * @param file 직인 이미지 파일 (PNG, JPG, 5MB 이하)
     * @return 성공 응답
     */
    @Operation(summary = "이미지 직인 업로드", description = "직인 이미지를 S3에 업로드 (기존 직인 삭제)")
    @PostMapping("/seal/image")
    public ResponseEntity<CustomResponse<Void>> uploadSealImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("이미지 직인 업로드 - employeeId: {}, filename: {}", employeeId, file.getOriginalFilename());

        employeeSealService.uploadSealImage(employeeId, file);
        return ResponseEntity.ok(CustomResponse.success());
    }

    /**
     * 직인 삭제
     *
     * @param userDetails Security Context에서 자동 주입
     * @return 성공 응답
     */
    @Operation(summary = "직인 삭제", description = "S3 파일 및 DB 레코드 삭제")
    @DeleteMapping("/seal")
    public ResponseEntity<CustomResponse<Void>> deleteSeal(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("직인 삭제 - employeeId: {}", employeeId);

        employeeSealService.deleteSeal(employeeId);
        return ResponseEntity.ok(CustomResponse.success());
    }

    /**
     * 내 직인 자동 생성
     * 직인이 없을 때 이름으로 직인 자동 생성 (이미 있으면 skip)
     *
     * @param userDetails Security Context에서 자동 주입
     * @return 성공 응답
     * @author 혜원
     */
    @Operation(summary = "내 직인 자동 생성", description = "직인이 없을 때 이름으로 직인 자동 생성 (이미 있으면 skip)")
    @PostMapping("/seal/generate")
    public ResponseEntity<CustomResponse<Void>> generateMySeal(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("직인 자동 생성 요청 - employeeId: {}", employeeId);

        employeeSealService.generateSealForEmployee(employeeId);

        return ResponseEntity.ok(CustomResponse.success());
    }

    /**
     * 내 직인 이미지 조회
     * Security Context에서 자동으로 사용자 정보 추출
     *
     * @param userDetails Security Context에서 자동 주입
     * @return 직인 이미지 Presigned URL
     */
    @Operation(summary = "내 직인 이미지 조회", description = "현재 로그인한 사용자의 직인 이미지 URL 조회")
    @GetMapping("/seal")
    public ResponseEntity<CustomResponse<String>> getMySeal(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Integer employeeId = userDetails.getEmployeeId();
        log.info("내 직인 조회 - employeeId: {}", employeeId);

        String sealImageUrl = employeeSealService.getEmployeeSealUrl(employeeId);

        return ResponseEntity.ok(CustomResponse.success(sealImageUrl));
    }
}
