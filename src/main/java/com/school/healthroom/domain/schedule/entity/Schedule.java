package com.school.healthroom.domain.schedule.entity;

import com.school.healthroom.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Schedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    @Column(length = 50)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String content;

    public void update(String title, LocalDate startDate, LocalDate endDate,
                       LocalTime startTime, LocalTime endTime, String category, String content) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.category = category;
        this.content = content;
    }
}
