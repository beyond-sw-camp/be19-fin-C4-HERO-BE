package com.c4.hero.domain.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.util.Objects;

/**
 * <pre>
 * Class Name: AccountRole
 * Description: 계정과 역할(Role)의 다대다 관계를 매핑하는 엔티티
 *
 * History
 * 2025/12/09 이승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Entity
@Table(name = "tbl_account_role")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountRole {

    /**
     * 계정-역할 매핑 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_role_id")
    private Integer accountRoleId;

    /**
     * 매핑된 계정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    /**
     * 매핑된 역할
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @Builder
    public AccountRole(Account account, Role role) {
        this.account = account;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountRole that = (AccountRole) o;
        return Objects.equals(accountRoleId, that.accountRoleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountRoleId);
    }
}
