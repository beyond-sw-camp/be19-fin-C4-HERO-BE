package com.c4.hero.domain.department.controller;

import com.c4.hero.common.response.CustomResponse;
import com.c4.hero.domain.department.dto.DepartmentDTO;
import com.c4.hero.domain.department.dto.EmployeeDepartmentHistoryDTO;
import com.c4.hero.domain.department.dto.EmployeeGradeHistoryDTO;
import com.c4.hero.domain.department.dto.OrganizationNodeDTO;
import com.c4.hero.domain.department.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <pre>
 * Class Name: DepartmentController
 * Description: 부서(Department) 목록 조회 관련 REST API를 제공하는 컨트롤러
 *
 * History
 * 2025/12/24 (이지윤) 최초 작성 및 백엔드 코딩 컨벤션 적용
 * 2025/12/29 (승건) 조직도 조회 API 추가
 * 2025/12/29 (승건) 부서/직급 이력 조회 API 추가
 * 2026/01/07 (승건) 스웨거 작성
 * </pre>
 *
 * 공통으로 사용되는 부서 셀렉트 박스/필터(근태대시보드, 휴가 캘린더 등)를 위한
 * 부서 전체 목록 조회 API를 제공합니다.
 *
 * @author 이지윤
 * @version 1.2
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
@Tag(name = "부서 API", description = "부서 정보 및 조직도, 이력 조회 API")
public class DepartmentController {

    /** 부서 도메인 관련 비즈니스 로직을 처리하는 서비스 */
    private final DepartmentService departmentService;

    /**
     * 전체 부서 목록을 조회합니다.
     *
     * <p>특징</p>
     * <ul>
     *     <li>사용처: 부서 선택 드롭다운, 필터링 조건 등 공통 UI에서 사용</li>
     *     <li>일반적으로 사용 여부(Y/N), 정렬 순서 등을 고려한 목록을 서비스에서 리턴</li>
     * </ul>
     *
     * @return 부서 정보 DTO 리스트
     */
    @Operation(summary = "전체 부서 목록 조회", description = "부서 선택 드롭다운 등에서 사용할 전체 부서 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = DepartmentDTO.class)))
    })
    @GetMapping
    public List<DepartmentDTO> getDepartments() {
        return departmentService.getDepartments();
    }

    /**
     * 조직도 트리 구조를 조회합니다.
     *
     * <p>특징</p>
     * <ul>
     *     <li>부서 계층 구조와 각 부서에 속한 직원 정보를 포함합니다.</li>
     *     <li>퇴사한 직원은 제외됩니다.</li>
     * </ul>
     *
     * @return 조직도 트리 노드 리스트
     */
    @Operation(summary = "조직도 조회", description = "부서 계층 구조와 각 부서에 속한 직원 정보를 포함한 조직도를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @GetMapping("/organization-chart")
    public ResponseEntity<CustomResponse<List<OrganizationNodeDTO>>> getOrganizationChart() {
        List<OrganizationNodeDTO> organizationChart = departmentService.getOrganizationChart();
        return ResponseEntity.ok(CustomResponse.success(organizationChart));
    }

    /**
     * 특정 직원의 부서 변경 이력을 조회합니다.
     *
     * @param employeeId 직원 ID
     * @return 부서 변경 이력 리스트
     */
    @Operation(summary = "직원 부서 변경 이력 조회", description = "특정 직원의 부서 변경 이력을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @GetMapping("/history/department/{employeeId}")
    public ResponseEntity<CustomResponse<List<EmployeeDepartmentHistoryDTO>>> getDepartmentHistory(@PathVariable Integer employeeId) {
        List<EmployeeDepartmentHistoryDTO> history = departmentService.getDepartmentHistory(employeeId);
        return ResponseEntity.ok(CustomResponse.success(history));
    }

    /**
     * 특정 직원의 직급 변경 이력을 조회합니다.
     *
     * @param employeeId 직원 ID
     * @return 직급 변경 이력 리스트
     */
    @Operation(summary = "직원 직급 변경 이력 조회", description = "특정 직원의 직급 변경 이력을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class)))
    })
    @GetMapping("/history/grade/{employeeId}")
    public ResponseEntity<CustomResponse<List<EmployeeGradeHistoryDTO>>> getGradeHistory(@PathVariable Integer employeeId) {
        List<EmployeeGradeHistoryDTO> history = departmentService.getGradeHistory(employeeId);
        return ResponseEntity.ok(CustomResponse.success(history));
    }
}
