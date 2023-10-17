package com.github.mullerdaniil.miranda.ui.questionTraining.controller;

import com.github.mullerdaniil.miranda.module.questionTraining.entity.Question;
import com.github.mullerdaniil.miranda.module.questionTraining.entity.Tag;
import com.github.mullerdaniil.miranda.module.questionTraining.exception.QuestionServiceException;
import com.github.mullerdaniil.miranda.module.questionTraining.service.QuestionService;
import com.github.mullerdaniil.miranda.module.questionTraining.service.TagService;
import com.github.mullerdaniil.miranda.ui.questionTraining.event.QuestionEditedEvent;
import com.github.mullerdaniil.miranda.ui.questionTraining.event.TagsUpdatedEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.github.mullerdaniil.miranda.ui.util.DialogUtil.showErrorDialog;
import static com.github.mullerdaniil.miranda.ui.util.DialogUtil.showExceptionDialog;
import static javafx.collections.FXCollections.observableList;

@RequiredArgsConstructor
@Component
public class QuestionsTabController {
    @FXML
    private TextArea questionTextArea;
    @FXML
    private TextArea answerTextArea;
    @FXML
    private ListView<Tag> tagsListView;
    @FXML
    private ListView<Question> questionsListView;

    private final QuestionService questionService;
    private final TagService tagService;
    private final ApplicationEventPublisher eventPublisher;

    public void initialize() {
        tagsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tagsListView.setCellFactory(new TagsListViewCellFactory());
        questionsListView.setCellFactory(new QuestionsListViewCellFactory());
        questionsListView.getSelectionModel().selectedItemProperty()
                .addListener(new QuestionsListViewSelectionChangeListener());
        questionsListView.setOnKeyPressed(new QuestionsListViewKeyListener());
        refreshQuestions();
        refreshTags();
    }

    public void createNewQuestion() {
        try {
            questionService.create(
                    questionTextArea.getText(),
                    answerTextArea.getText(),
                    getSelectedTags()
            );
            eventPublisher.publishEvent(new QuestionEditedEvent());
        } catch (QuestionServiceException e) {
            showExceptionDialog(e);
        }
    }

    public void editSelectedQuestion() {
        var selectedQuestion = getSelectedQuestion();
        if (selectedQuestion != null) {
            try {
                questionService.update(
                        selectedQuestion.getId(),
                        questionTextArea.getText(),
                        answerTextArea.getText(),
                        tagsListView.getSelectionModel().getSelectedItems()
                );
                eventPublisher.publishEvent(new QuestionEditedEvent());
            } catch (QuestionServiceException e) {
                showExceptionDialog(e);
            }
        } else {
            showErrorDialog("Question not chosen.");
        }
    }

    public void clearInput() {
        questionTextArea.setText("");
        answerTextArea.setText("");
        tagsListView.getSelectionModel().clearSelection();
    }


    @EventListener(QuestionEditedEvent.class)
    public void refreshQuestions() {
        var previouslySelectedQuestion = getSelectedQuestion();
        questionsListView.setItems(observableList(questionService.findAll()));
        questionsListView.getSelectionModel().select(previouslySelectedQuestion);
    }

    @EventListener(TagsUpdatedEvent.class)
    public void refreshTags() {
        var previouslySelectedTags = getSelectedTags();
        tagsListView.setItems(observableList(tagService.findAll()));
        for (var tag : previouslySelectedTags) {
            tagsListView.getSelectionModel().select(tag);
        }
    }

    private void loadQuestion(Question question) {
        questionTextArea.setText(question.getQuestion());
        answerTextArea.setText(question.getAnswer());

        tagsListView.getSelectionModel().clearSelection();
        for (var tag : tagService.findByQuestionId(question.getId())) {
            tagsListView.getSelectionModel().select(tag);
        }
    }

    private List<Tag> getSelectedTags() {
        return tagsListView.getSelectionModel().getSelectedItems();
    }

    private Question getSelectedQuestion() {
        return questionsListView.getSelectionModel().getSelectedItem();
    }

    private void deleteSelectedQuestion() {
        var selectedQuestion = getSelectedQuestion();
        if (selectedQuestion != null) {
            questionService.deleteById(selectedQuestion.getId());
            clearInput();
        }
    }

    private class QuestionsListViewKeyListener implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            switch (event.getCode()) {
                case DELETE -> deleteSelectedQuestion();
            }
        }
    }

    private class QuestionsListViewSelectionChangeListener implements ChangeListener<Question> {
        @Override
        public void changed(ObservableValue<? extends Question> observable, Question oldValue, Question newValue) {
            if (newValue != null) {
                loadQuestion(newValue);
            }
        }
    }

    private static class QuestionsListViewCellFactory implements Callback<ListView<Question>, ListCell<Question>> {
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

    private static class TagsListViewCellFactory implements Callback<ListView<Tag>, ListCell<Tag>> {
        @Override
        public ListCell<Tag> call(ListView<Tag> param) {
            return new ListCell<>() {
                @Override
                protected void updateItem(Tag tag, boolean empty) {
                    super.updateItem(tag, empty);

                    if (empty || tag == null) {
                        setText(null);
                        setGraphic(null);
                        setStyle(null);
                    } else {
                        setText(tag.getName());
                    }
                }
            };
        }
    }
}