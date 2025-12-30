package com.c4.hero.domain.department.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * <pre>
 * Class Name: OrganizationEmployeeDetailDTO
 * Description: 조직도에서 보여줄 직원 정보를 담는 DTO
 *
 * History
 * 2025/12/29 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationEmployeeDetailDTO {

    private Integer employeeId;
    private String employeeName;
    private String employeeNumber;  // 사번
    private Integer gradeId;        // 직급 ID
    private String gradeName;       // 직급명
    private Integer jobTitleId;     // 직책 ID
    private String jobTitleName;    // 직책명
    private String imagePath;       // 프로필 이미지 경로
    
    private String email;          // 이메일 (복호화된 값)
    private LocalDate birthDate;   // 생년월일
    private String gender;         // 성별
    private LocalDate hireDate;    // 입사일
    private String contractType;   // 고용 형태
    private String status;         // 재직 상태
}
