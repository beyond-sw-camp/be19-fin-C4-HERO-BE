package com.c4.hero.domain.employee.service;

import com.c4.hero.domain.employee.dto.request.SignupRequestDTO;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.type.ChangeType;


/**
 * <pre>
 * Class Name: EmployeeService
 * Description: 직원 관련 비즈니스 로직을 정의하는 인터페이스
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * 2025/12/15 승건 변경 이력 메소드 추가 및 적용
 * </pre>
 *
 * @author 이승건
 * @version 1.1
 */

public interface EmployeeCommandService {
    /**
     * 새로운 직원을 등록합니다.
     *
     * @param request 등록할 직원의 정보
     * @throws com.c4.hero.common.exception.BusinessException 중복된 사번, 이메일, 전화번호가 존재할 경우
     */
    void signup(SignupRequestDTO request);

    /**
     * 직원의 부서에 대한 이력을 추가합니다.
     *
     * @param employee
     * @param changeType
     * @param departmentName
     */
    void addDepartmentHistory(Employee employee, ChangeType changeType, String departmentName);

    /**
     * 직원의 직급에 대한 이력을 추가합니다.
     *
     * @param employee
     * @param changeType
     * @param gradeName
     */
    void addGradeHistory(Employee employee, ChangeType changeType, String gradeName);

    /**
     * 직원의 부서를 변경하고 이력을 저장합니다.
     *
     * @param employeeNumber 사번
     * @param departmentName 변경할 부서명
     */
    void updateDepartment(String employeeNumber, String departmentName);

    /**
     * 직원의 직책을 변경합니다.
     *
     * @param employeeNumber 사번
     * @param jobTitleName 변경할 직책명
     */
    void updateJobTitle(String employeeNumber, String jobTitleName);
}
