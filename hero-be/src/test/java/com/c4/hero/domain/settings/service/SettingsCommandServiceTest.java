package com.c4.hero.domain.settings.service;

import com.c4.hero.domain.approval.entity.ApprovalFormTemplate;
import com.c4.hero.domain.approval.repository.ApprovalTemplateRepository;
import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.settings.dto.SettingsDefaultLineDTO;
import com.c4.hero.domain.settings.dto.SettingsDefaultRefDTO;
import com.c4.hero.domain.settings.dto.request.SettingsApprovalRequestDTO;
import com.c4.hero.domain.settings.enums.TargetType;
import com.c4.hero.domain.settings.repository.SettingsApprovalLineRepository;
import com.c4.hero.domain.settings.repository.SettingsApprovalRefRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
/**
 * <pre>
 * Class Name: Settings CommandServiceTest
 * Description:
 *  - 설정 관련 서비스 로직 테스트 코드
 *  - 결재 기본 설정 관련 : 기본 결재선 / 기본 참조 목록
 *
 * History
 * 2025/12/19 (민철) 최초작성
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
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
    @DisplayName("결재 설정 적용 성공 - 결재선과 참조가 정상적으로 저장된다")
    void applySettings_success() {
        // Given (준비)
        Integer templateId = 1;

        // 1. 요청 DTO 생성 (결재선 2개, 참조 1개)
        SettingsDefaultLineDTO line1 = SettingsDefaultLineDTO.builder()
                .seq(1)
                .targetType(TargetType.SPECIFIC_DEPT.name())
                .departmentId(10)
                .build();

        SettingsDefaultLineDTO line2 = SettingsDefaultLineDTO.builder()
                .seq(2)
                .targetType(TargetType.DRAFTER_DEPT.name())
                .departmentId(0)
                .build();

        SettingsDefaultRefDTO ref1 = SettingsDefaultRefDTO.builder()
                .targetType(TargetType.SPECIFIC_DEPT.name())
                .departmentId(20)
                .build();

        SettingsApprovalRequestDTO request = SettingsApprovalRequestDTO.builder()
                .templateId(templateId)
                .lines(List.of(line1, line2))
                .references(List.of(ref1))
                .build();

        // 2. Mock 행동 정의 (템플릿 조회 시 객체 리턴)
        ApprovalFormTemplate mockTemplate = new ApprovalFormTemplate(); // 필요시 빌더로 ID 등 설정
        given(templateRepository.findById(templateId)).willReturn(Optional.of(mockTemplate));

        // When (실행)
        String result = approvalSettingsService.applySettings(request);

        // Then (검증)
        // 1. 리턴 메시지 확인
        assertThat(result).isEqualTo("결재 설정 적용 성공");

        // 2. 템플릿 조회가 1번 호출되었는지 검증
        verify(templateRepository, times(1)).findById(templateId);

        // 3. 결재선 저장이 1번 호출되었는지 검증
        verify(settingsApprovalLineRepository, times(1)).saveAll(anyList());

        // 4. 참조선 저장이 1번 호출되었는지 검증
        verify(settingsApprovalRefRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("결재 설정 실패 - 존재하지 않는 템플릿 ID인 경우 예외 발생")
    void applySettings_fail_templateNotFound() {
        // Given
        Integer invalidTemplateId = 999;
        SettingsApprovalRequestDTO request = SettingsApprovalRequestDTO.builder()
                .templateId(invalidTemplateId)
                .lines(List.of())
                .references(List.of())
                .build();

        // Mock: 조회 시 Optional.empty() 반환
        given(templateRepository.findById(invalidTemplateId)).willReturn(Optional.empty());

        // When & Then (실행 및 예외 검증)
        assertThatThrownBy(() -> approvalSettingsService.applySettings(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode") // ErrorCode 필드가 있다면 검증
                .isEqualTo(ErrorCode.ENTITY_NOT_FOUND);
    }

    @Test
    @DisplayName("결재 설정 실패 - 결재선 저장 중 DB 에러 발생")
    void applySettings_fail_lineSaveError() {
        // Given
        Integer templateId = 1;
        SettingsDefaultLineDTO line = SettingsDefaultLineDTO.builder()
                .seq(1)
                .targetType(TargetType.SPECIFIC_DEPT.name())
                .departmentId(10)
                .build();

        SettingsApprovalRequestDTO request = SettingsApprovalRequestDTO.builder()
                .templateId(templateId)
                .lines(List.of(line)) // 결재선이 있어야 저장 로직을 탐
                .references(List.of())
                .build();

        ApprovalFormTemplate mockTemplate = new ApprovalFormTemplate();
        given(templateRepository.findById(templateId)).willReturn(Optional.of(mockTemplate));

        // Mock: saveAll 호출 시 RuntimeException 발생시키기
        given(settingsApprovalLineRepository.saveAll(anyList()))
                .willThrow(new RuntimeException("DB Connection Error"));

        // When & Then
        assertThatThrownBy(() -> approvalSettingsService.applySettings(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("기본결재선 적용 실패"); // 서비스 코드에서 throw하는 메시지
    }
}