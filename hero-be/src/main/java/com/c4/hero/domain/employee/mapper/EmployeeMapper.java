package com.c4.hero.domain.employee.mapper;

import com.c4.hero.domain.employee.dto.request.EmployeeSearchDTO;
import com.c4.hero.domain.employee.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface EmployeeMapper {
    /**
     * 검색 조건과 페이징을 적용하여 직원 목록을 조회합니다.
     * @param searchDTO 검색 및 페이징 조건
     * @return 직원 엔티티 목록
     */
    List<Employee> findWithPaging(EmployeeSearchDTO searchDTO);

    /**
     * 검색 조건에 맞는 직원의 총 수를 조회합니다.
     * @param searchDTO 검색 조건
     * @return 총 직원 수
     */
    int count(EmployeeSearchDTO searchDTO);

    /**
     * ID로 직원을 조회합니다.
     * @param employeeId 직원 ID
     * @return 직원 엔티티 (Optional)
     */
    Optional<Employee> findById(Integer employeeId);

    /**
     * 모든 부서 이름을 조회합니다.
     * @return 부서 이름 목록
     */
    List<String> findAllDepartmentNames();

    /**
     * 모든 직급 이름을 조회합니다.
     * @return 직급 이름 목록
     */
    List<String> findAllGradeNames();

    /**
     * 모든 직책 이름을 조회합니다.
     * @return 직책 이름 목록
     */
    List<String> findAllJobTitleNames();
}
