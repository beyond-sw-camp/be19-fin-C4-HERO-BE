package com.c4.hero.domain.approval.mapper;

import com.c4.hero.domain.approval.dto.ApprovalDefaultLineDTO;
import com.c4.hero.domain.approval.dto.ApprovalDefaultRefDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * <pre>
 * Class Name: ApprovalMapper
 * Description: 전자결재 관련 복잡한 쿼리를 위한 Mybatis활용한 DB 접근 계층
 *
 * History
 * 2025/12/25 (민철) 작성화면 관련 결재선/참조목록 자동지정을 위한 조회 mapper
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Mapper
public interface ApprovalMapper {
    /**
     * 기본 결재선 목록 조회
     * (departmentId가 0이면 기안자의 부서장, 아니면 지정 부서장 반환)
     */
    List<ApprovalDefaultLineDTO> selectDefaultLines(
            @Param("employeeId") Integer employeeId,
            @Param("templateId") Integer templateId
    );

    /**
     * 기본 참조자 목록 조회
     * (departmentId가 0이면 기안자의 부서장, 아니면 지정 부서장 반환)
     */
    List<ApprovalDefaultRefDTO> selectDefaultReferences(
            @Param("employeeId") Integer employeeId,
            @Param("templateId") Integer templateId
    );
}
