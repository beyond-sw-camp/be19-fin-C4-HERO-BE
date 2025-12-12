package com.c4.hero.domain.evaluation.mapper;

import com.c4.hero.domain.evaluation.dto.EvaluationResponseDTO;
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
    List<EvaluationResponseDTO> selectAllEvaluation();

    EvaluationResponseDTO selectEvaluation(@Param("evaluationId") Integer id);
}
