package com.c4.hero.domain.attendance.mapper;

import com.c4.hero.domain.attendance.dto.ChangeLogDTO;
import com.c4.hero.domain.attendance.dto.CorrectionDTO;
import com.c4.hero.domain.attendance.dto.OvertimeDTO;
import com.c4.hero.domain.attendance.dto.PersonalDTO;
import com.c4.hero.domain.attendance.dto.AttSummaryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Interface Name: AttendanceMapper
 * Description: 근태(개인 근태, 초과 근무, 근태 정정 등) 관련 데이터를 조회하기 위한 MyBatis Mapper 인터페이스
 *
 * History
 * 2025/12/09 (이지윤) 최초 작성
 * 2025/12/30 (이지윤) 지연 출근 수정 로직에 관한 mapper 추가
 * 2025/12/30 (이지윤) 초과 근무 로직에 관한 mapper 추가
 * 2025/12/30 (이지윤) 근무제 수정 변경 로직에 관한 mapper 추가
 * 2026/01/02 (혜원) 알림 감지를 위한 mapper 추가
 * </pre>
 *
 * @author 이지윤
 * @version 1.2
 */
@Mapper
public interface AttendanceMapper {

    /**
     * 개인 근태 요약 정보를 조회합니다.
     *
     * <p>조회 대상</p>
     * <ul>
     *     <li>특정 직원({@code employeeId})의 근태 이력</li>
     *     <li>{@code startDate} ~ {@code endDate} 기간 동안의 근태 데이터를 기준으로 집계</li>
     * </ul>
     *
     * <p>반환 내용 예시</p>
     * <ul>
     *     <li>이번 달 근무일 수</li>
     *     <li>오늘 근무제 이름</li>
     *     <li>이번 달 지각/결근 횟수 등</li>
     * </ul>
     *
     * @param employeeId 조회 대상 직원 ID (필수)
     * @param startDate  조회 시작일(yyyy-MM-dd) 문자열
     * @param endDate    조회 종료일(yyyy-MM-dd) 문자열
     * @return 개인 근태 요약 정보를 담은 DTO
     */
    AttSummaryDTO selectPersonalSummary(
            @Param("employeeId") Integer employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate

    );

    /**
     * 개인 근태 기록 목록(페이지)을 조회합니다.
     *
     * @param employeeId 로그인한 사람의 정보 확인
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
            @Param("employeeId") Integer employeeId,
            @Param("offset") Integer offset,
            @Param("size") Integer size,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate

    );

    /**
     * 개인 근태 기록 총 개수를 조회합니다.
     *
     * @param employeeId 로그인한 사람의 정보 확인
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
            @Param("employeeId") Integer employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate

    );

    /**
     * 개인 근태 기록 단건 조회 (본인 소유 데이터만)
     *
     * @param employeeId 로그인한 사용자 ID
     * @param attendanceId 근태 기록 PK
     * @return 개인 근태 기록 1건 (없으면 null)
     */
    PersonalDTO selectPersonalById(
            @Param("employeeId") Integer employeeId,
            @Param("attendanceId") Integer attendanceId
    );

    int insertCorrectionRequest(
            @Param("employeeId") Integer employeeId,
            @Param("attendanceId") Integer attendanceId,
            @Param("targetDate") LocalDate targetDate,
            @Param("correctedStart") LocalTime correctedStart,
            @Param("correctedEnd") LocalTime correctedEnd,
            @Param("reason") String reason
    );



    /**
     * 초과 근무(연장 근무) 기록 목록(페이지)을 조회합니다.
     *
     * @param employeeId 로그인한 사람의 정보 확인
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
            @Param("employeeId") Integer employeeId,
            @Param("offset") Integer offset,
            @Param("size") Integer size,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate

    );

    /**
     * 초과 근무(연장 근무) 기록 총 개수를 조회합니다.
     *
     * @param employeeId 로그인한 사람의 정보 확인
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
            @Param("employeeId") Integer employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate

    );

    /**
     * 근태 정정(출퇴근 시간 수정) 요청 목록(페이지)을 조회합니다.
     *
     * @param employeeId 로그인한 사람의 정보 확인
     * @param offset    조회 시작 위치 (0부터 시작)
     * @param size      페이지당 데이터 개수
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 근태 정정 요청 리스트
     *
     * <p>
     * ※ 개인 근태/초과 근무와 동일하게, 근태 정정 목록/카운트 쿼리에서도
     *    {@code startDate}, {@code endDate} 필터를 동일하게 유지하기 위해
     *    메서드 간 파라미터 구조가 중복되는 패턴입니다.
     * </p>
     */
    List<CorrectionDTO> selectCorrectionPage(
            @Param("employeeId")  Integer employeeId,
            @Param("offset") Integer offset,
            @Param("size") Integer size,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate

    );

    /**
     * 근태 정정(출퇴근 시간 수정) 요청 총 개수를 조회합니다.
     *
     * @param employeeId 로그인한 사람의 정보 확인
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 근태 정정 요청 총 개수
     *
     * <p>
     * ※ 목록 조회(selectCorrectionPage)와 동일한 기간 필터를 적용하기 위해
     *    {@code startDate}, {@code endDate} 파라미터가 메서드 간 중복되어 있습니다.
     * </p>
     */
    int selectCorrectionCount(
            @Param("employeeId")  Integer employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate

    );

    /**
     * 근무제 정정 요청 목록(페이지)을 조회합니다.
     *
     * @param employeeId 로그인한 사람의 정보 확인
     * @param offset    조회 시작 위치 (0부터 시작)
     * @param size      페이지당 데이터 개수
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 근무제 정정 요청 리스트
     *
     * <p>
     * ※ 개인 근태/초과 근무와 동일하게, 근무제 정정 목록/카운트 쿼리에서도
     *    {@code startDate}, {@code endDate} 필터를 동일하게 유지하기 위해
     *    메서드 간 파라미터 구조가 중복되는 패턴입니다.
     * </p>
     */

    List<ChangeLogDTO> selectChangeLogPage(
            @Param("employeeId")  Integer employeeId,
            @Param("offset") Integer offset,
            @Param("size") Integer size,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate

    );

    /**
     * 근무제 정정 요청 총 개수를 조회합니다.
     *
     * @param employeeId 로그인한 사람의 정보 확인
     * @param startDate 조회 시작일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @param endDate   조회 종료일(yyyy-MM-dd), null인 경우 기간 필터 미적용
     * @return 근무제 정정 요청 총 개수
     *
     * <p>
     * ※ 목록 조회(selectCorrectionPage)와 동일한 기간 필터를 적용하기 위해
     *    {@code startDate}, {@code endDate} 파라미터가 메서드 간 중복되어 있습니다.
     * </p>
     */
    int selectChangeLogCount(
            @Param("employeeId") Integer employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    void insertOvertime(
            @Param("employeeId") Integer employeeId,
            @Param("workDate") LocalDate workDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("overtimeHours") float overtimeHours,
            @Param("reason") String reason
    );

    /**
     * 근무제 정정 요청 총 개수를 조회합니다.
     *
     * @param id 로그인한 사람의 정보 확인
     * <p>
     * ※ 목록 조회(selectCorrectionPage)와 동일한 기간 필터를 적용하기 위해
     *    {@code startDate}, {@code endDate} 파라미터가 메서드 간 중복되어 있습니다.
     * </p>
     */

    // (추가) work_system_template_id 또는 work_system_type_id 어떤 값이 와도 이름을 얻기 위한 조회
    String selectWorkSystemNameByAnyId(@Param("id") int id);

    // (추가) 근무제 변경 이력 INSERT
    int insertWorkSystemChangeLog(
            @Param("employeeId") int employeeId,
            @Param("date") LocalDate date,
            @Param("changeReason") String changeReason,
            @Param("templateName") String templateName,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("workSystemTemplateId") int workSystemTemplateId
    );


    // @author 헤원
    /**
     * [스케줄러] 특정 시점에 출근 기록이 없는 직원 조회 (미체크 알림 발송용)
     */
    List<Map<String, Object>> selectClockInMissingEmployees(@Param("workDate") LocalDate workDate);

    /**
     * [출근 시 즉시 호출] 특정 직원의 출근 예정 시간 및 현재 근태 ID 조회
     */
    Map<String, Object> selectCurrentWorkSystem(@Param("employeeId") Integer employeeId, @Param("workDate") LocalDate workDate);

    Integer selectBreakMinMinutes(@Param("attendanceId") int attendanceId);
}