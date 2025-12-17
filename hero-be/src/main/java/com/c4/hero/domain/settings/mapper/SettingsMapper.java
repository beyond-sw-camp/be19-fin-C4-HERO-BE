package com.c4.hero.domain.settings.mapper;

import com.c4.hero.domain.settings.dto.response.SettingsPermissionsResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Class Name: SettingsMapper
 * Description: 환경 설정 관련 MyBatis Mapper
 *
 * History
 * 2025/12/16 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Mapper
public interface SettingsMapper {

	/**
	 * 로그인 정책 조회
	 *
	 * @return 로그인 정책 값
	 */
	int selectPolicy();

	/**
	 * 사원 권한 목록 조회
	 *
	 * @param params   검색 조건
	 * @param pageable 페이징 정보
	 * @return 사원 권한 목록
	 */
	List<SettingsPermissionsResponseDTO> findEmployeePermissions(@Param("params") Map<String, Object> params, @Param("pageable") Pageable pageable);

	/**
	 * 사원 권한 목록 개수 조회
	 *
	 * @param params 검색 조건
	 * @return 사원 권한 목록 개수
	 */
	int countEmployeePermissions(@Param("params") Map<String, Object> params);
}
