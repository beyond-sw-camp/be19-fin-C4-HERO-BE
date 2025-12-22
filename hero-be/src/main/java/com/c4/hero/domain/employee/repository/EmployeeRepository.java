package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.type.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * <pre>
 * Interface Name: EmployeeRepository
 * Description: Employee 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/09 (승건) 최초 작성
 * 2025/12/22 (혜원) 알림 관련 필요한 엔티티 조회 기능 추가
 * </pre>
 *
 * @author 이승건
 * @version 2.0
 */
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    /**
     * 사번, 이메일, 또는 전화번호 중 하나라도 일치하는 직원 엔티티를 조회
     *
     * @param employeeNumber 사번
     * @param email 암호화된 이메일
     * @param phone 암호화된 전화번호
     * @return Optional<Employee>
     */
    Optional<Employee> findByEmployeeNumberOrEmailOrPhone(String employeeNumber, byte[] email, byte[] phone);

    /**
     * 주어진 부서 ID 목록에 속한 모든 직원의 부서를 새로운 부서 ID로 일괄 업데이트합니다.
     *
     * @param newDepartmentId    새롭게 할당될 부서의 ID
     * @param oldDepartmentIds   변경 대상이 되는 기존 부서 ID 목록
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Employee e SET e.employeeDepartment.departmentId = :newDepartmentId WHERE e.employeeDepartment.departmentId IN :oldDepartmentIds")
    void updateDepartmentByDepartmentIds(@Param("newDepartmentId") Integer newDepartmentId, @Param("oldDepartmentIds") List<Integer> oldDepartmentIds);

    List<Employee> findAllByEmployeeDepartment_DepartmentIdIn(List<Integer> departmentIds);

    /**
     * 직급 ID 목록으로 직원 조회
     *
     * @param gradeIds 직급 ID 목록
     * @return 직원 목록
     */
    List<Employee> findAllByGrade_GradeIdIn(List<Integer> gradeIds);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Employee e SET e.grade = null WHERE e.grade.gradeId IN :gradeIds")
    void updateGradeByGradeIds(@Param("gradeIds") List<Integer> gradeIds);

    /**
     * 특정 상태가 아닌 모든 직원 조회
     *
     * @param status 제외할 상태
     * @return 직원 목록
     */
    List<Employee> findAllByStatusNot(EmployeeStatus status);

    /**
     * 직책 ID 목록으로 직원 조회
     *
     * @param jobTitleIds 직책 ID 목록
     * @return 직원 목록
     */
    List<Employee> findAllByJobTitle_JobTitleIdIn(List<Integer> jobTitleIds);

    /**
     * 부서, 직급, 직책 조건에 해당하는 직원 조회 (중복 제거)
     *
     * @param departmentIds 부서 ID 목록 (null 가능)
     * @param gradeIds      직급 ID 목록 (null 가능)
     * @param jobTitleIds   직책 ID 목록 (null 가능)
     * @return 직원 목록
     */
    @Query("SELECT DISTINCT e FROM Employee e " +
           "WHERE (:departmentIds IS NULL OR e.employeeDepartment.departmentId IN :departmentIds) " +
           "OR (:gradeIds IS NULL OR e.grade.gradeId IN :gradeIds) " +
           "OR (:jobTitleIds IS NULL OR e.jobTitle.jobTitleId IN :jobTitleIds)")
    List<Employee> findEmployeesByGroupConditions(
            @Param("departmentIds") List<Integer> departmentIds,
            @Param("gradeIds") List<Integer> gradeIds,
            @Param("jobTitleIds") List<Integer> jobTitleIds);
}
