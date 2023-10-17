package com.github.mullerdaniil.miranda.ui.questionTraining.controller;

import com.github.mullerdaniil.miranda.module.questionTraining.entity.Question;
import com.github.mullerdaniil.miranda.module.questionTraining.entity.Tag;
import com.github.mullerdaniil.miranda.module.questionTraining.service.QuestionService;
import com.github.mullerdaniil.miranda.module.questionTraining.service.TagService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.HashSet;

import static javafx.collections.FXCollections.observableList;

@Deprecated
@RequiredArgsConstructor
@Controller
public class EditQuestionTabController {
    private final QuestionService questionService;
    private final TagService tagService;

    @FXML
    private TextArea questionTextArea;
    @FXML
    private TextArea answerTextArea;
    @FXML
    private ListView<String> tagNamesListView;
    @FXML
    private ListView<Question> questionsListView;

    public void initialize() {
        tagNamesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        questionsListView.getSelectionModel().selectedItemProperty()
                .addListener(new QuestionListViewChangeListener());
        questionsListView.setCellFactory(new QuestionListViewCellFactory());
        refreshQuestions();
    }


    public void editChosenQuestion() {
/*        var selectedQuestion = getSelectedQuestion();
        if (selectedQuestion != null) {
            questionService.update(
                    selectedQuestion.getId(),
                    questionTextArea.getText(),
                    answerTextArea.getText(),
                    tagNamesListView.getItems()
            );
        }*/
    }

    private void refreshQuestions() {
        var previouslySelectedQuestion = questionsListView.getSelectionModel().getSelectedItem();
        questionsListView.setItems(observableList(questionService.findAll()));
        questionsListView.getSelectionModel().select(previouslySelectedQuestion);
        loadSelectedQuestion();
    }

    private void loadSelectedQuestion() {
        var question = getSelectedQuestion();
        if (question != null) {
            questionTextArea.setText(question.getQuestion());
            answerTextArea.setText(question.getAnswer());

            var allTagNames = tagService.findAll()
                    .stream()
                    .map(Tag::getName)
                    .toList();

            var questionTagNames = new HashSet<>(tagService.findByQuestionId(question.getId()))
                    .stream()
                    .map(Tag::getName)
                    .toList();
            tagNamesListView.setItems(observableList(allTagNames));

            for (var tagName : questionTagNames) {
                tagNamesListView.getSelectionModel().select(tagName);
            }
        } else {
            questionTextArea.setText("");
            answerTextArea.setText("");
            tagNamesListView.getItems().clear();
        }
    }

    private Question getSelectedQuestion() {
        return questionsListView.getSelectionModel().getSelectedItem();
    }

    private class QuestionListViewChangeListener implements ChangeListener<Question> {
        @Override
        public void changed(ObservableValue<? extends Question> observable, Question oldValue, Question newValue) {
            loadSelectedQuestion();
        }
    }

    private static class QuestionListViewCellFactory implements Callback<ListView<Question>, ListCell<Question>> {
        @Override
        public ListCell<Question> call(ListView<Question> param) {
            return new ListCell<>() {
                @Override
                protected void updateItem(Question question, boolean empty) {
                    super.updateItem(question, empty);

                    if (empty || question == null) {
                        setText(null);
                        setGraphic(null);
                        setStyle(null);
                    } else {
                        setText(question.getQuestion());
                    }
                }
            };
        }
    }
}