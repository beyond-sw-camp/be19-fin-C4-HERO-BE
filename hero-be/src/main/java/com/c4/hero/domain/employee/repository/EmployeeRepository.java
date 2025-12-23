package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.Employee;
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
 * 2025/12/09 승건 최초 작성
 * 2025/12/19 승건 승진 후보자 조회 추가
 * </pre>
 *
 * @author 이승건
 * @version 1.0
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

    /**
     * 주어진 부서 ID 목록에 속하는 모든 직원을 조회합니다.
     *
     * @param departmentIds 부서 ID 목록
     * @return 직원 목록
     */
    List<Employee> findAllByEmployeeDepartment_DepartmentIdIn(List<Integer> departmentIds);

    /**
     * 주어진 직급 ID 목록에 속하는 모든 직원을 조회합니다.
     *
     * @param gradeIds 직급 ID 목록
     * @return 직원 목록
     */
    List<Employee> findAllByGrade_GradeIdIn(List<Integer> gradeIds);

    /**
     * 주어진 직급 ID 목록에 속한 모든 직원의 직급을 null로 일괄 업데이트합니다.
     *
     * @param gradeIds 변경 대상이 되는 기존 직급 ID 목록
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Employee e SET e.grade = null WHERE e.grade.gradeId IN :gradeIds")
    void updateGradeByGradeIds(@Param("gradeIds") List<Integer> gradeIds);

    /**
     * 조건에 맞는 승진 후보자들을 조회합니다.
     *
     * @param departmentIds    후보자가 속한 부서 ID 목록 (하위 부서 포함)
     * @param candidateGradeId 후보자의 현재 직급 ID
     * @param requiredPoint    승진에 필요한 최소 평가 점수
     * @return 조건에 맞는 직원 목록 (평가 점수 내림차순 정렬)
     */
    @Query("SELECT e " +
            " FROM Employee e " +
            "WHERE e.employeeDepartment.departmentId IN :departmentIds " +
            "  AND e.grade.gradeId = :candidateGradeId " +
            "  AND e.evaluationPoint >= :requiredPoint " +
            "ORDER BY e.evaluationPoint DESC")
    List<Employee> findPromotionCandidates(
            @Param("departmentIds") List<Integer> departmentIds,
            @Param("candidateGradeId") Integer candidateGradeId,
            @Param("requiredPoint") Integer requiredPoint
    );
}
