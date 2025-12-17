package com.c4.hero.common.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <pre>
 * Class Name: PageInfo
 * Description: 페이지네이션 계산 결과를 담는 내부 전용 DTO
 *
 * - Service 계층 내부에서만 사용
 * - DB 조회(offset/limit) 및 응답 조립에 활용
 *
 * History
 * 2025/12/12 최초 작성
 * </pre>
 */
@Getter
@AllArgsConstructor
public class PageInfo {

    /** 현재 페이지 번호 (1부터 시작, 보정된 값) */
    private int page;

    /** 페이지 크기 */
    private int size;

    /** DB 조회용 OFFSET */
    private int offset;

    /** 전체 페이지 수 */
    private int totalPages;
}
