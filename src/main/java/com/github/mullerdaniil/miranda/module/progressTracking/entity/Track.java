package com.github.mullerdaniil.miranda.module.progressTracking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer totalProgress;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private ZonedDateTime lastUpdateTime;

    @CreationTimestamp
    private ZonedDateTime creationTime;
}