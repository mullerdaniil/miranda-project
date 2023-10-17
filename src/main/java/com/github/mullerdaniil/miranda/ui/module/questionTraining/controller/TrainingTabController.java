package com.github.mullerdaniil.miranda.ui.module.questionTraining.controller;

import com.github.mullerdaniil.miranda.module.questionTraining.config.QuestionTrainingProperties;
import com.github.mullerdaniil.miranda.module.questionTraining.entity.Question;
import com.github.mullerdaniil.miranda.module.questionTraining.service.QuestionService;
import com.github.mullerdaniil.miranda.module.questionTraining.service.TagService;
import com.github.mullerdaniil.miranda.ui.module.questionTraining.event.QuestionsUpdatedEvent;
import com.github.mullerdaniil.miranda.ui.module.questionTraining.event.TagsUpdatedEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static com.github.mullerdaniil.miranda.ui.util.ColorUtil.toHexString;

@RequiredArgsConstructor
@Component
public class TrainingTabController {
    private final QuestionService questionService;
    private final TagService tagService;
    private final QuestionTrainingProperties questionTrainingProperties;

    @FXML
    private FlowPane currentQuestionTagsFlowPane;
    @FXML
    private Text questionText;
    @FXML
    private Text answerText;
    @FXML
    private Label currentQuestionPointsLabel;
    @FXML
    private VBox answerVBox;
    @FXML
    private VBox trainingTabVBox;
    @FXML
    private MenuButton availableTagsMenuButton;
    @FXML
    private Spinner<Integer> maxPointsFilterSpinner;

    private final TagCheckBoxListener tagCheckBoxListener = new TagCheckBoxListener();
    private Question currentQuestion = null;

    // TODO: 17.10.2023 use set of tags instead
    private Set<String> chosenTagNames = new HashSet<>();

    public void initialize() {
        maxPointsFilterSpinner.getValueFactory().setValue(questionTrainingProperties.points().max());
        for (var tag : tagService.findAll()) {
            chosenTagNames.add(tag.getName());
        }
        setRandomQuestion();
        refreshAvailableTags();
        trainingTabVBox.setOnKeyPressed(new TrainingTabVBoxKeyListener());
    }

    public void setQuestionAnswered() {
        if (currentQuestion != null) {
            questionService.setQuestionAnswered(currentQuestion.getId(), true);
        }
        setRandomQuestion();
    }

    public void setQuestionNotAnswered() {
        if (currentQuestion != null) {
            questionService.setQuestionAnswered(currentQuestion.getId(), false);
        }
        setRandomQuestion();
    }

    public void showAnswer() {
        if (currentQuestion != null) {
            answerText.setText(currentQuestion.getAnswer());
        } else {
            answerText.setText("");
        }
        answerVBox.setStyle(null);
    }

    @EventListener({
            QuestionsUpdatedEvent.class,
            TagsUpdatedEvent.class
    })
    public void resetQuestion() {
        currentQuestion = null;
        loadCurrentQuestion();
    }

    private void setRandomQuestion() {
        currentQuestion = questionService
                .getRandomByMaxPointsAndTagNames(maxPointsFilterSpinner.getValue(), chosenTagNames)
                .orElse(null);

        loadCurrentQuestion();
    }

    private void loadCurrentQuestion() {
        if (currentQuestion != null) {
            questionText.setText(currentQuestion.getQuestion());
            currentQuestionPointsLabel.setText(String.valueOf(currentQuestion.getPoints()));
            hideAnswer();
        } else {
            questionText.setText("");
            currentQuestionPointsLabel.setText("");
            showAnswer();
        }

        answerText.setText("");
        loadCurrentQuestionTags();
    }

    private void loadCurrentQuestionTags() {
        var questionTagsList = currentQuestionTagsFlowPane.getChildren();
        questionTagsList.clear();

        if (currentQuestion != null) {
            var tags = tagService.findByQuestionId(currentQuestion.getId());

            for (var tag : tags) {
                var tagLabel = new Label(tag.getName());
                tagLabel.setPadding(new Insets(0, 5, 0, 5));
                tagLabel.setStyle("-fx-border-radius: 15; -fx-background-color: %s; -fx-text-fill: %s"
                        .formatted(
                                toHexString(tag.getBackgroundColor()),
                                toHexString(tag.getTextColor())
                        ));
                questionTagsList.add(tagLabel);
            }
        }
    }

    private void hideAnswer() {
        answerText.setText("");
        answerVBox.setStyle("-fx-background-color: -color-warning-muted;");
    }

    @EventListener(TagsUpdatedEvent.class)
    public void refreshAvailableTags() {
        var tags = tagService.findAll();
        Set<String> newChosenTagNames = new HashSet<>();
        availableTagsMenuButton.getItems().clear();

        for (var tag : tags) {
            var checkBox = new CheckBox(tag.getName());
            checkBox.setOnAction(tagCheckBoxListener);
            var customMenuItem = new CustomMenuItem(checkBox);
            availableTagsMenuButton.getItems().add(customMenuItem);

            if (chosenTagNames.contains(tag.getName())) {
                checkBox.setSelected(true);
                newChosenTagNames.add(tag.getName());
            }
        }

        chosenTagNames = newChosenTagNames;
    }

    private class TrainingTabVBoxKeyListener implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            switch (event.getCode()) {
                case A -> setQuestionAnswered();
                case S -> showAnswer();
                case D -> setQuestionNotAnswered();
            }
        }
    }

    private class TagCheckBoxListener implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            CheckBox tagCheckBox = (CheckBox) event.getSource();
            var tagName = tagCheckBox.getText();

            if (tagCheckBox.isSelected()) {
                chosenTagNames.add(tagName);
            } else {
                chosenTagNames.remove(tagName);
            }
        }
    }
}