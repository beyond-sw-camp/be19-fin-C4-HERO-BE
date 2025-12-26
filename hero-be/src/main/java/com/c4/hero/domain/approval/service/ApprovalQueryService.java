package com.c4.hero.domain.approval.service;

import com.c4.hero.domain.approval.dto.ApprovalDefaultLineDTO;
import com.c4.hero.domain.approval.dto.ApprovalDefaultRefDTO;
import com.c4.hero.domain.approval.dto.ApprovalTemplateResponseDTO;
import com.c4.hero.domain.approval.dto.response.ApprovalTemplateDetailResponseDTO;
import com.c4.hero.domain.approval.dto.response.ApprovalDocumentsResponseDTO;
import com.c4.hero.domain.approval.entity.*;
import com.c4.hero.domain.approval.mapper.ApprovalMapper;
import com.c4.hero.domain.approval.repository.ApprovalBookmarkRepository;
import com.c4.hero.domain.approval.repository.ApprovalTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <pre>
 * Class Name  : ApprovalQueryService
 * Description :
 * - 전자결재 조회 관련 서비스 로직
 * - 전자결재 단순 조회 -> JPA
 * - 전자결재 복잡한 조회(쿼리) -> MyBatis
 *
 * History
 * 2025/12/15 (민철) 최초 작성
 * 2025/12/19 (민철) ApprovalTemplate.java 문서템플릿 필드명 수정에 의한 getter메서드 수정
 * 2025/12/25 (민철) CQRS 패턴 적용 및 작성화면 조회 메서드 로직 추가
 *
 * </pre>
 *
 * @author 민철
 * @version 2.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalQueryService {

    private final ApprovalTemplateRepository templateRepository;
    private final ApprovalBookmarkRepository bookmarkRepository;
    private final ApprovalMapper approvalMapper;


    /**
     * 모든 결재 양식 조회 (즐겨찾기 여부 포함)
     *
     * @param employeeId 현재 로그인한 사원 ID
     * @return 문서 템플릿 목록
     */
    @Transactional(readOnly = true)
    public List<ApprovalTemplateResponseDTO> getAllTemplates(Integer employeeId) {

        List<ApprovalTemplate> templates =
                templateRepository.findAll();

        List<Integer> bookmarkedIds =
                bookmarkRepository.findTemplateIdsByEmpId(employeeId);

        return templates.stream()
                .map(t -> ApprovalTemplateResponseDTO.builder()
                        .templateId(t.getTemplateId())
                        .templateName(t.getTemplateName())
                        .templateKey(t.getTemplateKey())
                        .category(t.getCategory())
                        .description(t.getDescription())
                        .bookmarking(bookmarkedIds.contains(t.getTemplateId()))
                        .build())
                .collect(Collectors.toList());
    }





    /**
     * 문서조회 메소드
     *
     * @param  
     * @return ResponseEntity<> 
     */
    public List<ApprovalDocumentsResponseDTO> getDocuments(int page, int size, String fromDate, String toDate, String sortBy, String condition) {

        return null;
    }

    /**
     * 서식 화면 조회 메서드
     * * @param employeeId 기안자 ID (로그인 사용자)
     * @param templateId 서식 ID
     * @return 서식 정보 + 계산된 결재선/참조자 목록
     */
    @Transactional(readOnly = true)
    public ApprovalTemplateDetailResponseDTO getTemplate(Integer employeeId, Integer templateId) {

        // 1. 서식 기본 정보 조회 (JPA)
        // -> 단순히 서식명, 카테고리 등을 가져옴
        ApprovalTemplate templateEntity = templateRepository.findByTemplateId(templateId);

        if (templateEntity == null) {
            throw new IllegalArgumentException("해당 서식을 찾을 수 없습니다. id=" + templateId);
        }
//        log.info("template detail response: {}", templateEntity);

        // 2. 동적 결재선 조회 (MyBatis)
        // -> 0일 때 직속 부서장 변환 로직은 쿼리 내부에서 처리
        List<ApprovalDefaultLineDTO> lineDTOs = approvalMapper.selectDefaultLines(employeeId, templateId);
//        log.info("template detail response: {}", lineDTOs);

        // 3. 동적 참조자 조회 (MyBatis)
        // -> 0일 때 직속 부서장 변환 로직은 쿼리 내부에서 처리
        List<ApprovalDefaultRefDTO> refDTOs = approvalMapper.selectDefaultReferences(employeeId, templateId);
//        log.info("template detail response: {}", refDTOs);

        // 4. 결과 조립 및 반환
        return ApprovalTemplateDetailResponseDTO.builder()
                .templateId(templateEntity.getTemplateId())
                .templateName(templateEntity.getTemplateName())
                .templateKey(templateEntity.getTemplateKey())
                .category(templateEntity.getCategory())
                .lines(lineDTOs)
                .references(refDTOs)
                .build();
    }
}
