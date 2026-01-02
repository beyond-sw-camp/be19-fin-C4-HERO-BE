package com.c4.hero.domain.evaluation.service;

import com.c4.hero.domain.evaluation.dto.ai.analysis.AiFormItemFastApiDTO;
import com.c4.hero.domain.evaluation.dto.ai.analysis.MemberAnalysisFastApiRequestDTO;
import com.c4.hero.domain.evaluation.dto.ai.analysis.MemberAnalysisRequestDTO;
import com.c4.hero.domain.evaluation.dto.ai.analysis.MemberAnalysisResponseDTO;
import com.c4.hero.domain.evaluation.dto.ai.promotion.PromotionCandidateResponseDTO;
import com.c4.hero.domain.evaluation.dto.ai.violation.GuideViolationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Class Name: AiService
 * Description: Ai 관련 서비스 로직 처리
 *
 * History
 * 2025/12/30 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@Service
@RequiredArgsConstructor
public class AiService {

    /** 설정 클래스 의존성 주입 */
    private final WebClient aiWebClient;

    /**
     * 파이썬 서버에 사원 분석을 요청하는 로직
     *
     * @param request MemberAnalysisRequestDTO
     *        요청한 사원 분석 데이터
     * @return MemberAnalysisResponseDTO
     *        응답하는 사원 분석 결과 데이터
     */
    public MemberAnalysisResponseDTO analyzeMember(MemberAnalysisRequestDTO request) {

        if (request.getFormItems() == null || request.getFormItems().isEmpty()) {
            throw new IllegalArgumentException("AI 분석 요청에 formItems가 없습니다.");
        }

        MemberAnalysisFastApiRequestDTO fastApiRequest =
                new MemberAnalysisFastApiRequestDTO();

        fastApiRequest.setTemplateName(request.getTemplateName());
        fastApiRequest.setEmployeeName(request.getEmployeeName());
        fastApiRequest.setEmployeeDepartment(request.getEmployeeDepartment());
        fastApiRequest.setEmployeeGrade(request.getEmployeeGrade());
        fastApiRequest.setTotalScore(request.getTotalScore());
        fastApiRequest.setTotalRank(request.getTotalRank());

        fastApiRequest.setFormItems(
                request.getFormItems().stream().map(item -> {
                    AiFormItemFastApiDTO dto = new AiFormItemFastApiDTO();
                    dto.setItemName(item.getItemName());
                    dto.setScore(item.getScore());
                    dto.setWeight(item.getWeight());
                    dto.setComment(item.getComment());
                    return dto;
                }).toList()
        );

        return aiWebClient.post()
                .uri("/api/analyze/member")
                .bodyValue(fastApiRequest)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("AI 서버 오류: " + body))
                )
                .bodyToMono(MemberAnalysisResponseDTO.class)
                .block();
    }


    /**
     * 파이썬 서버에 평가 가이드 위반 분석을 요청하는 로직
     *
     * @param guide String
     *        가이드 내용
     * @param template Map<String, Object>
     *        평가 템플릿 데이터
     * @return List<GuideViolationResponseDTO>
     */
    public List<GuideViolationResponseDTO> analyzeViolation(String guide, Map<String, Object> template) {
        return aiWebClient.post()
                .uri("/api/analyze/violation")
                .bodyValue(
                        Map.of(
                                "guide", guide,
                                "template", template
                        )
                )
                .retrieve()
                .bodyToFlux(GuideViolationResponseDTO.class)
                .collectList()
                .block();
    }


    /**
     * 파이썬 서버에 승진 추천 대상자 분석을 요청하는 로직
     *
     * @param dashboardData List<Object>
     *       대시보드에 사용될 평가 데이터
     * @return List<PromotionCandidateResponseDTO>
     */
    public List<PromotionCandidateResponseDTO> analyzePromotion(
            List<Object> dashboardData
    ) {
        return aiWebClient.post()
                .uri("/api/analyze/promotion")
                .bodyValue(dashboardData)
                .retrieve()
                .bodyToFlux(PromotionCandidateResponseDTO.class)
                .collectList()
                .block();
    }

}
