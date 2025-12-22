package com.c4.hero.domain.settings.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
/**
 * <pre>
 * Class Name: SettingsDocumentTemplateResponseDTO
 * Description: 서식목록조회 응답 DTO
 *
 * History
 * 2025/12/19 (민철) 최초작성
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
public class SettingsDocumentTemplateResponseDTO {

    private Integer templateId;
    private String templateName;
    private String templateKey;
    private String category;
    private String description;
    private Integer steps;
}
