package com.github.mullerdaniil.miranda.module.questionTraining.service;

import com.github.mullerdaniil.miranda.module.questionTraining.entity.Tag;
import com.github.mullerdaniil.miranda.module.questionTraining.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TagService {
    private final TagRepository tagRepository;

    public Optional<Tag> findByName(String name) {
        return tagRepository.findByName(name);
    }

    public List<Tag> findByQuestionId(Long questionId) {
        return tagRepository.findTagsByQuestionId(questionId);
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public void create(String name, Color color) {
        var tag = Tag.builder()
                .name(name)
                .color(color)
                .build();

        tagRepository.save(tag);
    }
}