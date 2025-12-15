package com.c4.hero.domain.settings.repository;

import com.c4.hero.domain.settings.entity.SettingsLoginPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsLoginPolicesRepository extends JpaRepository<SettingsLoginPolicy, Integer> {
}
