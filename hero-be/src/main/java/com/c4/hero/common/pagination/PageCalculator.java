package com.c4.hero.common.pagination;

/**
 * <pre>
 * Class Name: PageCalculator
 * Description: 페이지네이션 공통 계산 유틸 클래스
 *
 * - page / size 보정
 * - offset 계산
 * - totalPages 계산
 *
 * History
 * 2025/12/12 최초 작성
 * </pre>
 */
public class PageCalculator {

    /** 인스턴스 생성 방지 */
    private PageCalculator() {}

    /**
     * 페이지네이션 계산 수행
     *
     * @param page       요청 페이지 번호 (1부터 시작)
     * @param size       페이지당 데이터 개수
     * @param totalCount 전체 데이터 개수
     * @return 페이지 계산 결과(PageInfo)
     */
    public static PageInfo calculate(int page, int size, int totalCount) {

        // 1. page / size 최소값 보정
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);

        // 2. 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalCount / safeSize);

        // 3. page 범위 보정
        if (totalPages == 0) {
            safePage = 1;
        } else if (safePage > totalPages) {
            safePage = totalPages;
        }

        // 4. OFFSET 계산 (1-based → offset)
        int offset = (safePage - 1) * safeSize;

        return new PageInfo(
                safePage,
                safeSize,
                offset,
                totalPages
        );
    }
}
