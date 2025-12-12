package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * <pre>
 * Class Name: PersonalPageResponseDTO
 * Description: 개인 근태 기록 목록(페이지네이션 포함) 응답 DTO
 *
 * History
 * 2025/12/10 (이지윤) 최초 작성
 * </pre>
 *
 * @author 이지윤
 * @version 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageResponseDTO<T> {

    /** 출석 및 휴가 부분에 관한 DTO */
    private List<T> items;

    /** 현재 페이지 번호 (1부터 시작) */
    private int page;

    /** 한 페이지당 데이터 개수 */
    private int size;

    /** 전체 데이터 개수 */
    private int totalCount;

    /** 전체 페이지 수 */
    private int totalPages;
}
