package com.c4.hero.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * <pre>
 * Class Name: DateUtil
 * Description: 날짜/시간 처리 유틸리티
 *
 * - 날짜 포맷 변환
 * - 근속연수 계산
 * - 월 시작일/종료일 계산
 * - 날짜 차이 계산
 *
 * History
 * 2025/11/28 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
public class DateUtil {

    /** 날짜 포맷: yyyy-MM-dd */
    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** 월 포맷: yyyy-MM */
    public static final DateTimeFormatter YYYY_MM = DateTimeFormatter.ofPattern("yyyy-MM");

    /** 날짜시간 포맷: yyyy-MM-dd HH:mm:ss */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 현재 년월을 YYYY-MM 형식으로 반환
     * 급여 처리 등에서 사용
     *
     * @return 현재 년월 (예: "2024-11")
     */
    public static String getCurrentYearMonth() {
        return YearMonth.now().format(YYYY_MM);
    }

    /**
     * 근속연수 계산
     * 입사일부터 종료일까지의 연수를 소수점 둘째자리까지 반환
     *
     * @param hireDate 입사일
     * @param endDate 종료일 (퇴사일 또는 현재일)
     * @return 근속연수 (예: 3.25년)
     */
    public static double calculateYearsOfService(LocalDate hireDate, LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(hireDate, endDate);
        // 1년 = 365.25일 (윤년 고려)
        return Math.round(days / 365.25 * 100.0) / 100.0;
    }

    /**
     * 해당 월의 첫날 반환
     * 급여 처리 기간 계산 등에서 사용
     *
     * @param yearMonth 년월 (예: "2024-11")
     * @return 해당 월의 첫날 (예: 2024-11-01)
     */
    public static LocalDate getFirstDayOfMonth(String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth, YYYY_MM);
        return ym.atDay(1);
    }

    /**
     * 해당 월의 마지막 날 반환
     * 급여 처리 기간 계산 등에서 사용
     *
     * @param yearMonth 년월 (예: "2024-11")
     * @return 해당 월의 마지막 날 (예: 2024-11-30)
     */
    public static LocalDate getLastDayOfMonth(String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth, YYYY_MM);
        return ym.atEndOfMonth();
    }

    /**
     * 두 날짜 사이의 일수 계산
     * 휴가 일수, 근무 일수 계산 등에서 사용
     *
     * @param start 시작일
     * @param end 종료일
     * @return 일수 차이
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 문자열을 LocalDate로 변환
     * yyyy-MM-dd 형식의 문자열만 지원
     *
     * @param dateStr 날짜 문자열 (예: "2024-11-28")
     * @return LocalDate 객체
     */
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, YYYY_MM_DD);
    }

    /**
     * 문자열을 LocalDateTime으로 변환
     * yyyy-MM-dd HH:mm:ss 형식의 문자열만 지원
     *
     * @param dateTimeStr 날짜시간 문자열 (예: "2024-11-28 10:30:00")
     * @return LocalDateTime 객체
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, YYYY_MM_DD_HH_MM_SS);
    }
}