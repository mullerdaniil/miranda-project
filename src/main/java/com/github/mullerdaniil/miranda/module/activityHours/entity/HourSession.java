package com.github.mullerdaniil.miranda.module.activityHours.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class HourSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String activityName;

    @Column(nullable = false)
    private LocalDate date;

    @CreationTimestamp
    private ZonedDateTime creationTime;

    private boolean completed = false;

    public void reverseCompleted() {
        completed = !completed;
    }
}