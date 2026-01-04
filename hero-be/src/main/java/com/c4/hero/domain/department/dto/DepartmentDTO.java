package com.c4.hero.domain.department.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 * Class Name: DepartmentDTO
 * Description: 부서 선택/표시를 위한 부서 기본 정보를 담는 DTO
 *
 * History
 * 2025/12/24 (이지윤) 최초 작성 및 백엔드 코딩 컨벤션 적용
 * </pre>
 *
 * 부서 드롭다운, 필터, 목록 화면 등에서
 * 부서 ID와 부서명을 전달하기 위한 간단한 DTO입니다.
 *
 * @author 이지윤
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DepartmentDTO {

    /** 부서 PK (식별자) */
    private Integer departmentId;

    /** 부서 이름 */
    private String departmentName;
}
