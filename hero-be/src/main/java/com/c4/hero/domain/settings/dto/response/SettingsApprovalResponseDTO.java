package com.c4.hero.domain.settings.dto.response;

import com.c4.hero.domain.settings.dto.SettingsDefaultLineDTO;
import com.c4.hero.domain.settings.dto.SettingsDefaultRefDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * <pre>
 * Class Name: SettingsApprovalResponseDTO
 * Description: 서식에 대한 기본 결재선/참조목록 조회 응답 데이터
 *
 * History
 * 2025/12/21 (민철) 각 단계별 기본 결재/참조 설정 리스트
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsApprovalResponseDTO {

    private List<SettingsDefaultLineDTO> lines;

    private List<SettingsDefaultRefDTO> references;
}
