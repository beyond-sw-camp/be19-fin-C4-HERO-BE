package com.c4.hero.domain.settings.repository;

import com.c4.hero.domain.attendance.entity.WorkSystemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingAttTypeRepository extends JpaRepository<WorkSystemType, Integer> {
}
