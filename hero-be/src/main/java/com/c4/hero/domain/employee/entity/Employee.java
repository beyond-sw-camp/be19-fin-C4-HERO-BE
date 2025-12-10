package com.c4.hero.domain.employee.entity;

import com.c4.hero.domain.employee.type.EmployeeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name: Employee
 * Description: 직원 정보를 담는 엔티티 클래스
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
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
    private Department department;

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

    /**
     * 직원 상태 (예: 재직, 휴직, 퇴사)
     */
    @Enumerated(EnumType.STRING)
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
    @Column(name = "image_path", nullable = false)
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

    @Builder
    public Employee(Department department, String employeeNumber, String employeeName, byte[] email, byte[] phone, LocalDate birthDate, String gender, EmployeeStatus status, String contractType, byte[] address, LocalDate hireDate, String imagePath, Grade grade, JobTitle jobTitle) {
        this.department = department;
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
    }
}
