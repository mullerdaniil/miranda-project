package com.github.mullerdaniil.miranda.module.questionTraining.service;

import com.github.mullerdaniil.miranda.module.questionTraining.config.QuestionTrainingProperties;
import com.github.mullerdaniil.miranda.module.questionTraining.entity.Question;
import com.github.mullerdaniil.miranda.module.questionTraining.entity.Tag;
import com.github.mullerdaniil.miranda.module.questionTraining.exception.QuestionServiceException;
import com.github.mullerdaniil.miranda.module.questionTraining.repository.QuestionRepository;
import com.github.mullerdaniil.miranda.module.questionTraining.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Transactional
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final TagRepository tagRepository;
    private final QuestionTrainingProperties questionTrainingProperties;

    public Optional<Question> getRandomByMaxPointsAndTagNames(Integer maxPoints, Set<String> tagNames) {
        return questionRepository.getRandomByMaxPointsAndTagNames(maxPoints, tagNames);
    }

    public void save(String question, String answer, Set<String> tagNames) {
        var newQuestion = Question.builder()
                .question(question)
                .answer(answer)
                .build();

        var selectedTags = tagRepository.findAll()
                .stream()
                .filter(tag -> tagNames.contains(tag.getName()))
                .toList();

        for (var tag : selectedTags) {
            newQuestion.addTag(tag);
        }

        questionRepository.save(newQuestion);
    }

    public void save(String question, String answer) {
        var questionToSave = Question.builder()
                .question(question)
                .answer(answer)
                .build();

        questionRepository.save(questionToSave);
    }

    public void addTag(Long id, Tag tag) {
        var maybeQuestion = questionRepository.findById(id);
        if (maybeQuestion.isEmpty()) {
            throw new QuestionServiceException("Question not found.");
        }

        var question = maybeQuestion.get();
        question.addTag(tag);
//        questionRepository.save(question);
    }

    public void removeTag(Long id, Tag tag) {
        var maybeQuestion = questionRepository.findById(id);
        if (maybeQuestion.isEmpty()) {
            throw new QuestionServiceException("Question not found.");
        }

        var question = maybeQuestion.get();
        question.removeTag(tag);
//        questionRepository.save(question);
    }

    public void setQuestionAnswered(Long id, boolean answered) {
        var maybeQuestion = questionRepository.findById(id);
        if (maybeQuestion.isEmpty()) {
            throw new QuestionServiceException("Question not found.");
        }

        var question = maybeQuestion.get();
        int oldPointsValue = question.getPoints();
        int newPointsValue;

        if (answered) {
            newPointsValue = oldPointsValue + questionTrainingProperties.points().up();
        } else {
            newPointsValue = oldPointsValue - questionTrainingProperties.points().down();
        }

        if (newPointsValue < 0) {
            newPointsValue = 0;
        }

        if (newPointsValue > questionTrainingProperties.points().max()) {
            newPointsValue = questionTrainingProperties.points().max();
        }

        question.setPoints(newPointsValue);
    }
}