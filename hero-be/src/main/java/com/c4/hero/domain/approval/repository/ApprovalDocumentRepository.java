package com.c4.hero.domain.approval.repository;

import com.c4.hero.domain.approval.entity.ApprovalDocument;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ApprovalDocumentRepository extends JpaRepository<ApprovalDocument, Integer> {

    // 마지막 문서 번호를 조회하는 쿼리
    // "HERO-2025-%" 로 시작하는 것 중 제일 큰 값을 가져옴
    @Query("SELECT d.docNo FROM ApprovalDocument d WHERE d.docNo LIKE :prefix ORDER BY d.docNo DESC LIMIT 1")
    String findLastDocNoLike(@Param("prefix") String prefix);
}
