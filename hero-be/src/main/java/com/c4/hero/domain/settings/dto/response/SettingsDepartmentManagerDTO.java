package com.c4.hero.domain.settings.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <pre>
 * Class Name: SettingsDepartmentManagerDTO
 * Description: 부서 관리자 정보 응답 DTO
 *
 * History
 * 2025/12/16 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Getter
@Setter
@Builder
public class SettingsDepartmentManagerDTO {
	private Integer employeeId;
	private String employeeNumber;
	private String employeeName;
	private String jobTitle;
	private String grade;
}
