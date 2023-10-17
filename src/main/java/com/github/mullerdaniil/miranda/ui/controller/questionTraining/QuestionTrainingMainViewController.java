package com.github.mullerdaniil.miranda.ui.controller.questionTraining;

import com.github.mullerdaniil.miranda.module.questionTraining.config.QuestionTrainingProperties;
import com.github.mullerdaniil.miranda.module.questionTraining.entity.Question;
import com.github.mullerdaniil.miranda.module.questionTraining.service.QuestionService;
import com.github.mullerdaniil.miranda.module.questionTraining.service.TagService;
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
import java.util.Set;

import static com.github.mullerdaniil.miranda.ui.util.ColorUtil.toHexString;

@RequiredArgsConstructor
//@Component
public class QuestionTrainingMainViewController {
}