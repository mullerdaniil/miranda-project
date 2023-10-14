package com.github.mullerdaniil.miranda.ui.controller;

import com.github.mullerdaniil.miranda.module.questionTraining.config.QuestionTrainingProperties;
import com.github.mullerdaniil.miranda.module.questionTraining.entity.Question;
import com.github.mullerdaniil.miranda.module.questionTraining.entity.Tag;
import com.github.mullerdaniil.miranda.module.questionTraining.service.QuestionService;
import com.github.mullerdaniil.miranda.module.questionTraining.service.TagService;
import javafx.collections.ObservableList;
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
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.mullerdaniil.miranda.ui.util.ColorUtil.toHexString;

@RequiredArgsConstructor
@Component
public class QuestionTrainingController {
    private final QuestionService questionService;
    private final TagService tagService;
    private final QuestionTrainingProperties questionTrainingProperties;

    @FXML
    private TextArea createQuestionTextArea;
    @FXML
    private TextArea createAnswerTextArea;
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
    @FXML
    private ListView<String> createQuestionTagNamesListView;

    private final TagCheckBoxListener tagCheckBoxListener = new TagCheckBoxListener();
    private Question currentQuestion = null;
    private Set<String> chosenTagNames = new HashSet<>();

    public void initialize() {
        maxPointsFilterSpinner.getValueFactory().setValue(questionTrainingProperties.points().max());
        createQuestionTagNamesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        for (var tag : tagService.findAll()) {
            chosenTagNames.add(tag.getName());
        }
        loadRandomQuestion();
        refreshAvailableTags();
        trainingTabVBox.setOnKeyPressed(new TrainingTabVBoxKeyListener());
    }

    public void setQuestionAnswered() {
        if (currentQuestion != null) {
            questionService.setQuestionAnswered(currentQuestion.getId(), true);
        }
        loadRandomQuestion();
    }

    public void setQuestionNotAnswered() {
        if (currentQuestion != null) {
            questionService.setQuestionAnswered(currentQuestion.getId(), false);
        }
        loadRandomQuestion();
    }

    public void showAnswer() {
        if (currentQuestion != null) {
            answerText.setText(currentQuestion.getAnswer());
        } else {
            answerText.setText("");
        }
        answerVBox.setStyle(null);
    }

    private void loadRandomQuestion() {
        var maybeCurrentQuestion = questionService.getRandomByMaxPointsAndTagNames(maxPointsFilterSpinner.getValue(), chosenTagNames);
        if (maybeCurrentQuestion.isPresent()) {
            currentQuestion = maybeCurrentQuestion.get();
            questionText.setText(currentQuestion.getQuestion());
            currentQuestionPointsLabel.setText(String.valueOf(currentQuestion.getPoints()));
            hideAnswer();
            loadCurrentQuestionTags();
        } else {
            currentQuestion = null;
            questionText.setText("");
            answerText.setText("");
            currentQuestionPointsLabel.setText("");
            loadCurrentQuestionTags();
            showAnswer();
        }
    }

    public void createNewQuestion() {
        String questionText = this.createQuestionTextArea.getText();
        String answerText = this.createAnswerTextArea.getText();
        var selectedTagNames = new HashSet<>(createQuestionTagNamesListView.getSelectionModel().getSelectedItems());

        questionService.save(questionText, answerText, selectedTagNames);
    }

    private void hideAnswer() {
        answerText.setText("");
        answerVBox.setStyle("-fx-background-color: -color-warning-muted;");
    }

    private void loadCurrentQuestionTags() {
        var questionTagsList = currentQuestionTagsFlowPane.getChildren();
        questionTagsList.clear();

        if (currentQuestion != null) {
            var tags = tagService.findByQuestionId(currentQuestion.getId());

            for (var tag : tags) {
                var tagLabel = new Label(tag.getName());
                tagLabel.setPadding(new Insets(0, 5, 0, 5));
                tagLabel.setStyle("-fx-border-radius: 15; -fx-background-color: %s;".formatted(toHexString(tag.getColor())));
                questionTagsList.add(tagLabel);
            }
        }
    }

    private void refreshAvailableTags() {
        var tags = tagService.findAll();
        Set<String> newChosenTagNames = new HashSet<>();
        availableTagsMenuButton.getItems().clear();
        createQuestionTagNamesListView.getItems().clear();

        for (var tag : tags) {
            var checkBox = new CheckBox(tag.getName());
            checkBox.setOnAction(tagCheckBoxListener);
            var customMenuItem = new CustomMenuItem(checkBox);
            availableTagsMenuButton.getItems().add(customMenuItem);
            createQuestionTagNamesListView.getItems().add(tag.getName());

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