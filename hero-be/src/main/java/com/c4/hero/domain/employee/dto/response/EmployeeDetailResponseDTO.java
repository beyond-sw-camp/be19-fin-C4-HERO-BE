package com.c4.hero.domain.employee.dto.response;

import com.c4.hero.domain.employee.type.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * <pre>
 * Class Name: EmployeeDetailResponseDTO
 * Description: 직원 상세 정보 조회 시 사용되는 데이터 전송 객체
 *
 * History
 * 2025/12/11 승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Getter
@Builder
public class EmployeeDetailResponseDTO {
    private Integer employeeId;
    private String employeeName;
    private String employeeNumber;
    private String email;
    private String phone;
    private String contractType;
    private String gender;
    private LocalDate hireDate;
    private LocalDate terminationDate;
    private LocalDate retentionExpireAt;
    private LocalDate birthDate;
    private String sealImageUrl;
    private String imagePath;
    private String address;
    private String departmentName;
    private String departmentPath; // 부서 전체 경로 (예: 본부 > 개발팀)
    private Long daysOfService; // 근속 연수
    private String gradeName;
    private String jobTitleName;
    private BigDecimal evaluationPoint;
    private String status;
}
