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
import com.c4.hero.domain.evaluation.dto.item.FormItemRequestDTO;
import com.c4.hero.domain.evaluation.dto.item.FormItemUpdateDTO;
import com.c4.hero.domain.evaluation.dto.item.TemplateItemRequestDTO;
import com.c4.hero.domain.evaluation.dto.item.TemplateItemUpdateDTO;
import com.c4.hero.domain.evaluation.dto.period.EvaluationPeriodRequestDTO;
import com.c4.hero.domain.evaluation.dto.score.ItemScoreRequestDTO;
import com.c4.hero.domain.evaluation.dto.score.ItemScoreUpdateDTO;
import com.c4.hero.domain.evaluation.dto.template.EvaluationTemplateRequestDTO;
import com.c4.hero.domain.evaluation.dto.template.EvaluationTemplateResponseDTO;
import com.c4.hero.domain.evaluation.dto.template.EvaluationTemplateUpdateDTO;
import com.c4.hero.domain.evaluation.entity.FormItem;
import com.c4.hero.domain.evaluation.entity.*;
import com.c4.hero.domain.evaluation.mapper.*;
import com.c4.hero.domain.evaluation.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * <pre>
 * Class Name: EvaluationServiceTest
 * Description: 평가 관련 서비스 로직 테스트
 *
 * History
 * 2025/12/27 (김승민) 최초 작성
 * </pre>
 *
 * @author 김승민
 */

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {

    /** Repository Mocks */
    @Mock
    private EvaluationTemplateRepository templateRepository;

    @Mock
    private TemplateItemRepository itemRepository;

    @Mock
    private CriteriaRepository criteriaRepository;

    @Mock
    private EvaluationPeriodRepository evaluationPeriodRepository;

    @Mock
    private EvaluationGuideRepository guideRepository;

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private SelectedItemRepository selectedItemRepository;

    @Mock
    private EvaluateeRepository evaluateeRepository;

    @Mock
    private EvaluationFormRepository formRepository;

    @Mock
    private FormItemRepository formItemRepository;

    @Mock
    private ItemScoreRepository itemScoreRepository;


    /** Mapper Mocks */
    @Mock
    private EvaluationTemplateMapper evaluationTemplateMapper;

    @Mock
    private EvaluationGuideMapper evaluationGuideMapper;

    @Mock
    private EvaluationEmployeeMapper evaluationEmployeeMapper;

    @Mock
    private EvaluationMapper evaluationMapper;

    @Mock
    private EvaluationFormMapper evaluationFormMapper;

    @Mock
    private DashBoardMapper dashBoardMapper;

    /** 테스트하려는 실제 evaluationService (내부 의존성들을 Mock 객체로 주입) */
    @InjectMocks
    private EvaluationService evaluationService;

    /** 특정 타입의 객체가 메서드에서 호출 인자로 전달되면 그 객체를 검사 */
    @Captor
    private ArgumentCaptor<EvaluationTemplate> templateCaptor;

    @Captor
    private ArgumentCaptor<TemplateItem> templateItemCaptor;

    @Captor
    private ArgumentCaptor<Criteria> criteriaCaptor;

    @Captor
    private ArgumentCaptor<EvaluationPeriod> periodCaptor;

    @Captor
    private ArgumentCaptor<Evaluation> evaluationCaptor;

    @Captor
    private ArgumentCaptor<EvaluationForm> formCaptor;

    @Captor
    private ArgumentCaptor<ItemScore> scoreCaptor;

    @Captor
    private ArgumentCaptor<SelectedItem> selectedItemCaptor;

    @Captor
    private ArgumentCaptor<Evaluatee> evaluateeCaptor;

    /** 평가 템플릿 */
    @Nested
    @DisplayName("평가 템플릿 생성 테스트")
    class CreateTemplateTests {

        @Test
        @DisplayName("템플릿 생성 성공")
        void createTemplate_success_withItemsCriteriasPeriod() {
            // given
            EvaluationTemplateRequestDTO dto = new EvaluationTemplateRequestDTO();
            dto.setEvaluationTemplateName("2026 상반기");
            dto.setEvaluationTemplateCreatedAt(LocalDateTime.now());
            dto.setEvaluationTemplateEmployeeId(1);
            dto.setEvaluationTemplateDepartmentId(2);
            dto.setEvaluationTemplateType(1);

            // 평가 항목, 평가 기준 설정
            TemplateItemRequestDTO item1 = new TemplateItemRequestDTO();
            item1.setTemplateItemItem("업무 수행");
            item1.setTemplateItemDescription("업무 수행 능력");

            CriteriaRequestDTO c1 = new CriteriaRequestDTO();
            c1.setCriteriaRank("S");
            c1.setCriteriaDescription("탁월");
            c1.setCriteriaMinScore(90);
            c1.setCriteriaMaxScore(100);

            item1.setCriterias(List.of(c1));
            dto.setTemplateItems(List.of(item1));

            // 평가 기간 설정
            EvaluationPeriodRequestDTO periodDTO = new EvaluationPeriodRequestDTO();
            periodDTO.setEvaluationPeriodName("기간");
            periodDTO.setEvaluationPeriodStart(LocalDateTime.now().minusDays(3));
            periodDTO.setEvaluationPeriodEnd(LocalDateTime.now().plusDays(3));
            dto.setEvaluationPeriod(periodDTO);

            // save() 호출 시, DB가 PK=10을 발급한 것처럼 처리
            doAnswer(inv -> {
                EvaluationTemplate t = inv.getArgument(0);
                t.setTemplateId(10);
                return t;
            }).when(templateRepository).save(any(EvaluationTemplate.class));

            // save() 호출 시, itemId = 100 이 생긴 것처럼 처리
            doAnswer(inv -> {
                TemplateItem it = inv.getArgument(0);
                it.setItemId(100);
                return it;
            }).when(itemRepository).save(any(TemplateItem.class));

            // when
            Integer templateId = evaluationService.createTemplate(dto);

            // then
            assertThat(templateId).isEqualTo(10);

            verify(templateRepository).save(templateCaptor.capture());
            assertThat(templateCaptor.getValue().getName()).isEqualTo("2026 상반기");

            verify(itemRepository).save(templateItemCaptor.capture());
            assertThat(templateItemCaptor.getValue().getTemplateId()).isEqualTo(10);

            verify(criteriaRepository).save(criteriaCaptor.capture());
            assertThat(criteriaCaptor.getValue().getItemId()).isEqualTo(100);

            verify(evaluationPeriodRepository).save(periodCaptor.capture());
            assertThat(periodCaptor.getValue().getTemplateId()).isEqualTo(10);
        }

        @Test
        @DisplayName("템플릿 생성 실패: DTO가 null일 때")
        void createTemplate_fail_null() {
            // when, then
            assertThatThrownBy(() -> evaluationService.createTemplate(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("템플릿 생성 요청 데이터가 비어있습니다.");
        }
    }

    @Nested
    @DisplayName("평가 템플릿 조회 테스트")
    class SelectTemplateTests {

        @Test
        @DisplayName("템플릿 조회 성공")
        void selectTemplate_success() {
            // given
            EvaluationTemplateResponseDTO resp = new EvaluationTemplateResponseDTO();
            when(evaluationTemplateMapper.selectTemplate(1)).thenReturn(resp);

            // when
            EvaluationTemplateResponseDTO result = evaluationService.selectTemplate(1);

            // then
            assertThat(result).isSameAs(resp);
        }

        @Test
        @DisplayName("템플릿 조회 실패: 평가 테플릿 id null")
        void selectTemplate_fail_nullId() {
            assertThatThrownBy(() -> evaluationService.selectTemplate(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("조회할 템플릿 ID가 없습니다.");
        }

        @Test
        @DisplayName("템플릿 조회 실패: 결과가 null")
        void selectTemplate_fail_notFound() {
            when(evaluationTemplateMapper.selectTemplate(1)).thenReturn(null);

            assertThatThrownBy(() -> evaluationService.selectTemplate(1))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("해당 템플릿을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("평가 템플릿 전체 조회 테스트")
    class SelectAllTemplateTests {

        @Test
        @DisplayName("템플릿 전체 조회 실패: 템플릿 없음")
        void selectAllTemplate_empty() {
            when(evaluationTemplateMapper.selectTemplateIdsWithPaging(anyInt(), anyInt()))
                    .thenReturn(List.of());

            PageResponse<EvaluationTemplateResponseDTO> result =
                    evaluationService.selectAllTemplate(0, 10);

            assertThat(result.getContent()).isEmpty();
            verify(evaluationTemplateMapper, never()).selectTemplatesByIds(anyList());
        }

        @Test
        @DisplayName("템플릿 전체 조회 성공")
        void selectAllTemplate_success() {
            when(evaluationTemplateMapper.selectTemplateIdsWithPaging(0, 10))
                    .thenReturn(List.of(1, 2));

            when(evaluationTemplateMapper.selectTemplatesByIds(List.of(1, 2)))
                    .thenReturn(List.of(new EvaluationTemplateResponseDTO(), new EvaluationTemplateResponseDTO()));

            when(evaluationTemplateMapper.countAllTemplates()).thenReturn(2L);

            PageResponse<EvaluationTemplateResponseDTO> result =
                    evaluationService.selectAllTemplate(0, 10);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("평가 템플릿 수정 테스트")
    class UpdateTemplateTests {

        @Test
        @DisplayName("템플릿 수정 성공")
        void updateTemplate_success() {
            // given
            EvaluationTemplateUpdateDTO dto = new EvaluationTemplateUpdateDTO();
            dto.setEvaluationTemplateTemplateId(10);
            dto.setEvaluationTemplateName("수정");
            dto.setEvaluationTemplateCreatedAt(LocalDateTime.now());
            dto.setEvaluationTemplateEmployeeId(1);
            dto.setEvaluationTemplateDepartmentId(2);
            dto.setEvaluationTemplateType(1);

            EvaluationTemplate existing = new EvaluationTemplate();
            existing.setTemplateId(10);

            when(templateRepository.findById(10)).thenReturn(Optional.of(existing));
            when(templateRepository.save(any(EvaluationTemplate.class))).thenAnswer(inv -> inv.getArgument(0));

            // when
            Integer id = evaluationService.updateTemplate(dto);

            // then
            assertThat(id).isEqualTo(10);
            verify(templateRepository).save(existing);
            assertThat(existing.getName()).isEqualTo("수정");
        }

        @Test
        @DisplayName("템플릿 수정 실패: 템플릿이 null")
        void updateTemplate_fail_null() {
            assertThatThrownBy(() -> evaluationService.updateTemplate(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("템플릿 수정 요청 데이터가 없습니다.");
        }
    }

    @Nested
    @DisplayName("평가 템플릿 삭제 테스트")
    class DeleteTemplateTests {

        @Test
        @DisplayName("템플릿 삭제 실패: 테플릿 id가 null")
        void deleteTemplate_fail_null() {
            assertThatThrownBy(() -> evaluationService.deleteTemplate(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("삭제할 템플릿 ID가 없습니다.");
        }

        @Test
        @DisplayName("템플릿 삭제 실패: 삭제하려는 템플릿이 존재하지 않음")
        void deleteTemplate_fail_notExist() {
            when(templateRepository.existsById(10)).thenReturn(false);

            assertThatThrownBy(() -> evaluationService.deleteTemplate(10))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("삭제하려는 템플릿이 존재하지 않습니다.");
        }

        @Test
        @DisplayName("템플릿 삭제 성공")
        void deleteTemplate_success() {
            when(templateRepository.existsById(10)).thenReturn(true);

            TemplateItem item1 = new TemplateItem();
            item1.setItemId(100);
            TemplateItem item2 = new TemplateItem();
            item2.setItemId(101);

            when(itemRepository.findByTemplateId(10)).thenReturn(List.of(item1, item2));

            // when
            evaluationService.deleteTemplate(10);

            // then
            verify(criteriaRepository).deleteByItemIdIn(List.of(100, 101));
            verify(itemRepository).deleteByTemplateId(10);
            verify(evaluationPeriodRepository).deleteByTemplateId(10);
            verify(templateRepository).deleteById(10);
        }
    }

    /** 평가 가이드 */
    @Nested
    @DisplayName("평가 가이드 생성 테스트")
    class CreateGuideTests {

        @Test
        @DisplayName("가이드 생성 성공")
        void createGuide_success() {
            // given
            EvaluationGuideRequestDTO dto = new EvaluationGuideRequestDTO();
            dto.setEvaluationGuideName("가이드");
            dto.setEvaluationGuideContent("내용");
            dto.setEvaluationGuideCreatedAt(LocalDateTime.now());
            dto.setEvaluationGuideEmployeeId(1);
            dto.setEvaluationGuideDepartmentId(2);

            doAnswer(inv -> {
                EvaluationGuide g = inv.getArgument(0);
                g.setEvaluationGuideId(99);
                return g;
            }).when(guideRepository).save(any(EvaluationGuide.class));

            //when
            Integer id = evaluationService.createGuide(dto);

            //then
            assertThat(id).isEqualTo(99);
            verify(guideRepository).save(any(EvaluationGuide.class));
        }

        @Test
        @DisplayName("가이드 생성 실패: DTO가 null")
        void createGuide_fail_null() {
            assertThatThrownBy(() -> evaluationService.createGuide(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("가이드 생성 요청 데이터가 없습니다.");
        }
    }

    @Nested
    @DisplayName("평가 가이드 조회 테스트")
    class SelectGuideTests {

        @Test
        @DisplayName("가이드 조회 성공")
        void selectGuide_success() {
            EvaluationGuideResponseDTO resp = new EvaluationGuideResponseDTO();
            when(evaluationGuideMapper.selectGuide(1)).thenReturn(resp);

            EvaluationGuideResponseDTO result = evaluationService.selectGuide(1);

            assertThat(result).isSameAs(resp);
        }

        @Test
        @DisplayName("가이드 조회 실패: 가이드 id가 null")
        void selectGuide_fail_null() {
            assertThatThrownBy(() -> evaluationService.selectGuide(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("조회할 가이드 ID가 없습니다.");
        }

        @Test
        @DisplayName("가이드 조회 실패: 결과가 null")
        void selectGuide_fail_notFound() {
            when(evaluationGuideMapper.selectGuide(1)).thenReturn(null);

            assertThatThrownBy(() -> evaluationService.selectGuide(1))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("해당 가이드를 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("가이드 전체 조회 실패: 결과가 null")
        void selectAllGuide_empty() {
            when(evaluationGuideMapper.selectGuideIdsWithPaging(anyInt(), anyInt()))
                    .thenReturn(List.of());

            PageResponse<EvaluationGuideResponseDTO> result =
                    evaluationService.selectAllGuide(0, 10);

            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("평가 가이드 삭제 테스트")
    class DeleteGuideTests {
        @Test
        @DisplayName("가이드 삭제 실패: 삭제할 가이드가 null")
        void deleteGuide_fail() {
            when(guideRepository.existsById(10)).thenReturn(false);

            assertThatThrownBy(() -> evaluationService.deleteGuide(10))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("삭제할 가이드가 존재하지 않습니다.");
        }

        @Test
        @DisplayName("가이드 삭제 성공")
        void deleteGuide_success() {
            when(guideRepository.existsById(10)).thenReturn(true);

            evaluationService.deleteGuide(10);

            verify(guideRepository).deleteById(10);
        }
    }

    /** 평가 */
    @Nested
    @DisplayName("평가 생성, 삭제 테스트")
    class EvaluationTests {

        @Test
        @DisplayName("평가 생성 성공")
        void createEvaluation_success() {
            //given
            EvaluationRequestDTO dto = new EvaluationRequestDTO();
            dto.setEvaluationEmployeeId(1);
            dto.setEvaluationDepartmentId(2);
            dto.setEvaluationTemplateId(3);
            dto.setEvaluationName("평가");
            dto.setEvaluationStatus(0);
            dto.setEvaluationCreatedAt(LocalDateTime.now());
            dto.setEvaluationEvaluationGuideId(5);
            dto.setEvaluationEvaluationPeriodId(6);

            // 선택된 평가 항목
            var si = new com.c4.hero.domain.evaluation.dto.item.SelectedItemRequestDTO();
            si.setSelectedItemItemId(100);
            dto.setSelectedItems(List.of(si));

            // 피평가자
            EvaluateeRequestDTO e1 = new EvaluateeRequestDTO();
            e1.setEvaluateeEmployeeId(11);
            e1.setEvaluateeStatus(0);
            dto.setEvaluatees(List.of(e1));

            doAnswer(inv -> {
                Evaluation e = inv.getArgument(0);
                e.setEvaluationId(77);
                return e;
            }).when(evaluationRepository).save(any(Evaluation.class));

            //when
            Integer evalId = evaluationService.createEvaluation(dto);

            //then
            assertThat(evalId).isEqualTo(77);

            verify(evaluationRepository).save(any(Evaluation.class));
            verify(selectedItemRepository).save(selectedItemCaptor.capture());
            assertThat(selectedItemCaptor.getValue().getEvaluationId()).isEqualTo(77);

            verify(evaluateeRepository).save(evaluateeCaptor.capture());
            assertThat(evaluateeCaptor.getValue().getEvaluationId()).isEqualTo(77);
        }

        @Test
        @DisplayName("평가 생성 실패: DTO가 null")
        void createEvaluation_fail_null() {
            assertThatThrownBy(() -> evaluationService.createEvaluation(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("평가 생성 요청 데이터가 없습니다.");
        }

        @Test
        @DisplayName("평가 삭제 실패: 평가 id가 null")
        void deleteEvaluation_fail_null() {
            assertThatThrownBy(() -> evaluationService.deleteEvaluation(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("삭제할 평가 ID가 없습니다.");
        }

        @Test
        @DisplayName("평가 삭제 실패: 삭제할 평가가 없음")
        void deleteEvaluation_fail_notExist() {
            when(evaluationRepository.existsById(10)).thenReturn(false);

            assertThatThrownBy(() -> evaluationService.deleteEvaluation(10))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("삭제할 평가가 존재하지 않습니다.");
        }

        @Test
        @DisplayName("평가 삭제 성공")
        void deleteEvaluation_success() {
            when(evaluationRepository.existsById(10)).thenReturn(true);

            evaluationService.deleteEvaluation(10);

            verify(selectedItemRepository).deleteByEvaluationId(10);
            verify(evaluateeRepository).deleteByEvaluationId(10);
            verify(evaluationRepository).deleteById(10);
        }
    }

    /** 평가서 */
    @Nested
    @DisplayName("평가서 생성 테스트")
    class CreateFormTests {

        @Test
        @DisplayName("평가서 생성 성공")
        void createForm_success() {
            //given
            EvaluationFormRequestDTO dto = new EvaluationFormRequestDTO();
            dto.setEvaluationFormEvaluationId(10);
            dto.setEvaluationFormEmployeeId(11);
            dto.setEvaluationFormDepartmentId(2);
            dto.setEvaluationFormCreatedAt(LocalDateTime.now());

            //평가서 항목
            FormItemRequestDTO itemDTO = new FormItemRequestDTO();
            itemDTO.setFormItemSelectedItemId(100);
            itemDTO.setFormItemWeight(0.5f);
            itemDTO.setFormItemDescription("desc");

            //평가서 점수
            ItemScoreRequestDTO scoreDTO = new ItemScoreRequestDTO();
            scoreDTO.setItemScoreScore(80);
            scoreDTO.setItemScoreDescription("좋음");
            scoreDTO.setItemScoreRank("A");
            itemDTO.setItemScore(scoreDTO);

            dto.setFormItems(List.of(itemDTO));

            doAnswer(inv -> {
                EvaluationForm f = inv.getArgument(0);
                f.setFormId(55);
                return f;
            }).when(formRepository).save(any(EvaluationForm.class));

            doAnswer(inv -> {
                com.c4.hero.domain.evaluation.entity.FormItem fi = inv.getArgument(0);
                fi.setFormItemId(99);
                return fi;
            }).when(formItemRepository).save(any(com.c4.hero.domain.evaluation.entity.FormItem.class));

            Evaluatee evaluatee = new Evaluatee();
            when(evaluateeRepository.findByEvaluationIdAndEmployeeId(10, 11)).thenReturn(evaluatee);

            when(evaluateeRepository.countByEvaluationIdAndStatus(10, 0)).thenReturn(1L);

            //when
            Integer formId = evaluationService.createForm(dto);

            //then
            assertThat(formId).isEqualTo(55);

            verify(formRepository).save(any(EvaluationForm.class));
            verify(formItemRepository).save(any(com.c4.hero.domain.evaluation.entity.FormItem.class));
            verify(itemScoreRepository).save(scoreCaptor.capture());
            assertThat(scoreCaptor.getValue().getScore()).isEqualTo(80f);

            verify(evaluateeRepository).save(evaluatee);
            assertThat(evaluatee.getStatus()).isEqualTo(1);
        }
    }

    /** 평가서 채점 */
    @Nested
    @DisplayName("평가서 채점 테스트")
    class GradingFormTests {

        @Test
        @DisplayName("평가서 채점")
        void gradingForm_calculateWeightedAverage() {
            // given
            EvaluationFormUpdateDTO dto = new EvaluationFormUpdateDTO();
            dto.setEvaluationFormFormId(1);
            dto.setEvaluationFormTotal("총평");
            dto.setEvaluationFormCreatedAt(LocalDateTime.now());

            // 채점 항목
            FormItemUpdateDTO itemU1 = new FormItemUpdateDTO();
            itemU1.setFormItemFormItemId(10);

            // 점수
            ItemScoreUpdateDTO scoreU1 = new ItemScoreUpdateDTO();
            scoreU1.setItemScoreItemScoreId(100);
            scoreU1.setItemScoreScore(80);
            scoreU1.setItemScoreDescription("ok");
            scoreU1.setItemScoreRank("A");
            itemU1.setItemScore(scoreU1);

            dto.setFormItems(List.of(itemU1));

            EvaluationForm form = new EvaluationForm();
            form.setFormId(1);
            form.setEvaluationId(7);
            form.setEmployeeId(11);

            when(formRepository.findById(1)).thenReturn(Optional.of(form));

            // 평가서 항목 조회
            FormItem fiEntity = new FormItem();
            fiEntity.setFormItemId(10);
            fiEntity.setWeight(0.6f);
            when(formItemRepository.findById(10)).thenReturn(Optional.of(fiEntity));

            // 항목 점수 조회
            ItemScore scoreEntity = new ItemScore();
            scoreEntity.setItemScoreId(100);
            when(itemScoreRepository.findById(100)).thenReturn(Optional.of(scoreEntity));

            // 평가서 항목 2개 설정
            FormItem fi1 = new FormItem();
            fi1.setFormItemId(201);
            fi1.setWeight(0.6f);

            FormItem fi2 = new FormItem();
            fi2.setFormItemId(202);
            fi2.setWeight(0.4f);

            when(formItemRepository.findByFormId(1)).thenReturn(List.of(fi1, fi2));

            ItemScore s1 = new ItemScore(); s1.setScore(80);
            ItemScore s2 = new ItemScore(); s2.setScore(90);

            when(itemScoreRepository.findByFormItemId(201)).thenReturn(s1);
            when(itemScoreRepository.findByFormItemId(202)).thenReturn(s2);

            // 피평가자 상태 변경
            Evaluatee evaluatee = new Evaluatee();
            when(evaluateeRepository.findByEvaluationIdAndEmployeeId(7, 11)).thenReturn(evaluatee);

            when(evaluateeRepository.countByEvaluationIdAndStatusNot(7, 2)).thenReturn(999L);

            // when
            Integer result = evaluationService.gradingForm(dto);

            // then
            assertThat(result).isEqualTo(1);

            verify(formRepository, atLeastOnce()).save(formCaptor.capture());
            EvaluationForm saved = formCaptor.getValue();
            assertThat(saved.getTotalScore()).isEqualTo(84.0f);

            verify(evaluateeRepository).save(evaluatee);
            assertThat(evaluatee.getStatus()).isEqualTo(2);
        }
    }

    /** 대시보드 */
    @Nested
    @DisplayName("대시보드 데이이터 조회 테스트")
    class DashboardTests {

        @Test
        @DisplayName("selectAllDashBoard() mapper 결과 반환")
        void selectAllDashBoard_success() {
            List<DashBoardResponseDTO> list = List.of(new DashBoardResponseDTO());
            when(dashBoardMapper.selectAllDashBoard()).thenReturn(list);

            List<DashBoardResponseDTO> result = evaluationService.selectAllDashBoard();

            assertThat(result).isSameAs(list);
        }

        @Test
        @DisplayName("selectDashBoard(deptId) mapper 결과 반환")
        void selectDashBoard_success() {
            List<DashBoardResponseDTO> list = List.of(new DashBoardResponseDTO());
            when(dashBoardMapper.selectDashBoard(2)).thenReturn(list);

            List<DashBoardResponseDTO> result = evaluationService.selectDashBoard(2);

            assertThat(result).isSameAs(list);
        }
    }
}