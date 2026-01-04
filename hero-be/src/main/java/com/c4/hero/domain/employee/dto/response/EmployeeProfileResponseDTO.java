package com.c4.hero.domain.employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 * Class Name: EmployeeProfileResponseDTO
 * Description: 직원 프로필 응답 DTO
 *
 * History
 * 2025/12/28 (혜원) 최초 작성
 * 2025/12/28 (혜원) Presigned URL 지원을 위한 Setter 추가
 * 2025/12/29 (혜원) 부서 ID 필드 추가
 * </pre>
 *
 * @author 혜원
 * @version 1.2
 */
@Getter
@Setter  // Presigned URL 설정을 위해 추가
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProfileResponseDTO {

    // 기본 정보
    private Integer employeeId;
    private String employeeNumber;
    private String employeeName;
    private String birthDate;
    private String gender;
    private String status;
    private String contractType;

    // 조직 정보
    private Integer departmentId;  // 부서 ID 추가
    private String department;
    private String team;
    private String position;
    private String rank;

    // 연락처 정보
    private String email;
    private String officePhone;
    private String mobile;
    private String address;

    // 근무 정보
    private String hireDate;
    private Integer yearsOfService;
    private Long salary;
    private String performance;

    // 직인 정보 (Presigned URL)
    private String profileImageUrl;
    private String sealImageUrl;  // DB: S3 키, 응답: Presigned URL
}