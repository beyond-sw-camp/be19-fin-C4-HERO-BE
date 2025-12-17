package com.c4.hero.domain.settings.repository;

import com.c4.hero.domain.settings.entity.SettingsLoginPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <pre>
 * Class Name: SettingsLoginPolicyRepository
 * Description: 로그인 정책 관련 JPA Repository
 *
 * History
 * 2025/12/16 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
public interface SettingsLoginPolicyRepository extends JpaRepository<SettingsLoginPolicy, Integer> {
}
