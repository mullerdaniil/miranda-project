package com.github.mullerdaniil.miranda.ui;

import atlantafx.base.theme.CupertinoDark;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PrimaryStageInitializer implements ApplicationListener<MirandaJavaFxApplication.StageReadyEvent> {
    private final FxWeaver fxWeaver;

    @Override
    public void onApplicationEvent(MirandaJavaFxApplication.StageReadyEvent event) {
        Stage stage = event.getStage();
        Parent parent = fxWeaver.loadView(MainViewController.class);
        Scene scene = new Scene(parent, 1400, 700);
        stage.setScene(scene);
        stage.setMinWidth(1400);
        stage.setMinHeight(600);

        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
        stage.show();
    }
}