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

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public void update(Long id,
                       String question,
                       String answer,
                       List<Tag> tags) {
        var existingQuestion = questionRepository.findById(id).orElseThrow(
                () -> new QuestionServiceException("Question with id = %d not found.".formatted(id))
        );

        checkQuestionAndAnswerAreNotBlank(question, answer);

        existingQuestion.setQuestion(question);
        existingQuestion.setAnswer(answer);
        existingQuestion.getQuestionTags().clear();
        for (var tag : tags) {
            existingQuestion.addTag(tag);
        }
    }

    public void deleteById(Long id) {
        questionRepository.deleteById(id);
    }


    public Long create(String question, String answer, List<Tag> tags) {
        checkQuestionAndAnswerAreNotBlank(question, answer);

        var newQuestion = Question.builder()
                .question(question)
                .answer(answer)
                .build();

        for (var tag : tags) {
            newQuestion.addTag(tag);
        }

        var createdQuestion = questionRepository.save(newQuestion);
        return createdQuestion.getId();
    }

    @Deprecated
    public void create(String question, String answer, Set<String> tagNames) {
        checkQuestionAndAnswerAreNotBlank(question, answer);

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

    public void setQuestionAnswered(Long id, boolean answered) {
        var question = questionRepository.findById(id).orElseThrow(
                () -> new QuestionServiceException("Question with id = %d not found.".formatted(id))
        );

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

    private static void checkQuestionAndAnswerAreNotBlank(String question, String answer) {
        if (question.isBlank()) {
            throw new QuestionServiceException("Question text must not be blank.");
        }

        if (answer.isBlank()) {
            throw new QuestionServiceException("Answer text must not be blank.");
        }
    }
}