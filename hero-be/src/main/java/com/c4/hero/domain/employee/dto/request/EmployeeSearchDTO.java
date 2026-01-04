package com.c4.hero.domain.employee.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * Class Name: EmployeeSearchDTO
 * Description: 직원 검색 및 페이징 조건을 담는 DTO
 *
 * History
 * 2025/12/11 승건 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSearchDTO {
    
    // 검색 조건
    private String departmentName; // 부서명
    private String jobTitleName;   // 직책명
    private String gradeName;      // 직급명
    private String employeeName;   // 직원 이름
    private Integer resigningExpected; // 퇴사 예정자 여부
                                    // 0: 포함
                                    // 1: 미포함
                                    // 2: 퇴사 예정자만

    // 페이징 조건
    @Builder.Default
    private int page = 1;          // 현재 페이지 (기본값 1)
    
    @Builder.Default
    private int size = 10;         // 페이지 당 개수 (기본값 10)

    // 페이징 계산용 (MyBatis에서 사용)
    public int getOffset() {
        return (page - 1) * size;
    }
}
