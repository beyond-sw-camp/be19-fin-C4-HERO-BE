package com.c4.hero.domain.evaluation.service;

import com.c4.hero.domain.evaluation.dto.*;
import com.c4.hero.domain.evaluation.entity.*;
import com.c4.hero.domain.evaluation.mapper.EvaluationGuideMapper;
import com.c4.hero.domain.evaluation.mapper.EvaluationTemplateMapper;
import com.c4.hero.domain.evaluation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationService
 * Description: 평가 관련 서비스 로직 처리
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */
@Service
@RequiredArgsConstructor
public class EvaluationService {

    /** 평가 템플릿 저장소 의존성 주입 */
    private final EvaluationTemplateRepository templateRepository;

    /** 평가 템플릿 저장소 의존성 주입 */
    private final TemplateItemRepository itemRepository;

    /** 평가 기준 저장소 의존성 주입 */
    private final CriteriaRepository criteriaRepository;

    /** 평가 기간 저장소 의존성 주입 */
    private final EvaluationPeriodRepository evaluationPeriodRepository;

    /** 평가 가이드 저장소 의존성 주입 */
    private final EvaluationGuideRepository guideRepository;

    /** 평가 템플릿 mapper 의존성 주입 */
    private final EvaluationTemplateMapper evaluationTemplateMapper;

    /** 평가 가이드 mapper 의존성 주입 */
    private final EvaluationGuideMapper evaluationGuideMapper;



    /**
     * 평가 템플릿 생성 로직
     *
     * @param templateDTO EvaluationTemplateRequestDTO
     *      평가 템플릿 생성 데이터를 파라미터로 받음.
     * @return templateId Integer
     *     생성된 평가 템플릿 테이블의 pk를 응답함
     */
    @Transactional
    public Integer createTemplate(EvaluationTemplateRequestDTO templateDTO) {

        /** 새로운 평가 템플릿 생성 */
        EvaluationTemplate template = new EvaluationTemplate();

        template.setName(templateDTO.getEvaluationTemplateName());
        template.setCreatedAt(templateDTO.getEvaluationTemplateCreatedAt());
        template.setEmployeeId(templateDTO.getEvaluationTemplateEmployeeId());
        template.setDepartmentId(templateDTO.getEvaluationTemplateDepartmentId());
        template.setType(templateDTO.getEvaluationTemplateType());

        templateRepository.save(template);
        Integer templateId = template.getTemplateId();

        if(templateDTO.getTemplateItems() != null) {
            for(TemplateItemRequestDTO itemDTO : templateDTO.getTemplateItems()) {

                /** 평가 템플릿과 연결된 새로운 평가 항목 생성 */
                TemplateItem item = new TemplateItem();

                item.setTemplateId(templateId);
                item.setItem(itemDTO.getTemplateItemItem());
                item.setDescription(itemDTO.getTemplateItemDescription());

                itemRepository.save(item);
                Integer itemId = item.getItemId();

                if(itemDTO.getCriterias() != null) {
                    for(CriteriaRequestDTO criteriaDTO : itemDTO.getCriterias()) {

                        /** 평가 항목과 연결된 새로운 평가 기준 생성 */
                        Criteria criteria = new Criteria();

                        criteria.setItemId(itemId);
                        criteria.setRank(criteriaDTO.getCriteriaRank());
                        criteria.setDesription(criteriaDTO.getCriteriaDescription());
                        criteria.setMinScore(criteriaDTO.getCriteriaMinScore());
                        criteria.setMaxScore(criteriaDTO.getCriteriaMaxScore());

                        criteriaRepository.save(criteria);
                    }
                }
            }
        }

        if(templateDTO.getEvaluationPeriod() != null) {

            /** 새로운 평가 기간 생성 */
            EvaluationPeriodRequestDTO periodDTO = templateDTO.getEvaluationPeriod();

            EvaluationPeriod period = new EvaluationPeriod();
            period.setTemplateId(templateId);
            period.setName(periodDTO.getEvaluationPeriodName());
            period.setStart(periodDTO.getEvaluationPeriodStart());
            period.setEnd(periodDTO.getEvaluationPeriodEnd());

            evaluationPeriodRepository.save(period);


        }

        return templateId;
    }

    /**
     * 평가 템플릿 조회(개별) 서비스 로직
     *
     * @param id Integer
     *      평가 템플릿 키(template_id)를 파라미터로 받음.
     * @return result EvaluationTemplateResponseDTO
     *     평가 템플릿 테이블의 pk로 특정 평가 템플릿 데이터를 응답함
     */
    public EvaluationTemplateResponseDTO selectTemplate(Integer id) {

        EvaluationTemplateResponseDTO result = evaluationTemplateMapper.selectTemplate(id);

        return result;
    }

    /**
     * 평가 템플릿 조회(전체) 서비스 로직
     *
     * @return result List<EvaluationTemplateResponseDTO>
     *     전체 평가 템플릿 데이터를 응답함
     */
    public List<EvaluationTemplateResponseDTO> selectAllTemplate() {

        List<EvaluationTemplateResponseDTO> result = evaluationTemplateMapper.selectAllTemplate();

        return result;
    }


    /**
     * 평가 템플릿 수정 서비스 로직
     *
     * @param evaluationTemplateDTO EvaluationTemplateUpdateDTO
     *      평가 템플릿 수정 데이터를 파라미터로 받음.
     * @return templateId Integer
     *     수정된 평가 템플릿 테이블의 pk를 응답함.
     */
    @Transactional
    public Integer updateTemplate(EvaluationTemplateUpdateDTO evaluationTemplateDTO) {

        /** 기존 평가 템플릿 수정 */
        EvaluationTemplate template = templateRepository.findById(evaluationTemplateDTO.getEvaluationTemplateTemplateId()).get();

        template.setName(evaluationTemplateDTO.getEvaluationTemplateName());
        template.setCreatedAt(evaluationTemplateDTO.getEvaluationTemplateCreatedAt());
        template.setEmployeeId(evaluationTemplateDTO.getEvaluationTemplateEmployeeId());
        template.setDepartmentId(evaluationTemplateDTO.getEvaluationTemplateDepartmentId());
        template.setType(evaluationTemplateDTO.getEvaluationTemplateType());

        templateRepository.save(template);
        Integer templateId = template.getTemplateId();

        if (evaluationTemplateDTO.getTemplateItems() != null) {
            for (TemplateItemUpdateDTO itemDTO : evaluationTemplateDTO.getTemplateItems()) {

                /** 평가 템플릿과 연관된 기존 평가 항목 수정 */
                TemplateItem item = itemRepository.findById(itemDTO.getTemplateItemItemId()).get();
                item.setTemplateId(templateId);
                item.setItem(itemDTO.getTemplateItemItem());
                item.setDescription(itemDTO.getTemplateItemDescription());
                itemRepository.save(item);

                Integer itemId = item.getItemId();
                if (itemDTO.getCriterias() != null) {
                    for (CriteriaUpdateDTO criteriaDTO : itemDTO.getCriterias()) {

                        /** 평가 항목과 연관된 기존 평가 기준 수정 */
                        Criteria criteria = criteriaRepository.findById(criteriaDTO.getCriteriaCriteriaId()).get();
                        criteria.setItemId(itemId);
                        criteria.setRank(criteriaDTO.getCriteriaRank());
                        criteria.setDesription(criteriaDTO.getCriteriaDescription());
                        criteria.setMinScore(criteriaDTO.getCriteriaMinScore());
                        criteria.setMaxScore(criteriaDTO.getCriteriaMaxScore());
                        criteriaRepository.save(criteria);
                    }
                }
            }
        }


        if (evaluationTemplateDTO.getEvaluationPeriodEvaluationPeriodId() != null) {

            /** 평가 템플릿과 연관된 기존 평가 기간 수정 */
            EvaluationPeriod period = evaluationPeriodRepository.findById(evaluationTemplateDTO.getEvaluationPeriodEvaluationPeriodId()).get();
            period.setTemplateId(templateId);
            period.setName(evaluationTemplateDTO.getEvaluationPeriodName());
            period.setStart(evaluationTemplateDTO.getEvaluationPeriodStart());
            period.setEnd(evaluationTemplateDTO.getEvaluationPeriodEnd());
            evaluationPeriodRepository.save(period);
        }

        return templateId;

    }

    /**
     * 평가 템플릿 삭제 서비스 로직
     *
     * @param id Integer
     *      삭제할 평가 템플릿의 pk(template_id)를 받음
     * @return void
     *     삭제 후 특정 데이터를 반환하지 않음.
     */
    @Transactional
    public void deleteTemplate(Integer id) {

        if(!templateRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 평가 템플릿입니다.");
        }
        templateRepository.deleteById(id);
    }

    /**
     * 평가 템플릿 생성 로직
     *
     * @param guideRequestDTO EvaluationGuideRequestDTO
     *      평가 가이드 생성 데이터를 파라미터로 받음.
     * @return templateId Integer
     *     생성된 평가 템플릿 테이블의 pk를 응답함
     */
    @Transactional
    public Integer createGuide(EvaluationGuideRequestDTO guideRequestDTO) {

        /** 평가 가이드 생성 로직 */
        EvaluationGuide guide = new EvaluationGuide();
        guide.setName(guideRequestDTO.getEvaluationGuideName());
        guide.setContent(guideRequestDTO.getEvaluationGuideContent());
        guide.setCreatedAt(guideRequestDTO.getEvaluationGuideCreatedAt());
        guide.setEmployeeId(guideRequestDTO.getEvaluationGuideEmployeeId());
        guide.setDepartmentId(guideRequestDTO.getEvaluationGuideDepartmentId());

        guideRepository.save(guide);

        Integer guideId = guide.getEvaluationGuideId();

        return guideId;
    }

    /**
     * 평가 가이드 조회(개별) 서비스 로직
     *
     * @param id Integer
     *      평가 가이드 키(evaluation_guide_id)를 파라미터로 받음.
     * @return result EvaluationGuideResponseDTO
     *     평가 가이드 테이블의 pk로 특정 평가 템플릿 데이터를 응답함
     */
    public EvaluationGuideResponseDTO selectGuide(Integer id) {

        EvaluationGuideResponseDTO result = evaluationGuideMapper.selectGuide(id);

        return result;
    }

    /**
     * 평가 가이드 조회 서비스 로직
     *
     * @return result EvaluationGuideResponseDTO
     *     평가 가이드 테이블의 pk로 특정 평가 템플릿 데이터를 응답함
     */
    public List<EvaluationGuideResponseDTO> selectAllGuide() {

        List<EvaluationGuideResponseDTO> result = evaluationGuideMapper.selectAllTemplate();

        return result;
    }

    /**
     * 평가 가이드 삭제 서비스 로직
     *
     * @param id Integer
     *      삭제할 평가 가이드의 pk(evaluation_guide_id)를 받음
     * @return void
     *     삭제 후 특정 데이터를 반환하지 않음.
     */
    @Transactional
    public void deleteGuide(Integer id) {

        if(!guideRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 평가 가이드입니다.");
        }
        guideRepository.deleteById(id);
    }
}
