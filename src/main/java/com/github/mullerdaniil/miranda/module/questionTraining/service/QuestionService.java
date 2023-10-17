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

    public void update(Long questionId,
                       String question,
                       String answer,
                       List<Tag> tags) {

        // TODO: 17.10.2023 use orElseThrow(...) instead of maybe... everywhere!

        var maybeQuestion = questionRepository.findById(questionId);
        if (maybeQuestion.isPresent()) {
            var exisitingQuestion = maybeQuestion.get();
            exisitingQuestion.setQuestion(question);
            exisitingQuestion.setAnswer(answer);

            exisitingQuestion.getQuestionTags().clear();
            for (var tag : tags) {
                exisitingQuestion.addTag(tag);
            }
        } else {
            throw new QuestionServiceException("Question not found.");
        }
    }

    public void deleteById(Long id) {
        questionRepository.deleteById(id);
    }

/*    @Deprecated
    public void update(Long questionId,
                       String question,
                       String answer,
                       List<String> tagNames) {
        var maybeQuestion = questionRepository.findById(questionId);
        if (maybeQuestion.isPresent()) {
            var exisitingQuestion = maybeQuestion.get();
            exisitingQuestion.setQuestion(question);
            exisitingQuestion.setAnswer(answer);

            for (var tagName : tagNames) {
                var maybeTag = tagRepository.findByName(tagName);
                if (maybeTag.isPresent()) {
                    var tag = maybeTag.get();
                    exisitingQuestion.addTag(tag);
                } else {
                    throw new QuestionServiceException("Tag not found.");
                }
            }
        } else {
            throw new QuestionServiceException("Question not found.");
        }
    }*/

    public Long create(String question, String answer, List<Tag> tags) {
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

    @Deprecated
    public void create(String question, String answer) {
        var questionToSave = Question.builder()
                .question(question)
                .answer(answer)
                .build();

        questionRepository.save(questionToSave);
    }

    @Deprecated
    public void addTag(Long id, Tag tag) {
        var maybeQuestion = questionRepository.findById(id);
        if (maybeQuestion.isEmpty()) {
            throw new QuestionServiceException("Question not found.");
        }

        var question = maybeQuestion.get();
        question.addTag(tag);
//        questionRepository.save(question);
    }

    @Deprecated
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

    // TODO: 16.10.2023 method for question/answer not blank check
}