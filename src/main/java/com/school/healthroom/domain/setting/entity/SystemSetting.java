package com.school.healthroom.domain.setting.entity;

import com.school.healthroom.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "system_setting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SystemSetting extends BaseTimeEntity {

    public static final Long DEFAULT_ID = 1L;

    @Id
    private Long id;

    @Column(nullable = false, length = 100)
    private String schoolName;

    @Column(nullable = false, length = 50)
    private String teacherName;

    @Column(nullable = false)
    @Builder.Default
    private Integer retentionYears = 5;

    @Column(nullable = false)
    @Builder.Default
    private Boolean kioskEnabled = true;

    public static SystemSetting createDefault() {
        return SystemSetting.builder()
                .id(DEFAULT_ID)
                .schoolName("OO고등학교")
                .teacherName("보건교사")
                .retentionYears(5)
                .kioskEnabled(true)
                .build();
    }

    public void update(String schoolName, String teacherName, Integer retentionYears, Boolean kioskEnabled) {
        this.schoolName = schoolName;
        this.teacherName = teacherName;
        this.retentionYears = retentionYears != null ? retentionYears : 5;
        this.kioskEnabled = kioskEnabled != null ? kioskEnabled : true;
    }

    public void resetToDefault() {
        this.schoolName = "OO고등학교";
        this.teacherName = "보건교사";
        this.retentionYears = 5;
        this.kioskEnabled = true;
    }
}
