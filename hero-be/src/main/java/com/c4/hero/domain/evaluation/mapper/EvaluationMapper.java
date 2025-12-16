package com.c4.hero.domain.evaluation.mapper;

import com.c4.hero.domain.evaluation.dto.evaluation.EvaluationResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationMapper
 * Description: Mybatis 사용을 위한 평가 매퍼
 *
 * History
 * 2025/12/12 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Mapper
public interface EvaluationMapper {

    List<Integer> selectEvaluationIdsWithPaging(
            @Param("offset") int offset,
            @Param("size") int size
    );

    List<EvaluationResponseDTO> selectEvaluationsByIds(
            @Param("evaluationIds") List<Integer> evaluationIds
    );

    Long countAllEvaluations();

    List<EvaluationResponseDTO> selectAllEvaluation();

    EvaluationResponseDTO selectEvaluation(@Param("evaluationId") Integer id);
}
