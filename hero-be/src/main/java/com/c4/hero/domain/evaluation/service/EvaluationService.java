package com.c4.hero.domain.evaluation.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.evaluation.dto.*;
import com.c4.hero.domain.evaluation.entity.*;
import com.c4.hero.domain.evaluation.mapper.EvaluationEmployeeMapper;
import com.c4.hero.domain.evaluation.mapper.EvaluationGuideMapper;
import com.c4.hero.domain.evaluation.mapper.EvaluationMapper;
import com.c4.hero.domain.evaluation.mapper.EvaluationTemplateMapper;
import com.c4.hero.domain.evaluation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    /** 평가 저장소 의존성 주입 */
    private final EvaluationRepository evaluationRepository;

    /** 평가 선택 항목 저장소 의존성 주입 */
    private final SelectedItemRepository selectedItemRepository;

    /** 피평가자 저장소 의존성 주입 */
    private final EvaluateeRepository evaluateeRepository;

    /** 평가 템플릿 mapper 의존성 주입 */
    private final EvaluationTemplateMapper evaluationTemplateMapper;

    /** 평가 가이드 mapper 의존성 주입 */
    private final EvaluationGuideMapper evaluationGuideMapper;

    /** 피평가자 mapper 의존성 주입 */
    private final EvaluationEmployeeMapper evaluationEmployeeMapper;

    /** 평가 mapper 의존성 주입 */
    private final EvaluationMapper evaluationMapper;

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

        if (templateDTO == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "템플릿 생성 요청 데이터가 비어있습니다.");
        }

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

        if (id == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "조회할 템플릿 ID가 없습니다.");
        }

        EvaluationTemplateResponseDTO result = evaluationTemplateMapper.selectTemplate(id);

        if (result == null) {
            throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "해당 템플릿을 찾을 수 없습니다. (id=" + id + ")");
        }

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

        if (evaluationTemplateDTO == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "템플릿 수정 요청 데이터가 없습니다.");
        }

        /** 기존 평가 템플릿 수정 */
        EvaluationTemplate template = templateRepository.findById(evaluationTemplateDTO.getEvaluationTemplateTemplateId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "수정할 템플릿을 찾을 수 없습니다."));

        template.setName(evaluationTemplateDTO.getEvaluationTemplateName());
        template.setCreatedAt(evaluationTemplateDTO.getEvaluationTemplateCreatedAt());
        template.setEmployeeId(evaluationTemplateDTO.getEvaluationTemplateEmployeeId());
        template.setDepartmentId(evaluationTemplateDTO.getEvaluationTemplateDepartmentId());
        template.setType(evaluationTemplateDTO.getEvaluationTemplateType());

        templateRepository.save(template);
        Integer templateId = template.getTemplateId();


        /** 삭제된 기준 먼저 삭제 */
        if (evaluationTemplateDTO.getDeletedCriteriaIds() != null) {
            criteriaRepository.deleteAllById(evaluationTemplateDTO.getDeletedCriteriaIds());
        }

        /** 삭제된 항목 삭제 */
        if (evaluationTemplateDTO.getDeletedItemIds() != null) {
            itemRepository.deleteAllById(evaluationTemplateDTO.getDeletedItemIds());
        }

        if (evaluationTemplateDTO.getTemplateItems() != null) {
            for (TemplateItemUpdateDTO itemDTO : evaluationTemplateDTO.getTemplateItems()) {

                TemplateItem item;

                /** 기존 항목이면 UPDATE */
                if (itemDTO.getTemplateItemItemId() != null) {
                    item = itemRepository.findById(itemDTO.getTemplateItemItemId()).get();
                }
                /** 새 항목이면 INSERT */
                else {
                    item = new TemplateItem();
                    item.setTemplateId(templateId);
                }

                /** 평가 템플릿과 연관된 기존 평가 항목 수정 */
                item.setItem(itemDTO.getTemplateItemItem());
                item.setDescription(itemDTO.getTemplateItemDescription());

                itemRepository.save(item);
                Integer itemId = item.getItemId();

                if (itemDTO.getCriterias() != null) {
                    for (CriteriaUpdateDTO criteriaDTO : itemDTO.getCriterias()) {

                        Criteria criteria;

                        /** 기존 기준 UPDATE */
                        if (criteriaDTO.getCriteriaCriteriaId() != null) {
                            criteria = criteriaRepository.findById(criteriaDTO.getCriteriaCriteriaId()).get();
                        }
                        /** 새로운 기준 INSERT */
                        else {
                            criteria = new Criteria();
                            criteria.setItemId(itemId);
                        }

                        /** 평가 항목과 연관된 기존 평가 기준 수정 */
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
            EvaluationPeriod period = evaluationPeriodRepository.findById(evaluationTemplateDTO.getEvaluationPeriodEvaluationPeriodId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "수정할 평가 기간이 없습니다."));
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

        if (!templateRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "삭제하려는 템플릿이 존재하지 않습니다.");
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

        if (guideRequestDTO == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "가이드 생성 요청 데이터가 없습니다.");
        }

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

        if (id == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "조회할 가이드 ID가 없습니다.");
        }

        EvaluationGuideResponseDTO result = evaluationGuideMapper.selectGuide(id);

        if (result == null) {
            throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "해당 가이드를 찾을 수 없습니다.");
        }

        return result;
    }

    /**
     * 평가 가이드 조회 서비스 로직
     *
     * @return result EvaluationGuideResponseDTO
     *     평가 가이드 테이블의 pk로 특정 평가 템플릿 데이터를 응답함
     */
    public List<EvaluationGuideResponseDTO> selectAllGuide() {

        List<EvaluationGuideResponseDTO> result = evaluationGuideMapper.selectAllGuide();

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

        if (!guideRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "삭제할 가이드가 존재하지 않습니다.");
        }
        guideRepository.deleteById(id);
    }

    /**
     * 평가 가이드 수정 서비스 로직
     *
     * @param evaluationGuideDTO EvaluationGuideUpdateDTO
     *      평가 템플릿 수정 데이터를 파라미터로 받음.
     * @return templateId Integer
     *     수정된 평가 템플릿 테이블의 pk를 응답함.
     */
    @Transactional
    public Integer updateGuide(EvaluationGuideUpdateDTO evaluationGuideDTO) {

        if (evaluationGuideDTO == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "가이드 수정 요청 데이터가 없습니다.");
        }
        /** 평가 가이드 수정 로직 */
        EvaluationGuide guide = guideRepository.findById(evaluationGuideDTO.getEvaluationGuideEvaluationGuideId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "수정할 가이드를 찾을 수 없습니다."));

        guide.setName(evaluationGuideDTO.getEvaluationGuideName());
        guide.setContent(evaluationGuideDTO.getEvaluationGuideContent());
        guide.setCreatedAt(evaluationGuideDTO.getEvaluationGuideCreatedAt());
        guide.setEmployeeId(evaluationGuideDTO.getEvaluationGuideEmployeeId());
        guide.setDepartmentId(evaluationGuideDTO.getEvaluationGuideDepartmentId());

        guideRepository.save(guide);
        Integer guideId = guide.getEvaluationGuideId();

        return guideId;
    }

    /**
     * 피평가자들 조회 서비스 로직
     *
     * @param id Integer
     *      클라이언트로 부터 온 부서 id
     * @return result List<EmployeeResponseDTO>
     *      부서 id로 조회한 사원들 목록
     */
    public List<EmployeeResponseDTO> selectEmployeeByDepartmentId(Integer id) {

        List<EmployeeResponseDTO> result = evaluationEmployeeMapper.selectEmployeeByDepartmentId(id);

        return result;
    }

    /**
     * 평가 생성 로직
     *
     * @param evaluationDTO EvaluationRequestDTO
     *      평가 생성 데이터를 파라미터로 받음.
     * @return evaluationId Integer
     *     생성된 평가 테이블의 pk를 응답함
     */
    @Transactional
    public Integer createEvaluation(EvaluationRequestDTO evaluationDTO) {

        if (evaluationDTO == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "평가 생성 요청 데이터가 없습니다.");
        }

        Evaluation evaluation = new Evaluation();

        evaluation.setEmployeeId(evaluationDTO.getEvaluationEmployeeId());
        evaluation.setDepartmentId(evaluationDTO.getEvaluationDepartmentId());
        evaluation.setTemplateId(evaluationDTO.getEvaluationTemplateId());
        evaluation.setName(evaluationDTO.getEvaluationName());
        evaluation.setStatus(evaluationDTO.getEvaluationStatus());
        evaluation.setCreatedAt(evaluationDTO.getEvaluationCreatedAt());
        evaluation.setEvaluationGuideId(evaluationDTO.getEvaluationEvaluationGuideId());
        evaluation.setEvaluationPeriodId(evaluationDTO.getEvaluationEvaluationPeriodId());

        /** 평가 저장 */
        evaluationRepository.save(evaluation);
        Integer evaluationId = evaluation.getEvaluationId();

        /** 평가 선택 항목 저장 */
        if (evaluationDTO.getSelectedItems() != null) {
            for (SelectedItemRequestDTO itemDTO : evaluationDTO.getSelectedItems()) {

                SelectedItem selectedItem = new SelectedItem();
                selectedItem.setEvaluationId(evaluationId);
                selectedItem.setItemId(itemDTO.getSelectedItemItemId());

                selectedItemRepository.save(selectedItem);
            }
        }

        /** 피평가자 저장 */
        if (evaluationDTO.getEvaluatees() != null) {
            for (EvaluateeRequestDTO evaluateeDTO : evaluationDTO.getEvaluatees()) {

                Evaluatee evaluatee = new Evaluatee();
                evaluatee.setEvaluationId(evaluationId);
                evaluatee.setEmployeeId(evaluateeDTO.getEvaluateeEmployeeId());
                evaluatee.setStatus(evaluateeDTO.getEvaluateeStatus());

                evaluateeRepository.save(evaluatee);
            }
        }

        return evaluationId;
    }

    /**
     * 평가 전체 조회 서비스 로직
     *
     * @return result List<EvaluationResponseDTO>
     *      평가 전체 조회 데이터 반환
     */
    public List<EvaluationResponseDTO> selectAllEvaluation() {

        List<EvaluationResponseDTO> result = evaluationMapper.selectAllEvaluation();

        return result;
    }

    /**
     * 평가 조회(개별) 서비스 로직
     *
     * @param id Integer
     *      평가 키(evaluation_id)를 파라미터로 받음.
     * @return result EvaluationResponseDTO
     *     평가 pk로 특정 평가 데이터를 응답함
     */
    public EvaluationResponseDTO selectEvaluation(Integer id) {

        EvaluationResponseDTO result = evaluationMapper.selectEvaluation(id);

        return result;
    }
}
