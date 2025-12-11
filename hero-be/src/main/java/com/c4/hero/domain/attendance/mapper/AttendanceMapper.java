package com.c4.hero.domain.attendance.mapper;

import com.c4.hero.domain.attendance.dto.OvertimeDTO;
import com.c4.hero.domain.attendance.dto.PersonalDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <pre>
 * Interface Name: AttendanceMapper
 * Description: 근태(개인 근태, 초과 근무 등) 관련 데이터를 조회하기 위한 MyBatis Mapper 인터페이스
 *
 * History
 * 2025/12/09 (이지윤) 최초 작성
 * 2025/12/10 (이지윤) 초과 근무 조회 메서드 추가 및 컨벤션 정리
 * </pre>
 *
 * @author 이지윤
 * @version 2.0
 */
@Mapper
public interface AttendanceMapper {

    /**
     * 개인 근태 기록 목록(페이지)을 조회합니다.
     *
     * @param offset    조회 시작 위치 (0부터 시작)
     * @param size      조회할 데이터 개수
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 개인 근태 기록 리스트
     *
     * <p>
     * ※ {@code startDate}, {@code endDate} 파라미터는
     *    목록 조회(selectPersonalPage)와 총 개수 조회(selectPersonalCount)에서
     *    동일한 필터 조건을 유지하기 위해 의도적으로 중복 정의된 것입니다.
     * </p>
     */
    List<PersonalDTO> selectPersonalPage(
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 개인 근태 기록 총 개수를 조회합니다.
     *
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 개인 근태 기록 총 개수
     *
     * <p>
     * ※ {@code startDate}, {@code endDate} 파라미터는
     *    목록 조회(selectPersonalPage)와 동일한 필터 조건을 적용하기 위해
     *    이 메서드에서도 동일하게(중복으로) 선언되어 있습니다.
     * </p>
     */
    int selectPersonalCount(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 초과 근무(연장 근무) 기록 목록(페이지)을 조회합니다.
     *
     * @param offset    조회 시작 위치 (0부터 시작)
     * @param size      조회할 데이터 개수
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 초과 근무 기록 리스트
     *
     * <p>
     * ※ 개인 근태와 동일하게, 초과 근무 목록/카운트 쿼리에서도
     *    {@code startDate}, {@code endDate} 필터를 동일하게 적용하기 위해
     *    메서드 간 파라미터 구조가 중복되는 패턴입니다.
     * </p>
     */
    List<OvertimeDTO> selectOvertimePage(
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 초과 근무(연장 근무) 기록 총 개수를 조회합니다.
     *
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 초과 근무 기록 총 개수
     *
     * <p>
     * ※ 목록 조회(selectOvertimePage)와 동일한 기간 필터를 적용하기 위해
     *    {@code startDate}, {@code endDate} 파라미터가 메서드 간 중복되어 있습니다.
     * </p>
     */
    int selectOvertimeCount(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );
}
