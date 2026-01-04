package com.c4.hero.common.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * <pre>
 * Class Name: PageResponse
 * Description: 페이징 처리된 응답 포맷
 *
 * - 페이지네이션 정보 포함
 * - 전체 데이터 개수, 페이지 수 등
 *
 * 사용 예시:
 * PageResponse.of(content, page, size, totalElements)
 *
 * History
 * 2025/11/28 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 * @param <T> 응답 데이터 타입
 */
@Getter
@Builder
public class PageResponse<T> {

    /** 데이터 목록 */
    private List<T> content;

    /** 현재 페이지 번호 (0부터 시작) */
    private int page;

    /** 페이지 크기 (한 페이지당 데이터 개수) */
    private int size;

    /** 전체 데이터 개수 */
    private long totalElements;

    /** 전체 페이지 수 */
    private int totalPages;

    /** 첫 페이지 여부 */
    private boolean first;

    /** 마지막 페이지 여부 */
    private boolean last;

    /**
     * 페이징 응답 생성
     *
     * @param content 데이터 목록
     * @param page 현재 페이지 (0부터 시작)
     * @param size 페이지 크기
     * @param totalElements 전체 데이터 개수
     * @param <T> 응답 데이터 타입
     * @return 페이징 응답
     *
     * 예시: PageResponse.of(employees, 0, 10, 50)
     * 결과: {
     *   "content": [...],
     *   "page": 0,
     *   "size": 10,
     *   "totalElements": 50,
     *   "totalPages": 5,
     *   "first": true,
     *   "last": false
     * }
     */
    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        // 전체 페이지 수 계산 (올림)
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<T>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)                    // 첫 페이지 여부
                .last(page >= totalPages - 1)        // 마지막 페이지 여부
                .build();
    }
}