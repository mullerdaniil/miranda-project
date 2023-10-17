package com.github.mullerdaniil.miranda.ui.questionTraining.controller;

import com.github.mullerdaniil.miranda.module.questionTraining.service.QuestionService;
import com.github.mullerdaniil.miranda.module.questionTraining.service.TagService;
import com.github.mullerdaniil.miranda.ui.questionTraining.event.TagsUpdatedEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import java.util.HashSet;

@Deprecated
@RequiredArgsConstructor
@Controller
public class NewQuestionTabController {
    private final QuestionService questionService;
    private final TagService tagService;

    @FXML
    public TextArea questionTextArea;
    @FXML
    public TextArea answerTextArea;
    @FXML
    public ListView<String> tagNamesListView;

    public void initialize() {
        tagNamesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        refreshTags();
    }

    public void createNewQuestion() {
        String questionText = questionTextArea.getText();
        String answerText = answerTextArea.getText();
        var selectedTagNames = new HashSet<>(tagNamesListView.getSelectionModel().getSelectedItems());

        questionService.create(questionText, answerText, selectedTagNames);
    }


    public void refreshTags() {
        var tags = tagService.findAll();
        tagNamesListView.getItems().clear();

        for (var tag : tags) {
            tagNamesListView.getItems().add(tag.getName());
        }
    }
}