package com.c4.hero.domain.settings.repository;

import com.c4.hero.domain.settings.entity.SettingsDepartment;
import com.c4.hero.domain.settings.entity.SettingsEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SettingsDepartmentRepository extends JpaRepository<SettingsDepartment, Integer> {

    @Query("SELECT MAX(d.departmentId) FROM SettingsDepartment d")
    Integer findMaxDepartmentId();

    List<SettingsDepartment> findAllByDepartmentIdNotIn(List<Integer> departmentIds);
}
