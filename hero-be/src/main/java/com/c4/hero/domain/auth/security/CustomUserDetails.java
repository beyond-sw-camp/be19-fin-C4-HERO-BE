package com.c4.hero.domain.auth.security;

import com.c4.hero.domain.employee.entity.Account;
import com.c4.hero.domain.employee.entity.Employee;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <pre>
 * Class Name: CustomUserDetails
 * Description: Spring Security의 UserDetails를 구현한 커스텀 클래스
 *
 * - 인증/인가에 필요한 사용자 정보를 담는 역할
 *
 * History
 * 2025/12/09 (이승건) 최초 작성
 * 2025/12/22 (혜원) 토큰 Claims 복구용 생성자 추가 및 getUsername NPE 방어 로직 적용
 * 2025/12/29 (승건) 비밀번호 변경 필요 여부 필드 추가
 * </pre>
 *
 * @author 이승건
 * @version 1.1
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Account account;
    private final Integer employeeId;
    private final String employeeNumber;
    private final String employeeName;
    private final Integer departmentId;
    private final String departmentName;
    private final Integer gradeId;
    private final String gradeName;
    private final Integer jobTitleId;
    private final String jobTitleName;
    private final boolean passwordChangeRequired; // 필드 추가
    private final String imagePath; // 프로필 이미지 경로 추가

    public CustomUserDetails(Account account) {
        this.account = account;
        Employee employee = account.getEmployee();
        this.employeeId = employee.getEmployeeId();
        this.employeeNumber = employee.getEmployeeNumber();
        this.employeeName = employee.getEmployeeName();
        this.departmentId = employee.getEmployeeDepartment() != null ? employee.getEmployeeDepartment().getDepartmentId() : null;
        this.departmentName = employee.getEmployeeDepartment() != null ? employee.getEmployeeDepartment().getDepartmentName() : null;
        this.gradeId = employee.getGrade() != null ? employee.getGrade().getGradeId() : null;
        this.gradeName = employee.getGrade() != null ? employee.getGrade().getGrade() : null;
        this.jobTitleId = employee.getJobTitle() != null ? employee.getJobTitle().getJobTitleId() : null;
        this.jobTitleName = employee.getJobTitle() != null ? employee.getJobTitle().getJobTitle() : null;
        this.passwordChangeRequired = account.isPasswordChangeRequired();
        this.imagePath = employee.getImagePath();
    }

    // JWT 토큰 복구용
    public CustomUserDetails(Integer employeeId, String employeeNumber, String employeeName,
                             Integer departmentId, String departmentName, Integer gradeId,
                             String gradeName, Integer jobTitleId, String jobTitleName,
                             boolean passwordChangeRequired, String imagePath) { // 파라미터 추가
        this.employeeId = employeeId;
        this.employeeNumber = employeeNumber;
        this.employeeName = employeeName;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.gradeId = gradeId;
        this.gradeName = gradeName;
        this.jobTitleId = jobTitleId;
        this.jobTitleName = jobTitleName;
        this.passwordChangeRequired = passwordChangeRequired;
        this.imagePath = imagePath;
        this.account = null; // API 호출 시에는 DB 객체가 필요 없으므로 null 처리
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return account.getAccountRoles().stream()
                .map(accountRole -> new SimpleGrantedAuthority(accountRole.getRole().getRole().name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return account.getPasswordHash();
    }

    @Override
    public String getUsername() {
        // UserDetails의 getUsername()은 사용자를 식별하는 ID를 의미
        if (this.account != null) {
            return account.getAccount();
        }
        // 토큰 기반 인증일 때는 employeeNumber를 식별자로 사용
        return this.employeeNumber;
    }

    // -- 계정 상태 관련 메소드 --

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 (일단 true로 설정)
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠김 여부 (AccountStatus에 따라 동적으로 반환 가능)
        return account.getAccountStatus() != com.c4.hero.domain.employee.type.AccountStatus.DISABLED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 여부 (일단 true로 설정)
    }

    @Override
    public boolean isEnabled() {
        // 계정 활성화 여부
        return account.getAccountStatus() == com.c4.hero.domain.employee.type.AccountStatus.ACTIVE;
    }
}
