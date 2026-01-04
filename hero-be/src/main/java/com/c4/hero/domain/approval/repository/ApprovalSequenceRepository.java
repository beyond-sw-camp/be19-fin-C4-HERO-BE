package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApprovalSequenceRepository extends JpaRepository<ApprovalSequence, String> {

    // 비관적 락을 걸어 해당 키(seqType)를 조회
    // 트랜잭션이 끝날 때까지 다른 곳에서 이 row를 읽거나 쓸 수 없음
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ApprovalSequence s WHERE s.seqType = :seqType")
    Optional<ApprovalSequence> findBySeqTypeWithLock(@Param("seqType") String seqType);
}