package com.c4.hero.domain.evaluation.scheduler;

import com.c4.hero.domain.evaluation.repository.EvaluationTemplateRepository;
import com.c4.hero.domain.evaluation.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationScheduler
 * Description: 평가의 등급 자동 확정 스케줄러
 *
 * History
 * 2025/12/24 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */
@Component
@RequiredArgsConstructor
public class EvaluationScheduler {

    private final EvaluationTemplateRepository templateRepository;
    private final EvaluationService evaluationService;

    /**
     * 매일 자정 평가 자동 확정
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void autoFinalizeEvaluation() {

        /** 모든 평가 템플릿 ID 조회 */
        List<Integer> templateIds =
                templateRepository.findAllTemplateIds();

        /** 템플릿 단위로 자동 확정 시도 */
        for (Integer templateId : templateIds) {
            evaluationService.finalizeEvaluationByTemplate(templateId);
        }
    }
}
