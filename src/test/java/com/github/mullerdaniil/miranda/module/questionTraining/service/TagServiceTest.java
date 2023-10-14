package com.github.mullerdaniil.miranda.module.questionTraining.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class TagServiceTest {
    @Autowired
    private TagService tagService;

    @Test
    void findByQuestionId() {
    }

    @Test
    void createTags() {
        var name = "orange";
        var color = new Color(255, 128, 0);
        tagService.create(name, color);


        var name2 = "blue";
        var color2 = new Color(0, 128, 240);
        tagService.create(name2, color2);
    }
}