package com.c4.hero.domain.settings.service;

import com.c4.hero.domain.approval.entity.ApprovalTemplate;
import com.c4.hero.domain.approval.repository.ApprovalTemplateRepository;
import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.settings.dto.SettingsDefaultLineDTO;
import com.c4.hero.domain.settings.dto.SettingsDefaultRefDTO;
import com.c4.hero.domain.settings.dto.request.SettingsApprovalRequestDTO;
import com.c4.hero.domain.settings.entity.SettingsApprovalLine;
import com.c4.hero.domain.settings.entity.SettingsApprovalRef;
import com.c4.hero.domain.settings.enums.TargetType;
import com.c4.hero.domain.settings.repository.SettingsApprovalLineRepository;
import com.c4.hero.domain.settings.repository.SettingsApprovalRefRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SettingsCommandServiceTest {

    @Mock
    private ApprovalTemplateRepository templateRepository;

    @Mock
    private SettingsApprovalLineRepository settingsApprovalLineRepository;

    @Mock
    private SettingsApprovalRefRepository settingsApprovalRefRepository;

    @InjectMocks
    private SettingsCommandService approvalSettingsService;

    @Test
    @DisplayName("성공: 기존 설정을 삭제하고 새로운 결재선과 참조선을 저장한다")
    void applySettings_success() {
        // Given
        Integer templateId = 1;
        ApprovalTemplate mockTemplate = ApprovalTemplate.builder().templateId(templateId).build();

        // 요청 데이터: 결재선 1개, 참조선 1개
        SettingsDefaultLineDTO lineDto = SettingsDefaultLineDTO.builder()
                .seq(1)
                .targetType(TargetType.SPECIFIC_DEPT.name())
                .departmentId(100)
                .build();

        SettingsDefaultRefDTO refDto = SettingsDefaultRefDTO.builder()
                .targetType(TargetType.DRAFTER_DEPT.name())
                .departmentId(0) // 기안자 부서는 ID가 0이거나 없을 수 있음
                .build();

        SettingsApprovalRequestDTO request = SettingsApprovalRequestDTO.builder()
                .lines(List.of(lineDto))
                .references(List.of(refDto))
                .build();

        given(templateRepository.findById(templateId)).willReturn(Optional.of(mockTemplate));

        // When
        approvalSettingsService.applySettings(templateId, request);

        // Then
        // 1. 기존 데이터 삭제 로직 호출 검증 (Idempotency 보장 확인)
        verify(settingsApprovalLineRepository, times(1)).deleteAllByTemplate(mockTemplate);
        verify(settingsApprovalRefRepository, times(1)).deleteAllByTemplate(mockTemplate);

        // 2. 결재선 저장 데이터 검증 (ArgumentCaptor 사용)
        /* verify(repo).saveAll(anyList()) 대신 Captor를 쓰면
           실제로 저장된 Entity의 값을 꺼내서 검증할 수 있습니다.
        */
        ArgumentCaptor<List<SettingsApprovalLine>> lineCaptor = ArgumentCaptor.forClass(List.class);
        verify(settingsApprovalLineRepository).saveAll(lineCaptor.capture());

        List<SettingsApprovalLine> savedLines = lineCaptor.getValue();
        assertThat(savedLines).hasSize(1);
        assertThat(savedLines.get(0).getDepartmentId()).isEqualTo(100);
        assertThat(savedLines.get(0).getTemplate()).isEqualTo(mockTemplate);

        // 3. 참조선 저장 데이터 검증
        ArgumentCaptor<List<SettingsApprovalRef>> refCaptor = ArgumentCaptor.forClass(List.class);
        verify(settingsApprovalRefRepository).saveAll(refCaptor.capture());

        List<SettingsApprovalRef> savedRefs = refCaptor.getValue();
        assertThat(savedRefs).hasSize(1);
        assertThat(savedRefs.get(0).getTemplate()).isEqualTo(mockTemplate);
    }

    @Test
    @DisplayName("성공: 유효하지 않은 타겟 타입은 필터링되고 저장되지 않는다")
    void applySettings_filterInvalidTypes() {
        // Given
        Integer templateId = 1;
        ApprovalTemplate mockTemplate = ApprovalTemplate.builder().templateId(templateId).build();

        // INVALID_TYPE 이라는 이상한 타입이 들어왔다고 가정
        SettingsDefaultLineDTO invalidLine = SettingsDefaultLineDTO.builder()
                .targetType("UNKNOWN_TYPE")
                .departmentId(999)
                .build();

        SettingsApprovalRequestDTO request = SettingsApprovalRequestDTO.builder()
                .lines(List.of(invalidLine))
                .references(List.of())
                .build();

        given(templateRepository.findById(templateId)).willReturn(Optional.of(mockTemplate));

        // When
        approvalSettingsService.applySettings(templateId, request);

        // Then
        // 빈 리스트가 저장되거나, 아예 saveAll이 호출되지 않아야 함 (구현 방식에 따라 다름)
        // 여기서는 리팩토링된 코드 기준 "빈 리스트는 저장하지 않음" 로직이라면 saveAll 호출이 0회여야 함
        verify(settingsApprovalLineRepository, times(0)).saveAll(any());
    }

    @Test
    @DisplayName("실패: 템플릿이 존재하지 않으면 예외가 발생한다")
    void applySettings_fail_templateNotFound() {
        // Given
        Integer invalidTemplateId = 999;
        SettingsApprovalRequestDTO request = SettingsApprovalRequestDTO.builder().build();

        given(templateRepository.findById(invalidTemplateId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> approvalSettingsService.applySettings(invalidTemplateId, request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ENTITY_NOT_FOUND);
    }
}