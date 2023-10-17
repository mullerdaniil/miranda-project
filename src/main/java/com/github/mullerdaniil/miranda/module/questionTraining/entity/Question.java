package com.github.mullerdaniil.miranda.module.questionTraining.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString(of = {"question", "answer", "points"})
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String answer;

    @ColumnDefault("0")
    private int points;

    @Builder.Default
    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<QuestionTag> questionTags = new ArrayList<>();

    @CreationTimestamp
    private ZonedDateTime creationTime;

    public void addTag(Tag tag) {
        var questionTag = buildQuestionTag(this, tag);
        questionTags.add(questionTag);
//        tag.getQuestionTags().add(questionTag);
    }

    public void removeTag(Tag tag) {
        var questionTag = buildQuestionTag(this, tag);
        questionTags.remove(questionTag);
//        tag.getQuestionTags().remove(questionTag);
        questionTag.setQuestion(null);
        questionTag.setTag(null);
    }

    private QuestionTag buildQuestionTag(Question question, Tag tag) {
        return QuestionTag.builder()
                .question(question)
                .tag(tag)
                .build();
    }
}