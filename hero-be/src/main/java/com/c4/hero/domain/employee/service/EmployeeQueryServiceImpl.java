package com.c4.hero.domain.employee.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.common.response.PageResponse;
import com.c4.hero.common.s3.S3Service;
import com.c4.hero.common.util.EncryptionUtil;
import com.c4.hero.domain.employee.dto.request.EmployeeSearchDTO;
import com.c4.hero.domain.employee.dto.response.EmployeeDetailResponseDTO;
import com.c4.hero.domain.employee.dto.response.EmployeeListResponseDTO;
import com.c4.hero.domain.employee.dto.response.EmployeeSearchOptionsResponseDTO;
import com.c4.hero.domain.employee.dto.response.MyInfoResponseDTO;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <pre>
 * Class Name: EmployeeQueryServiceImpl
 * Description: 직원 정보 조회 관련 서비스 구현체
 *
 * History
 * 2025/12/12 승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EmployeeQueryServiceImpl implements EmployeeQueryService {

    private final EmployeeMapper employeeMapper;
    private final EncryptionUtil encryptionUtil;
    private final S3Service s3Service;

    /*
    * @Transactional(readOnly = true)
    * 1.성능 최적화
    *   •(JPA 사용 시)•JPA(Hibernate)는 엔티티를 조회할 때,
    *   나중에 변경되었는지 확인하기 위해 원본 상태를 복사해두는
    *   **스냅샷(Snapshot)**을 만듭니다.
    *   (Dirty Checking 용도)•readOnly = true를 걸면,
    *   "어차피 변경 안 할 거니까 스냅샷 안 만들어도 돼!"
    *   라고 알려주는 셈이라서 메모리 사용량이 줄고 속도가 빨라집니다.
    * 2.DB 부하 분산 (Replication 사용 시)
    *   •Master-Slave 구조의 DB를 사용한다면,
    *   읽기 전용 트랜잭션은 Slave(읽기 전용) DB로 요청을 보내서
    *   Master DB의 부하를 줄일 수 있습니다.
    * 3.실수 방지
    *   •개발자가 실수로 조회 메소드 안에서 데이터를 수정하는 코드를 넣어도,
    *   DB에 반영되지 않거나 예외가 발생하여 데이터 무결성을 보호할 수 있습니다.
    * */
    @Override
    public PageResponse<EmployeeListResponseDTO> getEmployees(EmployeeSearchDTO searchDTO) {
        // 1. 검색 조건에 맞는 데이터 목록 조회 (페이징 적용)
        List<Employee> employees = employeeMapper.findWithPaging(searchDTO);
        List<EmployeeListResponseDTO> dtoList = employees.stream()
                .map(this::convertToListDto)
                .collect(Collectors.toList());

        // 2. 검색 조건에 맞는 전체 데이터 개수 조회
        int totalCount = employeeMapper.count(searchDTO);

        // 3. PageResponse 객체 생성하여 반환 (page는 0-based index로 변환)
        return PageResponse.of(dtoList, searchDTO.getPage() - 1, searchDTO.getSize(), totalCount);
    }

    @Override
    public EmployeeDetailResponseDTO findById(Integer employeeId) {
        Employee employee = employeeMapper.findById(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));
        return convertToDetailDto(employee);
    }

    @Override
    public MyInfoResponseDTO getMyInfo(Integer employeeId) {
        Employee employee = employeeMapper.findById(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));



        return convertToMyInfoDto(employee);
    }

    @Override
    public EmployeeSearchOptionsResponseDTO getEmployeeSearchOptions() {
        List<String> departments = employeeMapper.findAllDepartmentNames();
        List<String> grades = employeeMapper.findAllGradeNames();
        List<String> jobTitles = employeeMapper.findAllJobTitleNames();

        return EmployeeSearchOptionsResponseDTO.builder()
                .department(departments)
                .grade(grades)
                .jobTitle(jobTitles)
                .build();
    }

    /* =================== private =================== */

    /**
     * employee 엔티티를 EmployeeListResponseDTO로 변환하는 메서드
     *
     * @param employee DB에서 조회한 정보
     * @return 변환된 DTO
     */
    private EmployeeListResponseDTO convertToListDto(Employee employee) {
        return new EmployeeListResponseDTO(
                employee.getEmployeeId(),
                employee.getEmployeeName(),
                employee.getEmployeeNumber(),
                employee.getEmployeeDepartment() != null ? employee.getEmployeeDepartment().getDepartmentName() : null,
                employee.getGrade() != null ? employee.getGrade().getGrade() : null,
                employee.getJobTitle() != null ? employee.getJobTitle().getJobTitle() : null
        );
    }

    /**
     * employee 엔티티를 EmployeeDetailResponseDTO로 변환하는 메서드
     *
     * @param employee DB에서 조회한 정보
     * @return 변환된 DTO
     */
    private EmployeeDetailResponseDTO convertToDetailDto(Employee employee) {
        String decryptedEmail = employee.getEmail() != null ? encryptionUtil.decrypt(employee.getEmail()) : null;
        log.info("이메일 복호화 완료: {}", decryptedEmail);
        String decryptedPhone = employee.getPhone() != null ? encryptionUtil.decrypt(employee.getPhone()) : null;
        log.info("폰 번호 복호화 완료: {}", decryptedPhone);
        String decryptedAddress = employee.getAddress() != null ? encryptionUtil.decrypt(employee.getAddress()) : null;
        log.info("주소 복호화 완료: {}", decryptedAddress);

        // 근속 일수 계산
        long daysOfService = 0;
        if (employee.getHireDate() != null) {
            LocalDate endDate = (employee.getTerminationDate() != null) ? employee.getTerminationDate() : LocalDate.now();
            daysOfService = ChronoUnit.DAYS.between(employee.getHireDate(), endDate) + 1;
        }

        // S3 URL 변환
        String imageUrl = s3Service.generatePresignedUrl(employee.getImagePath());
        String sealUrl = s3Service.generatePresignedUrl(employee.getSealImageUrl());

        return EmployeeDetailResponseDTO.builder()
                .employeeId(employee.getEmployeeId())
                .employeeNumber(employee.getEmployeeNumber())
                .employeeName(employee.getEmployeeName())
                .gender(employee.getGender())
                .evaluationPoint(employee.getEvaluationPoint())
                .imagePath(imageUrl)
                .sealImageUrl(sealUrl)
                .birthDate(employee.getBirthDate())
                .contractType(employee.getContractType())
                .jobTitleName(employee.getJobTitle() != null ? employee.getJobTitle().getJobTitle() : null)
                .gradeName(employee.getGrade() != null ? employee.getGrade().getGrade() : null)
                .departmentName(employee.getEmployeeDepartment() != null ? employee.getEmployeeDepartment().getDepartmentName() : null)
                .departmentPath(employee.getDepartmentPath())
                .email(decryptedEmail)
                .phone(decryptedPhone)
                .address(decryptedAddress)
                .hireDate(employee.getHireDate())
                .terminationDate((employee.getTerminationDate()))
                .daysOfService(daysOfService)
                .status(employee.getStatus().getDescription())
                .retentionExpireAt(employee.getRetentionExpireAt())
                .build();
    }

    /**
     * employee 엔티티를 MyInfoResponseDTO로 변환하는 메서드
     *
     * @param employee db에서 조회한 정보
     * @return 변환된 DTO
     */
    private MyInfoResponseDTO convertToMyInfoDto(Employee employee) {
        String decryptedEmail = employee.getEmail() != null ? encryptionUtil.decrypt(employee.getEmail()) : null;
        String decryptedPhone = employee.getPhone() != null ? encryptionUtil.decrypt(employee.getPhone()) : null;
        String decryptedAddress = employee.getAddress() != null ? encryptionUtil.decrypt(employee.getAddress()) : null;

        // 근속 일수 계산
        long daysOfService = 0;
        if (employee.getHireDate() != null) {
            LocalDate endDate = (employee.getTerminationDate() != null) ? employee.getTerminationDate() : LocalDate.now();
            daysOfService = ChronoUnit.DAYS.between(employee.getHireDate(), endDate) + 1;
        }

        // S3 URL 변환
        String imageUrl = s3Service.generatePresignedUrl(employee.getImagePath());
        String sealUrl = s3Service.generatePresignedUrl(employee.getSealImageUrl());

        return MyInfoResponseDTO.builder()
                .employeeId(employee.getEmployeeId())
                .employeeNumber(employee.getEmployeeNumber())
                .employeeName(employee.getEmployeeName())
                .imagePath(imageUrl)
                .gender(employee.getGender())
                .sealImageUrl(sealUrl)
                .birthDate(employee.getBirthDate())
                .contractType(employee.getContractType())
                .jobTitleName(employee.getJobTitle() != null ? employee.getJobTitle().getJobTitle() : null)
                .gradeName(employee.getGrade() != null ? employee.getGrade().getGrade() : null)
                .departmentName(employee.getEmployeeDepartment() != null ? employee.getEmployeeDepartment().getDepartmentName() : null)
                .departmentPath(employee.getDepartmentPath())
                .email(decryptedEmail)
                .phone(decryptedPhone)
                .address(decryptedAddress)
                .hireDate(employee.getHireDate())
                .terminationDate((employee.getTerminationDate()))
                .daysOfService(daysOfService)
                .baseSalary(employee.getBaseSalary())
                .status(employee.getStatus().getDescription())
                .build();
    }
}
