package com.c4.hero.domain.employee.mapper;

import com.c4.hero.domain.employee.dto.request.ContactUpdateRequestDTO;
import com.c4.hero.domain.employee.dto.request.EmployeeSearchDTO;
import com.c4.hero.domain.employee.dto.response.EmployeeProfileResponseDTO;
import com.c4.hero.domain.employee.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <pre>
 * Interface Name: EmployeeMapper
 * Description: 직원 관련 데이터베이스 접근 Mapper
 *
 * History
 * 2025/12/09 (승건) 최초 작성
 * 2025/12/28 (혜원) 프로필 관련 메서드 추가
 * 2025/12/30 (승건) Mapper 메소드에 SecretKey 추가
 * </pre>
 *
 * @author 승건
 * @version 1.1
 */
@Mapper
public interface EmployeeMapper {
    /**
     * 검색 조건과 페이징을 적용하여 직원 목록을 조회합니다.
     *
     * @param searchDTO 검색 및 페이징 조건
     * @return 직원 엔티티 목록
     */
    List<Employee> findWithPaging(EmployeeSearchDTO searchDTO);

    /**
     * 검색 조건에 맞는 직원의 총 수를 조회합니다.
     *
     * @param searchDTO 검색 조건
     * @return 총 직원 수
     */
    int count(EmployeeSearchDTO searchDTO);

    /**
     * ID로 직원을 조회합니다.
     *
     * @param employeeId 직원 ID
     * @return 직원 엔티티 (Optional)
     */
    Optional<Employee> findById(Integer employeeId);

    /**
     * 모든 부서 이름을 조회합니다.
     *
     * @return 부서 이름 목록
     */
    List<String> findAllDepartmentNames();

    /**
     * 모든 직급 이름을 조회합니다.
     *
     * @return 직급 이름 목록
     */
    List<String> findAllGradeNames();

    /**
     * 모든 직책 이름을 조회합니다.
     *
     * @return 직책 이름 목록
     */
    List<String> findAllJobTitleNames();

    /**
     * 직원 프로필 관련 기능
     * @author 혜원
     * @since 2025/12/28 */

    /**
     * 직원 ID로 프로필 정보 조회
     *
     * @param employeeId 직원 ID
     * @param secretKey 암호화 키
     * @return EmployeeProfileResponseDTO 직원 프로필 정보
     */
    EmployeeProfileResponseDTO findProfileByEmployeeId(@Param("employeeId") Integer employeeId,
                                                       @Param("secretKey") String secretKey);

    /**
     * 사원번호로 프로필 정보 조회
     *
     * @param employeeNumber 사원번호 (예: EMP005)
     * @param secretKey 암호화 키
     * @return EmployeeProfileResponseDTO 직원 프로필 정보
     */
    EmployeeProfileResponseDTO findProfileByEmployeeNumber(@Param("employeeNumber") String employeeNumber,
                                                           @Param("secretKey") String secretKey);

    /**
     * 연락처 정보 수정
     *
     * @param employeeId 직원 ID
     * @param requestDTO 수정할 연락처 정보
     * @param secretKey 암호화 키
     * @return 수정된 행 수
     */
    int updateContactInfo(@Param("employeeId") Integer employeeId,
                          @Param("dto") ContactUpdateRequestDTO requestDTO,
                          @Param("secretKey") String secretKey);

    /**
     * 직원 ID로 비밀번호 조회
     *
     * @param employeeId 직원 ID
     * @return 암호화된 비밀번호
     */
    String findPasswordHashByEmployeeId(@Param("employeeId") Integer employeeId);

    /**
     * 직원 ID로 비밀번호 업데이트
     *
     * @param employeeId 직원 ID
     * @param newPasswordHash 새로운 암호화된 비밀번호
     * @return 수정된 행 수
     */
    int updatePasswordHash(@Param("employeeId") Integer employeeId,
                           @Param("newPasswordHash") String newPasswordHash);

    /**
     * 직원 ID로 직인 이미지 URL 조회
     *
     * @param employeeId 직원 ID
     * @return 직인 이미지 URL
     */
    String findSealImageUrlByEmployeeId(@Param("employeeId") Integer employeeId);

    /**
     * 직인 이미지 URL 업데이트
     *
     * @param employeeId 직원 ID
     * @param sealImageUrl 직인 이미지 URL (null이면 삭제)
     * @return 수정된 행 수
     */
    int updateSealImageUrl(@Param("employeeId") Integer employeeId,
                           @Param("sealImageUrl") String sealImageUrl);

    /**
     * 직원 ID로 이름 조회
     *
     * @param employeeId 직원 ID
     * @return 직원 이름
     */
    String findEmployeeNameById(Integer employeeId);
}