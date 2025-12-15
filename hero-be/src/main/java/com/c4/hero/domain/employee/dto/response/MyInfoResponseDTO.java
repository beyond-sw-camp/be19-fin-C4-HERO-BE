package com.c4.hero.domain.employee.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * <pre>
 * Class Name: MyInfoResponseDTO
 * Description: 내 정보 조회 시 사용되는 데이터 전송 객체
 *
 * History
 * 2025/12/12 승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyInfoResponseDTO {
    private Integer employeeId;
    private String employeeNumber;
    private String employeeName;
    private String gender;
    private String imagePath;       // 프로필 사진 경로
    private String sealImageUrl;    // 직인 이미지 경로
    private LocalDate birthDate;
    private String contractType;
    private String jobTitleName;
    private String gradeName;
    private String departmentName;
    private String departmentPath;  // 부서 경로
    private String email;
    private String phone;
    private String address;
    private LocalDate hireDate;
    private LocalDate terminationDate;
    private Long daysOfService; // 근속 연수
    private Integer baseSalary;     // 기본급
    private String status;          // 재직 상태
}
