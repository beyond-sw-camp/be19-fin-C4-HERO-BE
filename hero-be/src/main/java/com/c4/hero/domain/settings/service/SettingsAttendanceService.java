package com.c4.hero.domain.settings.service;

import com.c4.hero.domain.attendance.entity.WorkSystemTemplate;
import com.c4.hero.domain.attendance.entity.WorkSystemType;
import com.c4.hero.domain.settings.dto.request.SettingWorkSystemRequestDTO;
import com.c4.hero.domain.settings.dto.response.SettingWorkSystemResponseDTO;
import com.c4.hero.domain.settings.mapper.SettingsMapper;
import com.c4.hero.domain.settings.repository.SettingAttTemplateRepository;
import com.c4.hero.domain.settings.repository.SettingAttTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <pre>
 * Class Name: SettingsAttendanceService
 * Description: 근태 설정(근무제 템플릿/근무제 유형) 관련 비즈니스 로직을 처리하는 서비스 클래스
 *
 * History
 * 2025/12/29 (지윤) 최초 작성 및 컨벤션 적용
 * 2026/01/07 (혜원) 신규 근무제 생성 시 WorkSystemType도 함께 생성하도록 수정
 * </pre>
 *
 * 근무제 템플릿(WorkSystemTemplate)에 대한 조회 및 일괄 저장(Upsert, Insert) 기능을 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettingsAttendanceService {

    /** 근태 설정 관련 조회용 MyBatis Mapper */
    private final SettingsMapper settingsMapper;

    /** 근무제 템플릿 저장/조회용 JPA Repository */
    private final SettingAttTemplateRepository settingAttTemplateRepository;

    /** 근무제 유형(WorkSystemType) 저장/조회용 JPA Repository */
    private final SettingAttTypeRepository settingAttTypeRepository;

    /**
     * 근무제 템플릿 목록을 조회합니다.
     *
     * <p>
     * MyBatis Mapper를 사용하여 설정 화면에서 필요한
     * 근무제 템플릿 리스트를 전달합니다.
     * </p>
     *
     * @return 근무제 템플릿 응답 DTO 리스트
     */
    public List<SettingWorkSystemResponseDTO> getWorkSystemTemplates() {
        List<SettingWorkSystemResponseDTO> list = settingsMapper.selectWorkSystemTemplates();
        log.debug("WorkSystemTemplates count={}", (list == null ? 0 : list.size()));

        return list;
    }

    /**
     * 근무제 템플릿 일괄 Upsert(INSERT/UPDATE)를 수행합니다.
     *
     * <p>규칙</p>
     * <ul>
     *     <li>신규 템플릿: {@code workSystemTemplateId == null} → INSERT (WorkSystemType도 함께 생성)</li>
     *     <li>기존 템플릿: {@code workSystemTemplateId != null} → UPDATE</li>
     * </ul>
     *
     * <p>처리 순서</p>
     * <ol>
     *     <li>요청 리스트 유효성 검증</li>
     *     <li>신규: WorkSystemType 생성 후 WorkSystemTemplate 생성</li>
     *     <li>수정: 기존 템플릿 로딩 후 업데이트</li>
     *     <li>saveAll로 일괄 저장</li>
     * </ol>
     *
     * @param requestList 근무제 템플릿 저장 요청 리스트
     */
    @Transactional
    public void upsertWorkSystemTemplates(List<SettingWorkSystemRequestDTO> requestList) {
        if (requestList == null || requestList.isEmpty()) {
            return;
        }

        // 1) 기본 유효성 검증
        for (SettingWorkSystemRequestDTO dto : requestList) {
            if (dto.getStartTime() == null || dto.getEndTime() == null) {
                throw new IllegalArgumentException("startTime/endTime은 필수입니다.");
            }
            if (dto.getBreakMinMinutes() == null || dto.getBreakMinMinutes() < 0) {
                throw new IllegalArgumentException("breakMinMinutes는 0 이상이어야 합니다.");
            }
            if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
                throw new IllegalArgumentException("reason(근무제명/사유)은 필수입니다.");
            }
        }

        List<WorkSystemTemplate> toSave = new ArrayList<>();

        // 2) 신규와 수정 분리
        List<SettingWorkSystemRequestDTO> newRequests = requestList.stream()
                .filter(dto -> dto.getWorkSystemTemplateId() == null)
                .toList();

        List<SettingWorkSystemRequestDTO> updateRequests = requestList.stream()
                .filter(dto -> dto.getWorkSystemTemplateId() != null)
                .toList();

        // 3) 신규 생성 처리 (WorkSystemType도 함께 생성)
        for (SettingWorkSystemRequestDTO dto : newRequests) {
            // WorkSystemType 먼저 생성
            WorkSystemType newType = WorkSystemType.create(
                    dto.getReason(),  // name으로 사용
                    dto.getStartTime(),  // flexStartMin
                    dto.getStartTime(),  // flexStartMax
                    true  // isFixedSchedule (고정 근무제)
            );
            WorkSystemType savedType = settingAttTypeRepository.save(newType);

            log.info("신규 WorkSystemType 생성 완료 - ID: {}, Name: {}",
                    savedType.getWorkSystemTypeId(), savedType.getWorkSystemName());

            // WorkSystemTemplate 생성
            WorkSystemTemplate newTemplate = WorkSystemTemplate.create(
                    dto.getStartTime(),
                    dto.getEndTime(),
                    dto.getBreakMinMinutes(),
                    dto.getReason(),
                    savedType
            );
            toSave.add(newTemplate);

            log.info("신규 WorkSystemTemplate 생성 준비 - Reason: {}", dto.getReason());
        }

        // 4) 기존 템플릿 수정 처리
        if (!updateRequests.isEmpty()) {
            // 필요한 WorkSystemType 로딩
            Set<Integer> typeIds = updateRequests.stream()
                    .map(SettingWorkSystemRequestDTO::getWorkSystemTypeId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            Map<Integer, WorkSystemType> typeMap = settingAttTypeRepository.findAllById(typeIds).stream()
                    .collect(Collectors.toMap(WorkSystemType::getWorkSystemTypeId, t -> t));

            // 기존 템플릿 로딩
            List<Integer> existingIds = updateRequests.stream()
                    .map(SettingWorkSystemRequestDTO::getWorkSystemTemplateId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            Map<Integer, WorkSystemTemplate> existingTemplateMap =
                    settingAttTemplateRepository.findAllById(existingIds).stream()
                            .collect(Collectors.toMap(WorkSystemTemplate::getId, t -> t));

            // 수정 처리
            for (SettingWorkSystemRequestDTO dto : updateRequests) {
                if (dto.getWorkSystemTypeId() == null) {
                    throw new IllegalArgumentException("수정 시 workSystemTypeId는 필수입니다.");
                }

                WorkSystemType type = typeMap.get(dto.getWorkSystemTypeId());
                if (type == null) {
                    throw new NoSuchElementException(
                            "존재하지 않는 workSystemTypeId=" + dto.getWorkSystemTypeId()
                    );
                }

                WorkSystemTemplate target = existingTemplateMap.get(dto.getWorkSystemTemplateId());
                if (target == null) {
                    throw new NoSuchElementException(
                            "존재하지 않는 workSystemTemplateId=" + dto.getWorkSystemTemplateId()
                    );
                }

                target.update(
                        dto.getStartTime(),
                        dto.getEndTime(),
                        dto.getBreakMinMinutes(),
                        dto.getReason(),
                        type
                );

                // WorkSystemType의 name도 업데이트
                type.updateName(dto.getReason());

                toSave.add(target);

                log.info("WorkSystemTemplate 수정 완료 - ID: {}", dto.getWorkSystemTemplateId());
            }
        }

        // 5) 일괄 저장
        settingAttTemplateRepository.saveAll(toSave);

        log.info("근무제 템플릿 저장 완료 - 총 {}건 (신규: {}건, 수정: {}건)",
                toSave.size(), newRequests.size(), updateRequests.size());
    }
}