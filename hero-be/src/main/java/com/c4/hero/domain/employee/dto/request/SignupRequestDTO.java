package com.c4.hero.domain.employee.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

/**
 * <pre>
 * Class Name: SignupRequestDTO
 * Description: 직원 회원가입 요청 시 사용되는 데이터 전송 객체
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Getter
@Setter
public class SignupRequestDTO {

    /**
     * 직원 이름
     */
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String employeeName;

    /**
     * 사번
     */
    @NotBlank(message = "사번은 필수 입력 항목입니다.")
    private String employeeNumber;

    /**
     * 이메일 주소
     */
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    /**
     * 전화번호
     */
    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    private String phone;

    /**
     * 고용 형태 (예: 정규직, 계약직)
     */
    @NotNull(message = "고용 형태는 필수 입력 항목입니다.")
    private String contractType; // 정규직/비정규직/인턴/일일근로자

    /**
     * 성별 (M: 남성, F: 여성)
     */
    @NotBlank(message = "성별은 필수 입력 항목입니다.")
    @Pattern(regexp = "^[MF]$", message = "성별은 'M' 또는 'F' 여야 합니다.")
    private String gender;

    /**
     * 입사일
     */
    @NotNull(message = "입사일은 필수 입력 항목입니다.")
    private LocalDate hireDate;

    /**
     * 프로필 이미지 파일
     */
    @NotNull(message = "프로필 이미지는 필수입니다.")
    private MultipartFile imageFile;

    /**
     * 기본급
     */
    @NotNull(message = "기본급은 필수 입력 항목입니다.")
    private Integer baseSalary;

    /**
     * 생년월일 (선택)
     */
    private LocalDate birthDate;

    /**
     * 주소 (선택)
     */
    private String address;

    /**
     * 부서 이름 (선택)
     */
    private String departmentName;

    /**
     * 직급 이름 (선택)
     */
    private String gradeName;

    /**
     * 직책 이름 (선택)
     */
    private String jobTitleName;
}
