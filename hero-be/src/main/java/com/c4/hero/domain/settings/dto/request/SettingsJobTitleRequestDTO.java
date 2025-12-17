package com.c4.hero.domain.settings.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name: SettingsJobTitleRequestDTO
 * Description: 직책 설정 요청 DTO
 *
 * History
 * 2025/12/16 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsJobTitleRequestDTO {
	private Integer jobTitleId;
	private String jobTitleName;
}
