package com.github.mullerdaniil.miranda.module.questionTraining.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString(of = {"name", "color"})
@EqualsAndHashCode(of = "name")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Color color;

    @Builder.Default
    @OneToMany(
            mappedBy = "tag",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<QuestionTag> questionTags = new ArrayList<>();

    @CreationTimestamp
    private ZonedDateTime creationTime;
}