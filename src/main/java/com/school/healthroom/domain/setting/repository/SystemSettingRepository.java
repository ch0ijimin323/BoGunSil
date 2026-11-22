package com.school.healthroom.domain.setting.repository;

import com.school.healthroom.domain.setting.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
}
