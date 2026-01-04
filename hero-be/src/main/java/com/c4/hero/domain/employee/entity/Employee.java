package com.c4.hero.domain.employee.entity;

import com.c4.hero.common.util.EncryptionUtil;
import com.c4.hero.domain.employee.type.EmployeeStatus;
import com.c4.hero.domain.employee.type.converter.EmployeeStatusConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: Employee
 * Description: 직원 정보를 담는 엔티티 클래스
 *
 * History
 * 2025/12/09 승건 최초 작성
 * 2025/12/15 승건 부서, 직급, 직책 변경 메소드 추가
 *  </pre>
 *
 * @author 이승건
 * @version 1.1
 */
@Entity
@Table(name = "tbl_employee")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee {

    /**
     * 직원 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer employeeId;

    /**
     * 소속 부서
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private EmployeeDepartment employeeDepartment;

    /**
     * 사번 (고유)
     */
    @Column(name = "employee_number", unique = true, nullable = false, length = 50)
    private String employeeNumber;

    /**
     * 직원 이름
     */
    @Column(name = "employee_name", nullable = false, length = 50)
    private String employeeName;

    /**
     * 이메일 (고유, 암호화)
     */
    @Column(name = "email", unique = true, nullable = false, columnDefinition = "varbinary(2048)")
    private byte[] email;

    /**
     * 전화번호 (고유, 암호화)
     */
    @Column(name = "phone", unique = true, nullable = false, columnDefinition = "varbinary(128)")
    private byte[] phone;

    /**
     * 생년월일
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * 성별
     */
    @Column(name = "gender", nullable = false, length = 50)
    private String gender;

    @Convert(converter = EmployeeStatusConverter.class)
    @Column(name = "status", nullable = false, length = 50)
    private EmployeeStatus status;

    /**
     * 고용 형태 (예: 정규직, 계약직)
     */
    @Column(name = "contract_type", nullable = false, length = 50)
    private String contractType;

    /**
     * 주소 (암호화)
     */
    @Column(name = "address", columnDefinition = "varbinary(4096)")
    private byte[] address;

    /**
     * 입사일
     */
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    /**
     * 퇴사일
     */
    @Column(name = "termination_date")
    private LocalDate terminationDate;

    /**
     * 개인정보 보관 만료일
     */
    @Column(name = "retention_expire_at")
    private LocalDate retentionExpireAt;

    /**
     * 프로필 이미지 경로
     */
    @Column(name = "image_path")
    private String imagePath;

    /**
     * 생성 일시
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 직인 이미지 URL
     */
    @Column(name = "seal_image_url")
    private String sealImageUrl;

    /**
     * 직급
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id")
    private Grade grade;

    /**
     * 직책
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_title_id")
    private JobTitle jobTitle;

    @Column(name = "evaluation_point")
    private BigDecimal evaluationPoint;

    @Column(name = "base_salary")
    private Integer baseSalary;

    @Transient // DB 컬럼과 매핑되지 않음
    private String departmentPath;

    public void changeDepartment(EmployeeDepartment newDepartment) {
        this.employeeDepartment = newDepartment;
    }

    public void changeGrade(Grade newGrade) {
        this.grade = newGrade;
        this.evaluationPoint = BigDecimal.valueOf(0);
    }
    public void changeJobTitle(JobTitle newJobTitle) {
        this.jobTitle = newJobTitle;
    }

    public void changeStatus(EmployeeStatus newStatus) {
        this.status = newStatus;
    }

    public void updateTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    public void updateRetentionExpireAt(LocalDate retentionExpireAt) {
        this.retentionExpireAt = retentionExpireAt;
    }

    /**
     * 개인정보를 익명화(마스킹) 처리합니다.
     * 통계에 필요한 정보(사번, 입사일, 퇴사일, 부서, 직급 등)는 유지하고,
     * 개인 식별 정보(이름, 이메일, 전화번호 등)는 삭제하거나 마스킹합니다.
     *
     * @param encryptionUtil 암호화 유틸리티
     */
    public void anonymize(EncryptionUtil encryptionUtil) {
        this.employeeName = "퇴사자";
        
        // UNIQUE 제약조건을 피하기 위해 employeeId를 포함한 더미 데이터 생성 후 암호화
        String dummyEmail = "deleted_" + this.employeeId + "@example.com";
        this.email = encryptionUtil.encrypt(dummyEmail);
        
        String dummyPhone = "000-0000-" + String.format("%04d", this.employeeId % 10000);
        this.phone = encryptionUtil.encrypt(dummyPhone);
        
        this.birthDate = null;
        this.gender = "U"; // Unknown
        this.address = null;
        this.imagePath = null;
        this.sealImageUrl = null;
    }

    @Builder
    public Employee(EmployeeDepartment employeeDepartment, String employeeNumber, String employeeName,
                    byte[] email, byte[] phone, LocalDate birthDate,
                    String gender, EmployeeStatus status, String contractType,
                    byte[] address, LocalDate hireDate, String imagePath,
                    Grade grade, JobTitle jobTitle, BigDecimal evaluationPoint,
                    Integer baseSalary) {
        this.employeeDepartment = employeeDepartment;
        this.employeeNumber = employeeNumber;
        this.employeeName = employeeName;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.gender = gender;
        this.status = status;
        this.contractType = contractType;
        this.address = address;
        this.hireDate = hireDate;
        this.imagePath = imagePath;
        this.grade = grade;
        this.jobTitle = jobTitle;
        this.evaluationPoint = evaluationPoint;
        this.baseSalary = baseSalary;
    }
}
