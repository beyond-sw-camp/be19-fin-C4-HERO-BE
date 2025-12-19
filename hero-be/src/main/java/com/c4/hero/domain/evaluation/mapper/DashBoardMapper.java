package com.c4.hero.domain.evaluation.mapper;

import com.c4.hero.domain.evaluation.dto.dashboard.DashBoardResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <pre>
 * Class Name: DashBoardMapper
 * Description: Mybatis 사용을 위한 대시보드 매퍼
 *
 * History
 * 2025/12/17 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Mapper
public interface DashBoardMapper {
    List<DashBoardResponseDTO> selectAllDashBoard();

    List<DashBoardResponseDTO> selectDashBoard(@Param("departmentId") Integer id);
}
