package com.github.mullerdaniil.miranda.module.questionTraining.service;

import com.github.mullerdaniil.miranda.module.questionTraining.entity.Question;
import com.github.mullerdaniil.miranda.module.questionTraining.entity.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class QuestionServiceTest {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private TagService tagService;

    @Test
    void getRandom() {
        var random = questionService.getRandom();
        System.out.println(random.getQuestion() + " : " + random.getAnswer());
    }

    @Test
    void save() {
        questionService.save("How to do it?", "Just do it!");
        questionService.save("How to do it? #2", "Just do it! #2");
        questionService.save("How to do it? #3", "Just do it! #3");
        questionService.save("How to do it? #4", "Just do it! #4");
    }

    @Test
    void addTag() {
        var orangeTag = tagService.findByName("orange").get();
        var blueTag = tagService.findByName("blue").get();

        questionService.addTag(1L, orangeTag);
        questionService.addTag(2L, blueTag);
        questionService.addTag(3L, orangeTag);
        questionService.addTag(4L, blueTag);
    }

    @Test
    void removeTag() {
    }
}