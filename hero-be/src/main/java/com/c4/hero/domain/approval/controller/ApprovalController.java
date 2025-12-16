/**
 * <pre>
 * Class Name  : ApprovalController
 * Description : 전자결재 문서 템플릿 관련 API를 처리하는 컨트롤러
 *
 * History
 * 2025/12/15 (변민철) 최초 작성
 * </pre>
 *
 * @author 변민철
 * @version 1.0
 */
package com.c4.hero.domain.approval.controller;

import com.c4.hero.domain.approval.dto.DocumentTemplateDTO;
import com.c4.hero.domain.approval.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
public class ApprovalController {

    /** 전자결재 서비스 */
    private final ApprovalService approvalService;

    /**
     * 문서 템플릿 전체 조회
     *
     * @return 문서 템플릿 목록
     */
    @GetMapping("/templates")
    public ResponseEntity<List<DocumentTemplateDTO>> getTemplates() {
        Integer currentEmpId = 1; // TODO: 인증 연동 후 실제 사번 ID로 교체

        List<DocumentTemplateDTO> templates =
                approvalService.getAllTemplates(currentEmpId);

        return ResponseEntity.ok(templates);
    }

    /**
     * 문서 템플릿 즐겨찾기 토글
     *
     * @param templateId 문서 템플릿 ID
     * @return 즐겨찾기 여부
     */
    @PostMapping("/templates/{templateId}/bookmark")
    public ResponseEntity<Boolean> toggleBookmark(
            @PathVariable Integer templateId
    ) {
        Integer currentEmpId = 1; // TODO: 인증 연동 후 실제 사번 ID로 교체

        boolean isBookmarked =
                approvalService.toggleBookmark(currentEmpId, templateId);

        return ResponseEntity.ok(isBookmarked);
    }
}
