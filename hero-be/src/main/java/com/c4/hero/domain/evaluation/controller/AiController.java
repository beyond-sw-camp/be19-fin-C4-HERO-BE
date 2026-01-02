package com.c4.hero.domain.evaluation.controller;

import com.c4.hero.domain.evaluation.dto.ai.analysis.MemberAnalysisRequestDTO;
import com.c4.hero.domain.evaluation.dto.ai.analysis.MemberAnalysisResponseDTO;
import com.c4.hero.domain.evaluation.dto.ai.promotion.PromotionCandidateResponseDTO;
import com.c4.hero.domain.evaluation.dto.ai.violation.GuideViolationRequestDTO;
import com.c4.hero.domain.evaluation.dto.ai.violation.GuideViolationResponseDTO;
import com.c4.hero.domain.evaluation.service.AiService;
import com.c4.hero.domain.evaluation.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <pre>
 * Class Name: AiController
 * Description: Ai 관련 컨트롤러 로직 처리
 *
 * History
 * 2025/12/30 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "파이썬 연동 API", description = "AI 관련 API")
public class AiController {

    /** AI 관련 서비스 의존성 주입 */
    private final AiService aiService;


    /**
     * 사원 분석
     *
     * @param request MemberAnalysisRequestDTO
     * @return result MemberAnalysisResponseDTO
     */
    @Operation(
            summary = "사원 역량 분석",
            description = "평가 데이터를 통해 사원의 역량을 분석한다."
    )
    @PostMapping("/analysis/member")
    public ResponseEntity<MemberAnalysisResponseDTO> analyzeMember(
            @RequestBody MemberAnalysisRequestDTO request
    ) {
        MemberAnalysisResponseDTO result = aiService.analyzeMember(request);

        return ResponseEntity.ok(result);
    }


    /**
     * 평가 가이드 위반 분석
     *
     * @param request MemberAnalysisRequestDTO
     * @return result MemberAnalysisResponseDTO
     */
    @Operation(
            summary = "평가 가이드 위반 분석",
            description = "평가 데이터를 통해 평가 가이드 위반을 분석한다."
    )
    @PostMapping("/violation")
    public ResponseEntity<List<GuideViolationResponseDTO>> analyzeViolation(
            @RequestBody GuideViolationRequestDTO request
    ) {
        List<GuideViolationResponseDTO> result =
                aiService.analyzeViolation(
                        request.getGuide(),
                        request.getTemplate()
                );

        return ResponseEntity.ok(result);
    }

    /**
     * 승진 대상자 추천
     *
     * @param dashboardData List<Object>
     * @return result List<PromotionCandidateResponseDTO>
     */
    @Operation(
            summary = "승진 추천 대상자 분석",
            description = "평가 데이터를 통해 승진 추천 대상자를 분석한다."
    )
    @PostMapping("/promotion")
    public ResponseEntity<List<PromotionCandidateResponseDTO>> analyzePromotion(
            @RequestBody List<Object> dashboardData
    ) {
        List<PromotionCandidateResponseDTO> result =
                aiService.analyzePromotion(dashboardData);

        return ResponseEntity.ok(result);
    }
}
