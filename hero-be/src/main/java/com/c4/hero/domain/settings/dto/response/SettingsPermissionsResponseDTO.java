package com.c4.hero.domain.settings.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * Class Name: SettingsPermissionsResponseDTO
 * Description: 권한 정보 응답 DTO
 *
 * History
 * 2025/12/16 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsPermissionsResponseDTO {
	private Integer employeeId;
	private String employeeName;
	private String employeeNumber;
	private String department;
	private String grade;
	private String jobTitle;
	private List<String> role;

}
