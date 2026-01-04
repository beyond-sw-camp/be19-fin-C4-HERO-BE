package com.c4.hero.domain.evaluation.mapper;

import com.c4.hero.domain.evaluation.dto.form.EvaluationFormResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationFormMapper
 * Description: Mybatis 사용을 위한 평가서 매퍼
 *
 * History
 * 2025/12/14 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Mapper
public interface EvaluationFormMapper {
    List<EvaluationFormResponseDTO> selectAllForm();

    EvaluationFormResponseDTO selectForm(@Param("evaluationId") Integer evaluationId, @Param("employeeId") Integer employeeId);
}
