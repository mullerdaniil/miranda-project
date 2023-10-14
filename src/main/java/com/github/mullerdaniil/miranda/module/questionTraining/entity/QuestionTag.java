package com.github.mullerdaniil.miranda.module.questionTraining.entity;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(of = {"question", "tag"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "question_tags")
@Entity
public class QuestionTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Question question;

    @ManyToOne
    private Tag tag;
}