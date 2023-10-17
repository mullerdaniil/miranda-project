package com.github.mullerdaniil.miranda.module.questionTraining.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.awt.*;
import java.time.ZonedDateTime;

@ToString(of = "name")
@EqualsAndHashCode(of = "id")
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
    private Color textColor;

    @Column(nullable = false)
    private Color backgroundColor;

/*    @Builder.Default
    @OneToMany(
            mappedBy = "tag",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<QuestionTag> questionTags = new ArrayList<>();*/

    @CreationTimestamp
    private ZonedDateTime creationTime;
}