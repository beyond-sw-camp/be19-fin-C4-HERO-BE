package com.c4.hero.domain.settings.repository;

import com.c4.hero.domain.attendance.entity.WorkSystemTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingAttTemplateRepository extends JpaRepository<WorkSystemTemplate, Integer> {
}
