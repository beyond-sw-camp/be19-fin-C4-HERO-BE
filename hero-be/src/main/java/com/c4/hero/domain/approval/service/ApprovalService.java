/**
 * <pre>
 * Class Name  : ApprovalService
 * Description : 전자결재 문서 템플릿 및 즐겨찾기 관련 서비스
 *
 * History
 * 2025/12/15 (변민철) 최초 작성
 * </pre>
 *
 * @author 변민철
 * @version 1.0
 */
package com.c4.hero.domain.approval.service;

import com.c4.hero.domain.approval.dto.DocumentTemplateDTO;
import com.c4.hero.domain.approval.entity.ApprovalBookmark;
import com.c4.hero.domain.approval.entity.ApprovalFormTemplate;
import com.c4.hero.domain.approval.repository.ApprovalBookmarkRepository;
import com.c4.hero.domain.approval.repository.ApprovalTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalTemplateRepository templateRepository;
    private final ApprovalBookmarkRepository bookmarkRepository;

    /**
     * 모든 결재 양식 조회 (즐겨찾기 여부 포함)
     *
     * @param currentEmpId 현재 로그인한 사원 ID
     * @return 문서 템플릿 목록
     */
    @Transactional(readOnly = true)
    public List<DocumentTemplateDTO> getAllTemplates(Integer currentEmpId) {

        // 1. 모든 템플릿 조회
        List<ApprovalFormTemplate> templates =
                templateRepository.findAll();

        // 2. 현재 사용자가 즐겨찾기한 템플릿 ID 목록 조회 (한 번의 쿼리로 가져옴)
        List<Integer> bookmarkedIds =
                bookmarkRepository.findTemplateIdsByEmpId(currentEmpId);

        // 3. DTO 변환 시 bookmarking true/false 설정
        return templates.stream()
                .map(t -> DocumentTemplateDTO.builder()
                        .id(t.getTemplateId())
                        .name(t.getName())
                        .templateKey(t.getTemplateKey())
                        .category(t.getCategory())
                        .description(t.getDescription())
                        // 포함되어 있으면 true
                        .bookmarking(bookmarkedIds.contains(t.getTemplateId()))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 즐겨찾기 토글 (있으면 삭제, 없으면 추가)
     *
     * @param empId      사원 ID
     * @param templateId 문서 템플릿 ID
     * @return 즐겨찾기 상태
     */
    @Transactional
    public boolean toggleBookmark(Integer empId, Integer templateId) {

        Optional<ApprovalBookmark> bookmark =
                bookmarkRepository.findByEmpIdAndTemplateId(empId, templateId);

        if (bookmark.isPresent()) {
            bookmarkRepository.delete(bookmark.get());
            return false; // 삭제됨 (즐겨찾기 해제)
        } else {
            ApprovalBookmark newBookmark = ApprovalBookmark.builder()
                    .empId(empId)
                    .templateId(templateId)
                    .build();
            bookmarkRepository.save(newBookmark);
            return true; // 생성됨 (즐겨찾기 등록)
        }
    }
}
