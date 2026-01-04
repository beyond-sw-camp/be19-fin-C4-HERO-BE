package com.c4.hero.domain.promotion.mapper;

import com.c4.hero.domain.promotion.dto.response.PromotionDetailForReviewResponseDTO;
import com.c4.hero.domain.promotion.dto.response.PromotionPlanDetailResponseDTO;
import com.c4.hero.domain.promotion.dto.response.PromotionPlanForReviewResponseDTO;
import com.c4.hero.domain.promotion.dto.response.PromotionPlanResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <pre>
 * Interface Name: PromotionMapper
 * Description: 승진 관련 데이터베이스 접근을 위한 MyBatis 매퍼 인터페이스
 *
 * History
 * 2025/12/19 (승건) 최초 작성
 * 2025/12/23 (승건) 심사용 상세 조회 메서드 추가
 * 2025/12/24 (승건) 심사용 목록 조회 메서드 추가
 * </pre>
 *
 * @author 승건
 * @version 1.2
 */
@Mapper
public interface PromotionMapper {

    /**
     * 조건에 맞는 승진 계획 목록을 페이징하여 조회합니다.
     *
     * @param isFinished 완료 여부 (true: 완료, false: 진행중, null: 전체)
     * @param offset     페이지 오프셋
     * @param limit      페이지당 개수
     * @return 승진 계획 목록
     */
    List<PromotionPlanResponseDTO> selectPromotionPlan(@Param("isFinished") Boolean isFinished, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 조건에 맞는 승진 계획의 전체 개수를 조회합니다.
     *
     * @param isFinished 완료 여부 (true: 완료, false: 진행중, null: 전체)
     * @return 전체 개수
     */
    long countPromotionPlan(@Param("isFinished") Boolean isFinished);

    /**
     * 특정 승진 계획의 상세 정보를 조회합니다.
     *
     * @param promotionId 조회할 승진 계획 ID
     * @return 승진 계획 상세 정보 (하위 계획 포함)
     */
    PromotionPlanDetailResponseDTO selectPromotionPlanDetail(int promotionId);

    /**
     * 특정 부서(및 하위 부서)의 직원이 후보자로 등록된 승진 계획 목록을 조회합니다.
     *
     * @param departmentIds 부서 ID 목록
     * @return 승진 계획 목록
     */
    List<PromotionPlanResponseDTO> selectRecommendPromotionPlan(@Param("departmentIds") List<Integer> departmentIds);

    /**
     * 특정 승진 계획의 상세 정보를 조회하되, 지정된 부서(및 하위 부서)에 속한 후보자만 포함합니다.
     *
     * @param promotionId   승진 계획 ID
     * @param departmentIds 조회할 부서 ID 목록 (팀장 부서 + 하위 부서)
     * @return 필터링된 승진 계획 상세 정보
     */
    PromotionPlanDetailResponseDTO selectRecommendPromotionPlanDetail(@Param("promotionId") int promotionId, @Param("departmentIds") List<Integer> departmentIds);

    /**
     * 심사용 승진 계획 상세 정보를 조회합니다. (승인 현황 포함)
     *
     * @param promotionId 승진 계획 ID
     * @return 심사용 승진 계획 상세 정보
     */
    PromotionPlanForReviewResponseDTO selectPromotionDetailForReview(@Param("promotionId") Integer promotionId);

    /**
     * 심사 가능한(추천 마감일이 지난) 승진 계획 목록을 조회합니다.
     *
     * @return 심사 대상 승진 계획 목록
     */
    List<PromotionPlanResponseDTO> selectPromotionPlanForReviewList();
}
