package com.github.mullerdaniil.miranda.module.questionTraining.service;

import com.github.mullerdaniil.miranda.module.questionTraining.entity.Tag;
import com.github.mullerdaniil.miranda.module.questionTraining.exception.TagServiceException;
import com.github.mullerdaniil.miranda.module.questionTraining.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
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

    @Deprecated
    public void create(String name, Color color) {
        var tag = Tag.builder()
                .name(name)
//                .color(color)
                .build();

        tagRepository.save(tag);
    }

    public void create(String name,
                       Color textColor,
                       Color backgroundColor) {
        // TODO: 17.10.2023 check name uniqueness
        var tag = Tag.builder()
                .name(name)
                .textColor(textColor)
                .backgroundColor(backgroundColor)
                .build();

        tagRepository.save(tag);
    }

    public void update(Long id,
                       String name,
                       Color textColor,
                       Color backgroundColor) {
        var maybeTag = tagRepository.findById(id);

        if (maybeTag.isPresent()) {
            var tag = maybeTag.get();

            tag.setName(name);
            tag.setTextColor(textColor);
            tag.setBackgroundColor(backgroundColor);
        } else {
            // TODO: 17.10.2023 fix exception message everywhere! (add id)
            throw new TagServiceException("Tag with id = %d not found.".formatted(id));
        }
    }
}