package com.c4.hero.domain.approval.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name  : OrganizationEmployeeDTO
 * Description : 직원 정보 DTO
 *
 * History
 * 2025/12/26 (민철) 최초 작성
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationEmployeeDTO {

    /** 직원 ID */
    private Integer employeeId;

    /** 직원명 */
    private String employeeName;

    /** 부서 ID */
    private Integer departmentId;

    /** 부서명 */
    private String departmentName;

    /** 직급 ID */
    private Integer gradeId;

    /** 직급명 */
    private String gradeName;

    /** 직책 ID */
    private Integer jobTitleId;

    /** 직책명 */
    private String jobTitleName;

    /** 이메일 */
    private String email;

    /** 전화번호 */
    private String phone;
}