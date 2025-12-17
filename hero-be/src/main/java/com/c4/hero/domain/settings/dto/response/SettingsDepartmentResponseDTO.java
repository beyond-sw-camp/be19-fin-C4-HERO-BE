package com.c4.hero.domain.settings.dto.response;

import com.c4.hero.domain.employee.entity.Employee;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * <pre>
 * Class Name: SettingsDepartmentResponseDTO
 * Description: 부서 정보 응답 DTO
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
public class SettingsDepartmentResponseDTO {
	private Integer departmentId;
	private String departmentName;
	private String departmentPhone;
	private Integer depth;
	private Integer parentDepartmentId;
	private SettingsDepartmentManagerDTO manager;
	private List<SettingsDepartmentResponseDTO> children;
}
