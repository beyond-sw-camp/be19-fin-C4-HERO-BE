package com.c4.hero.domain.employee.entity;

import com.c4.hero.domain.employee.type.AccountStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Class Name: Account
 * Description: 사용자의 계정 정보를 담는 엔티티 클래스
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Entity
@Table(name = "tbl_account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    /**
     * 계정 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Integer accountId;

    /**
     * 계정과 연결된 직원 정보
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    /**
     * 계정 아이디 (로그인 시 사용)
     */
    @Column(name = "account", nullable = false, length = 20)
    private String account;

    /**
     * 암호화된 비밀번호
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * 계정 상태 (예: 활성, 비활성, 잠김)
     */
    @Column(name = "account_status", nullable = false, length = 20)
    private AccountStatus accountStatus;

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
     * 마지막 로그인 일시
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * 비밀번호 변경 일시
     */
    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    /**
     * 비밀번호 변경 필요 여부
     */
    @Column(name = "password_change_required", nullable = false)
    private boolean passwordChangeRequired;

    /**
     * 계정이 가진 권한 목록
     */
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AccountRole> accountRoles = new ArrayList<>();

    @Builder
    public Account(Employee employee, String account, String passwordHash, AccountStatus accountStatus, boolean passwordChangeRequired) {
        this.employee = employee;
        this.account = account;
        this.passwordHash = passwordHash;
        this.accountStatus = accountStatus;
        this.passwordChangeRequired = passwordChangeRequired;
    }
}
