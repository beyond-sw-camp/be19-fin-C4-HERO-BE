package com.c4.hero.domain.settings.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 * Class Name: SettingsLoginPolicy
 * Description: 로그인 정책 엔티티
 *
 * History
 * 2025/12/16 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_login_policies")
public class SettingsLoginPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_policy_id")
    private Integer loginPolicyId;

    @Column(name = "value")
    private Integer value;
}
