package com.c4.hero.domain.settings.mapper;

import com.c4.hero.domain.settings.dto.response.SettingsPermissionsResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

@Mapper
public interface SettingsMapper {
    int selectPolicy();

    List<SettingsPermissionsResponseDTO> findEmployeePermissions(@Param("params") Map<String, Object> params, @Param("pageable") Pageable pageable);

    int countEmployeePermissions(@Param("params") Map<String, Object> params);
}
