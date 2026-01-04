package com.c4.hero.domain.settings.dto.request;

import lombok.Data;
import java.util.List;

/**
 * <pre>
 * Class Name: SettingsDepartmentRequestDTO
 * Description: 부서 설정 요청 DTO
 *
 * History
 * 2025/12/16 승건 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Data
public class SettingsDepartmentRequestDTO {
    private Integer departmentId;
    private String departmentName;
    private String departmentPhone;
    private Integer depth;
    private Integer parentDepartmentId;
    private Integer managerId;
    private List<SettingsDepartmentRequestDTO> children;
}
