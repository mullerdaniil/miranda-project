package com.github.mullerdaniil.miranda.module.questionTraining.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "question-training")
public record QuestionTrainingProperties(Points points) {
    public record Points(Integer up,
                         Integer down,
                         Integer max) {
    }
}