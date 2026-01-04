package com.c4.hero.domain.evaluation.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.evaluation.dto.criteria.CriteriaRequestDTO;
import com.c4.hero.domain.evaluation.dto.criteria.CriteriaUpdateDTO;
import com.c4.hero.domain.evaluation.dto.dashboard.DashBoardResponseDTO;
import com.c4.hero.domain.evaluation.dto.employee.EmployeeResponseDTO;
import com.c4.hero.domain.evaluation.dto.evaluatee.EvaluateeRequestDTO;
import com.c4.hero.domain.evaluation.dto.evaluation.EvaluationRequestDTO;
import com.c4.hero.domain.evaluation.dto.evaluation.EvaluationResponseDTO;
import com.c4.hero.domain.evaluation.dto.form.EvaluationFormRequestDTO;
import com.c4.hero.domain.evaluation.dto.form.EvaluationFormResponseDTO;
import com.c4.hero.domain.evaluation.dto.form.EvaluationFormUpdateDTO;
import com.c4.hero.domain.evaluation.dto.guide.EvaluationGuideRequestDTO;
import com.c4.hero.domain.evaluation.dto.guide.EvaluationGuideResponseDTO;
import com.c4.hero.domain.evaluation.dto.guide.EvaluationGuideUpdateDTO;
import com.c4.hero.domain.evaluation.dto.item.*;
import com.c4.hero.domain.evaluation.dto.period.EvaluationPeriodRequestDTO;
import com.c4.hero.domain.evaluation.dto.response.EmployeeEvaluationListResponseDTO;
import com.c4.hero.domain.evaluation.dto.score.ItemScoreRequestDTO;
import com.c4.hero.domain.evaluation.dto.score.ItemScoreUpdateDTO;
import com.c4.hero.domain.evaluation.dto.template.EvaluationTemplateRequestDTO;
import com.c4.hero.domain.evaluation.dto.template.EvaluationTemplateResponseDTO;
import com.c4.hero.domain.evaluation.dto.template.EvaluationTemplateUpdateDTO;
import com.c4.hero.domain.evaluation.entity.*;
import com.c4.hero.domain.evaluation.mapper.*;
import com.c4.hero.domain.evaluation.repository.*;
import com.c4.hero.domain.notification.event.evaluation.EvaluationNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Class Name: EvaluationService
 * Description: 평가 관련 서비스 로직 처리
 *
 * History
 * 2025/12/07 (김승민) 최초 작성
 * 2026/01/03 (혜원) 알림 발송 로직 추가
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

    /** 평가서 저장소 의존성 주입 */
    private final EvaluationFormRepository formRepository;

    /** 평가서 항목 저장소 의존성 주입 */
    private final FormItemRepository formItemRepository;

    /** 평가서 항목 점수 저장소 의존성 주입 */
    private final ItemScoreRepository itemScoreRepository;

    /** 평가 템플릿 mapper 의존성 주입 */
    private final EvaluationTemplateMapper evaluationTemplateMapper;

    /** 평가 가이드 mapper 의존성 주입 */
    private final EvaluationGuideMapper evaluationGuideMapper;

    /** 피평가자 mapper 의존성 주입 */
    private final EvaluationEmployeeMapper evaluationEmployeeMapper;

    /** 평가 mapper 의존성 주입 */
    private final EvaluationMapper evaluationMapper;

    /** 평가서 mapper 의존성 주입 */
    private final EvaluationFormMapper evaluationFormMapper;

    /** 대시보드 데이터 mapper 의존성 주입 */
    private final DashBoardMapper dashBoardMapper;

    /** */
    private final ApplicationEventPublisher eventPublisher;  // 추가!

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
     * @return result PageResponse<EvaluationTemplateResponseDTO>
     *     전체 평가 템플릿 데이터를 응답함
     */
    public PageResponse<EvaluationTemplateResponseDTO> selectAllTemplate(
            int page,
            int size
    ) {
        int offset = page * size;

        /** template_id만 페이징 조회 */
        List<Integer> templateIds =
                evaluationTemplateMapper.selectTemplateIdsWithPaging(offset, size);

        if (templateIds.isEmpty()) {
            return PageResponse.of(List.of(), page, size, 0);
        }

        /** 실제 데이터 조회 */
        List<EvaluationTemplateResponseDTO> content =
                evaluationTemplateMapper.selectTemplatesByIds(templateIds);

        /** 전체 개수 */
        long totalElements =
                evaluationTemplateMapper.countAllTemplates();

        PageResponse<EvaluationTemplateResponseDTO> result = PageResponse.of(content, page, size, totalElements);

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
    public void deleteTemplate(Integer templateId) {

        if (templateId == null) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "삭제할 템플릿 ID가 없습니다."
            );
        }

        if (!templateRepository.existsById(templateId)) {
            throw new BusinessException(
                    ErrorCode.ENTITY_NOT_FOUND,
                    "삭제하려는 템플릿이 존재하지 않습니다."
            );
        }

        /** 템플릿에 속한 평가 항목 조회 */
        List<TemplateItem> items = itemRepository.findByTemplateId(templateId);

        /** 평가 기준 삭제 */
        if (items != null && !items.isEmpty()) {
            List<Integer> itemIds = items.stream()
                    .map(TemplateItem::getItemId)
                    .toList();

            criteriaRepository.deleteByItemIdIn(itemIds);
        }

        /** 평가 항목 삭제 */
        itemRepository.deleteByTemplateId(templateId);

        /** 평가 기간 삭제 */
        evaluationPeriodRepository.deleteByTemplateId(templateId);

        /** 평가 템플릿 삭제 */
        templateRepository.deleteById(templateId);
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
     * @return result PageResponse<EvaluationGuideResponseDTO>
     *     전체 평가 가이드 데이터를 응답함
     */
    public PageResponse<EvaluationGuideResponseDTO> selectAllGuide(int page, int size) {

        int offset = page * size;

        /** PK만 페이징 조회 */
        List<Integer> guideIds =
                evaluationGuideMapper.selectGuideIdsWithPaging(offset, size);

        if (guideIds.isEmpty()) {
            return PageResponse.of(List.of(), page, size, 0);
        }

        /** 실제 데이터 조회 */
        List<EvaluationGuideResponseDTO> content =
                evaluationGuideMapper.selectGuidesByIds(guideIds);

        /** 전체 개수 */
        long totalElements =
                evaluationGuideMapper.countAllGuides();

        return PageResponse.of(content, page, size, totalElements);
    }

    /**
     * 평가 가이드 조회 서비스 로직(평가 생성에 사용)
     *
     * @return result List<EvaluationGuideResponseDTO>
     *     전체 평가 가이드 데이터를 응답함
     */
    public List<EvaluationGuideResponseDTO> selectAllGuide2() {

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

        System.out.println("result = " + result);

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

        // 피평가자 ID 목록 (알림 발송용)
        List<Integer> employeeIds = new ArrayList<>();

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

                // 피평가자 ID 추가 (알림 발송용)
                employeeIds.add(evaluateeDTO.getEvaluateeEmployeeId());
            }
        }

        // 평가 시작 알림 발행
        publishEvaluationStartedEvent(evaluationId, evaluationDTO, employeeIds);

        return evaluationId;
    }

    /**
     * 평가 전체 조회 서비스 로직
     *
     * @return result PageResponse<EvaluationResponseDTO>
     *      평가 전체 조회 데이터 반환
     */
    public PageResponse<EvaluationResponseDTO> selectAllEvaluation(int page, int size) {

        int offset = page * size;

        /** PK 페이징 */
        List<Integer> evaluationIds =
                evaluationMapper.selectEvaluationIdsWithPaging(offset, size);

        if (evaluationIds.isEmpty()) {
            return PageResponse.of(List.of(), page, size, 0);
        }

        /** 실제 데이터 */
        List<EvaluationResponseDTO> content =
                evaluationMapper.selectEvaluationsByIds(evaluationIds);

        /** 전체 개수 */
        long totalElements =
                evaluationMapper.countAllEvaluations();

        PageResponse<EvaluationResponseDTO> result = PageResponse.of(content, page, size, totalElements);

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

    /**
     * 평가 삭제 로직
     *
     * @param evaluationId Integer
     *      평가 키(evaluation_id)를 파라미터로 받음.
     * @return void
     *     응답할 데이터가 없음.
     */
    @Transactional
    public void deleteEvaluation(Integer evaluationId) {

        if (evaluationId == null) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "삭제할 평가 ID가 없습니다."
            );
        }

        if (!evaluationRepository.existsById(evaluationId)) {
            throw new BusinessException(
                    ErrorCode.ENTITY_NOT_FOUND,
                    "삭제할 평가가 존재하지 않습니다."
            );
        }

        /** 선택 항목 삭제 */
        selectedItemRepository.deleteByEvaluationId(evaluationId);

        /** 피평가자 삭제 */
        evaluateeRepository.deleteByEvaluationId(evaluationId);

        /** 평가 삭제 */
        evaluationRepository.deleteById(evaluationId);
    }

    /**
     * 평가서 생성 로직
     *
     * @param formRequestDTO EvaluationRequestDTO
     *      평가서 생성 데이터를 파라미터로 받음.
     * @return evaluationId Integer
     *     생성된 평가서 테이블의 pk를 응답함
     */
    @Transactional
    public Integer createForm(EvaluationFormRequestDTO formRequestDTO) {

        if (formRequestDTO == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "평가서 생성 요청 데이터가 없습니다.");
        }

        /** 평가서 생성 */
        EvaluationForm form = new EvaluationForm();

        form.setEvaluationId(formRequestDTO.getEvaluationFormEvaluationId());
        form.setEmployeeId(formRequestDTO.getEvaluationFormEmployeeId());
        form.setDepartmentId(formRequestDTO.getEvaluationFormDepartmentId());
        form.setCreatedAt(formRequestDTO.getEvaluationFormCreatedAt());

        formRepository.save(form);
        Integer formId = form.getFormId();

        /** 평가서 항목, 점수 저장 */
        if (formRequestDTO.getFormItems() != null) {
            for (FormItemRequestDTO itemDTO : formRequestDTO.getFormItems()) {

                FormItem formItem = new FormItem();
                formItem.setFormId(formId);
                formItem.setSelectedItemId(itemDTO.getFormItemSelectedItemId());
                formItem.setWeight(itemDTO.getFormItemWeight());
                formItem.setDescription(itemDTO.getFormItemDescription());

                formItemRepository.save(formItem);
                Integer formItemId = formItem.getFormItemId();


                ItemScoreRequestDTO scoreDTO = itemDTO.getItemScore();
                if (scoreDTO != null) {
                    ItemScore score = new ItemScore();
                    score.setFormItemId(formItemId);
                    score.setScore(scoreDTO.getItemScoreScore());
                    score.setDescription(scoreDTO.getItemScoreDescription());
                    score.setRank(scoreDTO.getItemScoreRank());
                    itemScoreRepository.save(score);
                }
            }
        }

        /** 피평가자 상태 변경 */
        Evaluatee evaluatee =
                evaluateeRepository.findByEvaluationIdAndEmployeeId(
                        formRequestDTO.getEvaluationFormEvaluationId(),
                        formRequestDTO.getEvaluationFormEmployeeId()
                );

        if (evaluatee != null) {
            evaluatee.setStatus(1);
            evaluateeRepository.save(evaluatee);
        }

        /** 남은 미작성 피평가자 체크 */
        Long remaining =
                evaluateeRepository.countByEvaluationIdAndStatus(
                        formRequestDTO.getEvaluationFormEvaluationId(), 0
                );

        if (remaining == 0) {
            Evaluation evaluation = evaluationRepository.findById(formRequestDTO.getEvaluationFormEvaluationId()).get();
            evaluation.setStatus(1);
            evaluationRepository.save(evaluation);
        }

        return formId;
    }

    /**
     * 전체 평가서 조회 서비스 로직
     *
     * @return result List<EvaluationFormResponseDTO>
     *     전체 평가서 데이터를 응답함
     */
    public List<EvaluationFormResponseDTO> selectAllForm() {

        List<EvaluationFormResponseDTO> result = evaluationFormMapper.selectAllForm();

        return result;
    }

    /**
     * 평가서 조회(개별) 서비스 로직
     *
     * @return result EvaluationFormResponseDTO
     *     평가서 pk로 특정 평가서 데이터를 응답함
     */
    public EvaluationFormResponseDTO selectForm(Integer evaluationId, Integer employeeId) {

        EvaluationFormResponseDTO result = evaluationFormMapper.selectForm(evaluationId, employeeId);

        return result;
    }

    /**
     * 평가서 수정 서비스 로직
     *
     * @param updateDTO EvaluationFormUpdateDTO
     *      평가서 수정 데이터를 파라미터로 받음.
     * @return templateId Integer
     *     수정된 평가서 테이블의 pk를 응답함.
     */
    @Transactional
    public Integer updateForm(EvaluationFormUpdateDTO updateDTO) {
        EvaluationForm form = formRepository.findById(updateDTO.getEvaluationFormFormId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.ENTITY_NOT_FOUND, "수정할 평가서를 찾을 수 없습니다."
                ));

        /** 평가서 수정 */
        form.setCreatedAt(updateDTO.getEvaluationFormCreatedAt());

        formRepository.save(form);
        Integer formId = form.getFormId();


        /** 항목 수정 */
        if (updateDTO.getFormItems() != null) {
            for (FormItemUpdateDTO itemDTO : updateDTO.getFormItems()) {

                FormItem formItem = formItemRepository.findById(
                        itemDTO.getFormItemFormItemId()
                ).orElseThrow(() -> new BusinessException(
                        ErrorCode.ENTITY_NOT_FOUND, "수정할 평가서 항목이 없습니다."
                ));

                formItem.setWeight(itemDTO.getFormItemWeight());
                formItem.setDescription(itemDTO.getFormItemDescription());

                formItemRepository.save(formItem);
                Integer formItemId = formItem.getFormItemId();

                /* ===== 점수 처리 ===== */
                ItemScoreUpdateDTO scoreDTO = itemDTO.getItemScore();
                if (scoreDTO != null) {

                    ItemScore score;

                    score = itemScoreRepository.findById(
                            scoreDTO.getItemScoreItemScoreId()
                    ).orElseThrow(() -> new BusinessException(
                            ErrorCode.ENTITY_NOT_FOUND, "수정할 점수가 없습니다."
                    ));

                    score.setFormItemId(formItemId);
                    score.setScore(scoreDTO.getItemScoreScore());
                    score.setDescription(scoreDTO.getItemScoreDescription());
                    score.setRank(scoreDTO.getItemScoreRank());

                    itemScoreRepository.save(score);
                }
            }
        }

        return formId;

    }


    /**
     * 평가서 채점 서비스 로직
     *
     * @param updateDTO EvaluationFormUpdateDTO
     *      평가서 채점 데이터를 파라미터로 받음.
     * @return templateId Integer
     *     채점된 평가서 테이블의 pk를 응답함.
     */
    @Transactional
    public Integer gradingForm(EvaluationFormUpdateDTO updateDTO) {

        EvaluationForm form = formRepository.findById(updateDTO.getEvaluationFormFormId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.ENTITY_NOT_FOUND, "채점할 평가서를 찾을 수 없습니다."
                ));

        Integer evaluationId = form.getEvaluationId();
        Integer employeeId = form.getEmployeeId();

        /** 평가서 총평 입력 */
        form.setTotal(updateDTO.getEvaluationFormTotal());

        /** 평가서 항목 점수 채점 */
        if (updateDTO.getFormItems() != null) {
            for (FormItemUpdateDTO itemDTO : updateDTO.getFormItems()) {

                FormItem formItem = formItemRepository.findById(
                        itemDTO.getFormItemFormItemId()
                ).orElseThrow(() -> new BusinessException(
                        ErrorCode.ENTITY_NOT_FOUND, "채점할 평가서 항목이 없습니다."
                ));

                formItemRepository.save(formItem);

                ItemScoreUpdateDTO scoreDTO = itemDTO.getItemScore();
                if (scoreDTO != null) {
                    ItemScore score = itemScoreRepository.findById(
                            scoreDTO.getItemScoreItemScoreId()
                    ).orElseThrow(() -> new BusinessException(
                            ErrorCode.ENTITY_NOT_FOUND, "채점할 점수가 없습니다."
                    ));

                    score.setScore(scoreDTO.getItemScoreScore());
                    score.setDescription(scoreDTO.getItemScoreDescription());
                    score.setRank(scoreDTO.getItemScoreRank());
                    itemScoreRepository.save(score);
                }
            }
        }

        /** 가중치 반영 평가서 점수 계산 */
        List<FormItem> formItems = formItemRepository.findByFormId(form.getFormId());

        double weightedSum = 0.0;
        double weightTotal = 0.0;

        for (FormItem item : formItems) {
            ItemScore score = itemScoreRepository.findByFormItemId(item.getFormItemId());
            if (score == null || score.getScore() == null || item.getWeight() == null) {
                continue;
            }

            weightedSum += score.getScore() * item.getWeight();
            weightTotal += item.getWeight();
        }

        float totalScore = weightTotal > 0 ? (float) (weightedSum / weightTotal) : 0f;

        form.setTotalScore(totalScore);

        /** 상대평가 전이므로 totalRank는 여기서 설정하지 않음 */
        formRepository.save(form);

        /** 피평가자 상태 변경 */
        Evaluatee evaluatee =
                evaluateeRepository.findByEvaluationIdAndEmployeeId(evaluationId, employeeId);

        if (evaluatee != null) {
            evaluatee.setStatus(2);
            evaluateeRepository.save(evaluatee);
        }


        /** 모든 피평자가 완료 여부 확인 */
        Long remainingEvaluatee =
                evaluateeRepository.countByEvaluationIdAndStatusNot(evaluationId, 2);

        if (remainingEvaluatee == 0) {

            /** 모든 평가서 조회 */
            List<EvaluationForm> forms =
                    formRepository.findByEvaluationId(evaluationId);

            if (!forms.isEmpty()) {

                /** 상대평가 등급 계산 (S/A/B/C/F) */
                forms.sort((a, b) -> Float.compare(
                        b.getTotalScore(), a.getTotalScore()
                ));

                int total = forms.size();

                int sCount = Math.max(1, (int) Math.round(total * 0.03));
                int aCount = Math.max(1, (int) Math.round(total * 0.20));
                int bCount = Math.max(1, (int) Math.round(total * 0.65));
                int cCount = Math.max(1, (int) Math.round(total * 0.10));

                int index = 0;

                for (EvaluationForm f : forms) {

                    if (index < sCount) f.setTotalRank("S");
                    else if (index < sCount + aCount) f.setTotalRank("A");
                    else if (index < sCount + aCount + bCount) f.setTotalRank("B");
                    else if (index < sCount + aCount + bCount + cCount) f.setTotalRank("C");
                    else f.setTotalRank("F");

                    formRepository.save(f);

                    // 각 피평가자에게 평가 결과 알림 발행
                    publishEvaluationGradedEvent(f, evaluationId);
                    index++;
                }


                /** 평가 최종 점수 계산 */
                float evaluationAvgScore =
                        (float) forms.stream()
                                .map(EvaluationForm::getTotalScore)
                                .filter(s -> s != null)
                                .mapToDouble(Float::doubleValue)
                                .average()
                                .orElse(0.0);

                Evaluation evaluation =
                        evaluationRepository.findById(evaluationId).orElseThrow();

                evaluation.setTotalScore(evaluationAvgScore);
                evaluation.setStatus(2);
                evaluation.setEndedAt(LocalDateTime.now());

                evaluationRepository.save(evaluation);
            }
        }

        return form.getFormId();
    }


    /**
     * 평가의 최종 등급 상대평가 채점 로직
     *
     * @param templateId Integer
     *        평가를 조회하기 위한 평가 템플릿 ID
     */
    @Transactional
    public void finalizeEvaluationByTemplate(Integer templateId) {

        EvaluationPeriod period =
                evaluationPeriodRepository.findByTemplateId(templateId);

        if (period == null || period.getEnd().isAfter(LocalDateTime.now())) return;

        List<Evaluation> evaluations =
                evaluationRepository.findByTemplateId(templateId);

        /** 모든 부서 평가가 완료된 경우만 */
        boolean allCompleted =
                evaluations.stream()
                        .allMatch(e -> e.getStatus() != null && e.getStatus() == 2);

        if (!allCompleted) return;

        /** 이미 확정된 평가 제외 */
        boolean alreadyFinalized =
                evaluations.stream()
                        .allMatch(e -> e.getTotalRank() != null);

        if (alreadyFinalized) return;

        /** 부서 상대평가 */
        evaluations.sort((a, b) ->
                Float.compare(b.getTotalScore(), a.getTotalScore())
        );

        int total = evaluations.size();

        int s = Math.max(1, (int) Math.round(total * 0.03));
        int a = Math.max(1, (int) Math.round(total * 0.20));
        int b = Math.max(1, (int) Math.round(total * 0.65));
        int c = Math.max(1, (int) Math.round(total * 0.10));

        int idx = 0;
        for (Evaluation e : evaluations) {

            if (idx < s) e.setTotalRank("S");
            else if (idx < s + a) e.setTotalRank("A");
            else if (idx < s + a + b) e.setTotalRank("B");
            else if (idx < s + a + b + c) e.setTotalRank("C");
            else e.setTotalRank("F");

            evaluationRepository.save(e);
            idx++;
        }
    }

    /**
     * 전체 대시보드 데이터 조회 서비스 로직
     *
     * @return result List<DashBoardResponseDTO>
     *     전체 대시보드 데이터를 응답함.
     */
    public List<DashBoardResponseDTO> selectAllDashBoard() {

        List<DashBoardResponseDTO> result = dashBoardMapper.selectAllDashBoard();

        return result;
    }

    /**
     * 대시보드 데이터 department_id로 조회하는 서비스 로직
     *
     * @param id Integer
     *     부서 ID를 요청함
     *
     * @return result List<DashBoardResponseDTO>
     *     부서 ID로 조회된 대시보드 데이터를 응답함.
     */
    public List<DashBoardResponseDTO> selectDashBoard(Integer id) {

        List<DashBoardResponseDTO> result = dashBoardMapper.selectDashBoard(id);

        return result;
    }

    /**
     * 특정 직원의 평가 결과 목록을 조회합니다.
     *
     * @param employeeId 직원 ID
     * @return 평가 결과 목록
     */
    public List<EmployeeEvaluationListResponseDTO> getEmployeeEvaluationList(Integer employeeId) {
        return evaluationMapper.findEvaluationFormsByEmployeeId(employeeId);
    }

    /* ========================================== */
    /* 평가 알림 헬퍼 메서드 */
    /* ========================================== */

    /**
     * 평가 시작 알림 이벤트 발행 (피평가자들에게)
     * <pre>
     * 호출 시점: 평가 생성 시
     * 수신자: 모든 피평가자
     * 알림 내용: "'XXX' 평가가 시작되었습니다."
     * </pre>
     * @param evaluationId 평가 ID
     * @param requestDTO 평가 생성 요청 DTO
     * @param employeeIds 피평가자 ID 목록
     */
    private void publishEvaluationStartedEvent(
            Integer evaluationId,
            EvaluationRequestDTO requestDTO,
            List<Integer> employeeIds
    ) {
        // 평가 기간 정보 조회
        EvaluationPeriod period = evaluationPeriodRepository.findById(
                requestDTO.getEvaluationEvaluationPeriodId()
        ).orElse(null);

        EvaluationNotificationEvent.EvaluationStartedEvent event =
                EvaluationNotificationEvent.EvaluationStartedEvent.builder()
                        .evaluationId(evaluationId)
                        .evaluationName(requestDTO.getEvaluationName())
                        .employeeIds(employeeIds)
                        .startDate(period != null ? period.getStart() : LocalDateTime.now())
                        .endDate(period != null ? period.getEnd() : null)
                        .build();

        eventPublisher.publishEvent(event);
    }

    /**
     * 평가 결과 등록 알림 이벤트 발행 (피평가자에게)
     * <pre>
     * 호출 시점: 평가서 채점 완료 시
     * 수신자: 피평가자
     * 알림 내용: "'XXX' 평가 결과가 등록되었습니다. (등급: A)"
     * </pre>
     * @param form 채점 완료된 평가서
     * @param evaluationId 평가 ID
     */
    private void publishEvaluationGradedEvent(EvaluationForm form, Integer evaluationId) {
        // 평가 정보 조회
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.ENTITY_NOT_FOUND, "평가를 찾을 수 없습니다."
                ));

        EvaluationNotificationEvent.EvaluationGradedEvent event =
                EvaluationNotificationEvent.EvaluationGradedEvent.builder()
                        .formId(form.getFormId())
                        .evaluationId(evaluationId)
                        .evaluationName(evaluation.getName())
                        .employeeId(form.getEmployeeId())
                        .grade(form.getTotalRank())
                        .gradedAt(LocalDateTime.now())
                        .build();

        eventPublisher.publishEvent(event);
    }
}
