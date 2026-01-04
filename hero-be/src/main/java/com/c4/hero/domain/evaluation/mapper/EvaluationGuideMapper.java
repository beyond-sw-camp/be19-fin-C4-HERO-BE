package com.c4.hero.domain.evaluation.mapper;

import com.c4.hero.domain.evaluation.dto.guide.EvaluationGuideResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationGuideMapper
 * Description: Mybatis 사용을 위한 평가 가이드 매퍼
 *
 * History
 * 2025/12/10 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Mapper
public interface EvaluationGuideMapper {

    List<Integer> selectGuideIdsWithPaging(
            @Param("offset") int offset,
            @Param("size") int size
    );

    List<EvaluationGuideResponseDTO> selectGuidesByIds(
            @Param("ids") List<Integer> ids
    );

    Long countAllGuides();

    EvaluationGuideResponseDTO selectGuide(@Param("guideId") Integer id);

    List<EvaluationGuideResponseDTO> selectAllGuide();
}
