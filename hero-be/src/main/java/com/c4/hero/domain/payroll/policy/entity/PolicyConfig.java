package com.c4.hero.domain.payroll.policy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 * Class Name : PolicyConfig
 * Description : 급여 정책(Payroll Policy) 공통 설정(Policy Config) 엔티티
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Entity
@Table(
        name = "tbl_payroll_policy_config",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_policy_config_key", columnNames = {"policy_id", "config_key"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PolicyConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="config_id")
    private Integer configId;

    /** 소속 급여 정책 ID */
    @Column(name="policy_id", nullable=false)
    private Integer policyId;

    /** 설정 키 (정책 내 유니크) */
    @Column(name="config_key", nullable=false, length=60)
    private String configKey;

    /** 값 유형 (예: STRING / NUMBER / BOOLEAN / JSON) */
    @Column(name="value_type", nullable=false, length=20)
    private String valueType; // STRING/NUMBER/BOOLEAN/JSON

    /** 설정 값 (텍스트 기반, JSON 포함 가능) */
    @Lob
    @Column(name="config_value", nullable=false)
    private String configValue;

    /** 설정 설명 (선택) */
    @Column(name="description", length=255)
    private String description;

    @Column(name="active_yn", nullable=false, length=1)
    private String activeYn;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", nullable=false)
    private LocalDateTime updatedAt;

    /**
     * 엔티티 최초 저장 시 기본값 및 생성/수정 시각 자동 세팅
     *  - activeYn: null이면 기본값 Y
     */
    @PrePersist
    void prePersist() {
        if (activeYn == null) activeYn = "Y";
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * 엔티티 수정 시 수정 시각 갱신
     */
    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 설정 값 갱신
     *  - valueType / configValue / description / activeYn을 일괄 반영
     *  - activeYn이 null이면 기본값 Y로 보정
     */
    public void apply(String valueType, String value, String description, String activeYn) {
        this.valueType = valueType;
        this.configValue = value;
        this.description = description;
        this.activeYn = (activeYn == null) ? "Y" : activeYn;
    }
}