package com.c4.hero.domain.settings.dto.request;

import com.c4.hero.domain.settings.dto.SettingsDefaultLineDTO;
import com.c4.hero.domain.settings.dto.SettingsDefaultRefDTO;
import lombok.*;

import java.util.List;

/**
 * <pre>
 * Class Name: ApprovalLineSettingsRequestDTO
 * Description:
 *  - 결재선 설정 입력 데이터
 *
 * History
 * 2025/12/18 (민철) 최초작성
 *
 * </pre>
 *
 * @author 민철
 * @version 1.0
 */
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SettingsApprovalRequestDTO {


    private List<SettingsDefaultLineDTO> lines;     // 기본 결재선 - 결재자의 부서

    private List<SettingsDefaultRefDTO> references; // 기본 참조목록 - 참조자의 부서
}
