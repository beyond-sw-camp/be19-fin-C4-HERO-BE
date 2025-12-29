package com.c4.hero.domain.payroll.policy.repository;

import com.c4.hero.domain.payroll.policy.entity.PolicyConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * <pre>
 * Class Name : PolicyConfigRepository
 * Description : 급여 정책 공통 설정(PolicyConfig) 관리 Repository
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public interface PolicyConfigRepository extends JpaRepository<PolicyConfig, Integer> {

    /**
     * 정책 ID 기준 공통 설정 목록 조회
     *
     * @param policyId 급여 정책 ID
     * @return 해당 정책에 속한 공통 설정 목록
     */
    List<PolicyConfig> findAllByPolicyId(Integer policyId);

    /**
     * 정책 ID와 설정 키 기준 단건 설정 조회
     *  - 설정 생성/수정(upsert) 시 중복 여부 확인 용도
     *
     * @param policyId 급여 정책 ID
     * @param configKey 설정 키
     * @return 해당 정책의 설정 (없을 수 있음)
     */
    Optional<PolicyConfig> findByPolicyIdAndConfigKey(Integer policyId, String configKey);

    /**
     * 정책 ID 기준 공통 설정 전체 삭제
     *
     * 용도
     *  - 급여 정책 삭제 시 하위 설정 데이터 정리
     *  - 정책 복사 시 기존 설정 초기화
     *
     * @param policyId 급여 정책 ID
     */
    void deleteAllByPolicyId(Integer policyId);

}
