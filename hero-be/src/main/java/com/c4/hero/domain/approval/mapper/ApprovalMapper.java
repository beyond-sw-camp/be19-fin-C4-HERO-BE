package com.c4.hero.domain.approval.mapper;

import com.c4.hero.domain.approval.dto.ApprovalDefaultLineDTO;
import com.c4.hero.domain.approval.dto.ApprovalDefaultRefDTO;
import com.c4.hero.domain.approval.dto.response.*;
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
 * 2025/12/26 (민철) 문서함 조회 메서드 추가
 * 2025/12/29 (민철) countInboxDocuments에 sortBy 파라미터 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 2.1
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

    /**
     * 문서함 목록 조회 (탭별 필터링)
     *
     * @param employeeId 조회할 직원 ID
     * @param tab        탭 구분 (all/que/request/reject/ref/end/draft)
     * @param offset     페이지 오프셋
     * @param size       페이지 크기
     * @param fromDate   시작일
     * @param toDate     종료일
     * @param sortBy     정렬 기준
     * @param condition  필터 조건
     * @return 문서 목록
     */
    List<ApprovalDocumentsResponseDTO> selectInboxDocuments(
            @Param("employeeId") Integer employeeId,
            @Param("tab") String tab,
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            @Param("sortBy") String sortBy,
            @Param("condition") String condition
    );

    /**
     * 문서함 전체 개수 조회 (탭별 필터링)
     *
     * @param employeeId 조회할 직원 ID
     * @param tab        탭 구분 (all/que/request/reject/ref/end/draft)
     * @param fromDate   시작일
     * @param toDate     종료일
     * @param sortBy     검색 필드
     * @param condition  필터 조건
     * @return 전체 문서 개수
     */
    int countInboxDocuments(
            @Param("employeeId") Integer employeeId,
            @Param("tab") String tab,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            @Param("sortBy") String sortBy,
            @Param("condition") String condition
    );

    /**
     * 문서 상세 정보 조회
     *
     * @param docId 문서 ID
     * @return 문서 상세 정보
     */
    ApprovalDocumentDetailResponseDTO selectDocumentDetail(@Param("docId") Integer docId);

    /**
     * 문서의 결재선 목록 조회
     *
     * @param docId 문서 ID
     * @return 결재선 목록
     */
    List<ApprovalLineResponseDTO> selectApprovalLines(@Param("docId") Integer docId);

    /**
     * 문서의 참조자 목록 조회
     *
     * @param docId 문서 ID
     * @return 참조자 목록
     */
    List<ApprovalReferenceResponseDTO> selectApprovalReferences(@Param("docId") Integer docId);

    /**
     * 문서의 첨부파일 목록 조회
     *
     * @param docId 문서 ID
     * @return 첨부파일 목록
     */
    List<ApprovalAttachmentResponseDTO> selectApprovalAttachments(@Param("docId") Integer docId);
}