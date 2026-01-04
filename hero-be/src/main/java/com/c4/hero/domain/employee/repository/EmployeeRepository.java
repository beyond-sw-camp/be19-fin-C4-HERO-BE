package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.type.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * <pre>
 * Interface Name: EmployeeRepository
 * Description: Employee 엔티티에 대한 데이터 접근을 위한 Repository
 *
 * History
 * 2025/12/09 (승건) 최초 작성
 * 2025/12/19 (승건) 승진 후보자 조회 추가
 * 2025/12/22 (혜원) 알림 관련 필요한 엔티티 조회 기능 추가
 * 2025/12/28 (승건) 사번으로 직원 조회 기능 추가
 * 2025/12/31 (승건) 퇴사자 처리를 위한 조건 검색 기능 추가
 * </pre>
 *
 * @author 이승건
 * @version 2.1
 */
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    /**
     * 사번으로 직원을 조회합니다.
     *
     * @param employeeNumber 사번
     * @return Optional<Employee>
     */
    Optional<Employee> findByEmployeeNumber(String employeeNumber);

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
           "AND (:gradeIds IS NULL OR e.grade.gradeId IN :gradeIds) " +
           "AND (:jobTitleIds IS NULL OR e.jobTitle.jobTitleId IN :jobTitleIds)")
    List<Employee> findEmployeesByGroupConditions(
            @Param("departmentIds") List<Integer> departmentIds,
            @Param("gradeIds") List<Integer> gradeIds,
            @Param("jobTitleIds") List<Integer> jobTitleIds);

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
            "  AND e.status = com.c4.hero.domain.employee.type.EmployeeStatus.ACTIVE " +
            "ORDER BY e.evaluationPoint DESC")
    List<Employee> findPromotionCandidates(
            @Param("departmentIds") List<Integer> departmentIds,
            @Param("candidateGradeId") Integer candidateGradeId,
            @Param("requiredPoint") Integer requiredPoint
    );

    /**
     * 퇴사일이 특정 날짜 이전(포함)이고, 상태가 특정 상태가 아닌 직원을 조회합니다.
     *
     * @param date 기준 날짜 (이 날짜 포함 이전)
     * @param status 제외할 상태 (이미 퇴직 처리된 직원 제외용)
     * @return 직원 목록
     */
    List<Employee> findAllByTerminationDateBeforeAndStatusNot(LocalDate date, EmployeeStatus status);

    /**
     * 개인정보 보관 만료일이 특정 날짜 이전(포함)인 직원을 조회합니다.
     *
     * @param date 기준 날짜
     * @return 직원 목록
     */
    List<Employee> findAllByRetentionExpireAtBefore(LocalDate date);
}
