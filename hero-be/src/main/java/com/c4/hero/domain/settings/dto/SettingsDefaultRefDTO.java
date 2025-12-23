package com.c4.hero.domain.settings.dto;

import lombok.*;
/**
 * <pre>
 * Class Name: SettingsDefaultRefDTO
 * Description: 기본 참조(목록) DTO
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
@AllArgsConstructor
@NoArgsConstructor
public class SettingsDefaultRefDTO {

    private String targetType;

    private Integer departmentId;
}
