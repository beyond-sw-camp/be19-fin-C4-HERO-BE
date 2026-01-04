package com.c4.hero.domain.settings.repository;

import com.c4.hero.domain.settings.entity.SettingsDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * <pre>
 * Class Name: SettingsDepartmentRepository
 * Description: 부서 설정 관련 JPA Repository
 *
 * History
 * 2025/12/16 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
public interface SettingsDepartmentRepository extends JpaRepository<SettingsDepartment, Integer> {

	/**
	 * 가장 큰 부서 ID 조회
	 *
	 * @return 가장 큰 부서 ID
	 */
	@Query("SELECT MAX(d.departmentId) FROM SettingsDepartment d")
	Integer findMaxDepartmentId();

	/**
	 * 특정 부서 ID 목록에 포함되지 않는 부서 목록 조회
	 *
	 * @param departmentIds 제외할 부서 ID 목록
	 * @return 부서 목록
	 */
	List<SettingsDepartment> findAllByDepartmentIdNotIn(List<Integer> departmentIds);

    /**
     * 부서 ID 0 이상 부서목록 조회
     *
     * @param adminId
     * @return  부서 목록
     */
    List<SettingsDepartment> findByDepartmentIdGreaterThan(int adminId);
}
