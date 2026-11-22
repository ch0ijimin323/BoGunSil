package com.school.healthroom.domain.commoncode.entity;

import com.school.healthroom.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "common_code",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_common_code_group_code", columnNames = {"groupCode", "code"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommonCode extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String groupCode;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String codeName;

    @Column(length = 50)
    private String parentCode;

    @Column(nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean useYn = true;

    @Column(length = 255)
    private String description;

    public void update(String codeName, String parentCode, Integer sortOrder, Boolean useYn, String description) {
        this.codeName = codeName;
        this.parentCode = parentCode;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.useYn = useYn != null ? useYn : true;
        this.description = description;
    }
}
