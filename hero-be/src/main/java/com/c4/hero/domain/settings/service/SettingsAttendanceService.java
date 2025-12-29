package com.c4.hero.domain.settings.service;

import com.c4.hero.domain.attendance.entity.WorkSystemTemplate;
import com.c4.hero.domain.attendance.entity.WorkSystemType;
import com.c4.hero.domain.settings.dto.request.WSTAttReqDTO;
import com.c4.hero.domain.settings.dto.response.WSTAttResDTO;
import com.c4.hero.domain.settings.mapper.SettingsMapper;
import com.c4.hero.domain.settings.repository.SettingAttTemplateRepository;
import com.c4.hero.domain.settings.repository.SettingAttTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 2025/12/29 (이지윤) 최초 작성 및 컨벤션 적용
 * </pre>
 *
 * 근무제 템플릿(WorkSystemTemplate)에 대한 조회 및 일괄 저장(Upsert) 기능을 제공합니다.
 * - 조회: MyBatis Mapper를 이용하여 현재 설정된 템플릿 목록을 조회
 * - 저장: JPA Repository를 이용하여 신규 템플릿 추가/기존 템플릿 수정 수행
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
    public List<WSTAttResDTO> getWorkSystemTemplates() {
        List<WSTAttResDTO> list = settingsMapper.selectWorkSystemTemplates();
        log.debug("WorkSystemTemplates count={}", (list == null ? 0 : list.size()));

        return list;
    }

    /**
     * 근무제 템플릿 일괄 Upsert(INSERT/UPDATE)를 수행합니다.
     *
     * <p>규칙</p>
     * <ul>
     *     <li>신규 템플릿: {@code workSystemTemplateId == null} → INSERT</li>
     *     <li>기존 템플릿: {@code workSystemTemplateId != null} → UPDATE</li>
     * </ul>
     *
     * <p>처리 순서</p>
     * <ol>
     *     <li>요청 리스트 유효성 검증</li>
     *     <li>필요한 근무제 유형(WorkSystemType) 일괄 로딩 및 존재 여부 검증</li>
     *     <li>수정 대상 템플릿(WorkSystemTemplate) 일괄 로딩</li>
     *     <li>INSERT/UPDATE 분기 처리 후 saveAll로 일괄 저장</li>
     * </ol>
     *
     * @param requestList 근무제 템플릿 저장 요청 리스트
     */
    @Transactional // 클래스 레벨 readOnly=true를 override
    public void upsertWorkSystemTemplates(List<WSTAttReqDTO> requestList) {
        if (requestList == null) {
            return;
        }

        // 1) 기본 유효성 검증
        for (WSTAttReqDTO dto : requestList) {
            if (dto.getWorkSystemTypeId() == null) {
                throw new IllegalArgumentException("workSystemTypeId는 필수입니다.");
            }
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

        // 2) 한 번에 필요한 WorkSystemType 로딩 (N+1 방지)
        Set<Integer> typeIds = requestList.stream()
                .map(WSTAttReqDTO::getWorkSystemTypeId)
                .collect(Collectors.toSet());

        Map<Integer, WorkSystemType> typeMap =
                settingAttTypeRepository.findAllById(typeIds).stream()
                        .collect(Collectors.toMap(WorkSystemType::getWorkSystemTypeId, t -> t));

        // 존재하지 않는 타입이 있으면 에러
        for (Integer typeId : typeIds) {
            if (!typeMap.containsKey(typeId)) {
                throw new NoSuchElementException("존재하지 않는 workSystemTypeId=" + typeId);
            }
        }

        // 3) 기존 템플릿도 한 번에 로딩 (UPDATE 대상)
        List<Integer> existingIds = requestList.stream()
                .map(WSTAttReqDTO::getWorkSystemTemplateId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Integer, WorkSystemTemplate> existingTemplateMap =
                settingAttTemplateRepository.findAllById(existingIds).stream()
                        .collect(Collectors.toMap(WorkSystemTemplate::getId, t -> t));

        // 4) INSERT/UPDATE 분기 처리
        List<WorkSystemTemplate> toSave = new ArrayList<>();

        for (WSTAttReqDTO dto : requestList) {
            WorkSystemType type = typeMap.get(dto.getWorkSystemTypeId());

            // (A) INSERT: workSystemTemplateId == null
            if (dto.getWorkSystemTemplateId() == null) {
                WorkSystemTemplate created = WorkSystemTemplate.create(
                        dto.getStartTime(),
                        dto.getEndTime(),
                        dto.getBreakMinMinutes(),
                        dto.getReason(),
                        type
                );
                toSave.add(created);
                continue;
            }

            // (B) UPDATE: workSystemTemplateId != null
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

            // JPA dirty checking으로도 업데이트가 반영되지만,
            // saveAll에 함께 넘겨 명시적으로 처리해도 무방합니다.
            toSave.add(target);
        }

        // 5) 저장
        settingAttTemplateRepository.saveAll(toSave);
    }
}
