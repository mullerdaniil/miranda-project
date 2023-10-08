package com.github.mullerdaniil.miranda.ui.controller;

import atlantafx.base.controls.RingProgressIndicator;
import com.github.mullerdaniil.miranda.module.activityHours.entity.Activity;
import com.github.mullerdaniil.miranda.module.activityHours.entity.HourSession;
import com.github.mullerdaniil.miranda.module.activityHours.event.ActivityDataUpdatedEvent;
import com.github.mullerdaniil.miranda.module.activityHours.event.HourSessionDataUpdatedEvent;
import com.github.mullerdaniil.miranda.module.activityHours.exception.ActivityServiceException;
import com.github.mullerdaniil.miranda.module.activityHours.exception.HourSessionServiceException;
import com.github.mullerdaniil.miranda.module.activityHours.service.ActivityService;
import com.github.mullerdaniil.miranda.module.activityHours.service.HourSessionService;
import com.github.mullerdaniil.miranda.ui.event.ActivityDataRefreshRequestEvent;
import com.github.mullerdaniil.miranda.ui.event.HourSessionDataRefreshRequestEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;

import static javafx.collections.FXCollections.observableArrayList;

@RequiredArgsConstructor
@Component
public class ActivityHoursController {
    private final ActivityService activityService;
    private final HourSessionService hourSessionService;
    private final ApplicationEventPublisher eventPublisher;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ListView<HourSession> hourSessionsListView;

    @FXML
    private ListView<String> activityNamesListView;

    @FXML
    private RingProgressIndicator hourSessionsProgressIndicator;

    public void initialize() {
        datePicker.setValue(LocalDate.now());
        eventPublisher.publishEvent(new ActivityDataRefreshRequestEvent());
        eventPublisher.publishEvent(new HourSessionDataRefreshRequestEvent());
        hourSessionsListView.setCellFactory(new HourSessionsListViewCellFactory());
        hourSessionsListView.setOnKeyPressed(new HourSessionsListViewKeyListener());
        activityNamesListView.setOnKeyPressed(new ActivityNamesListViewKeyListener());
        datePicker.valueProperty().addListener(new DatePickerChangeListener());
    }

    public void onNewActivityButton() {
        var textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("New activity");
        textInputDialog.setContentText("Enter the activity name:");
        textInputDialog.setHeaderText(null);
        textInputDialog.setGraphic(null);

        var optionalValue = textInputDialog.showAndWait();
        if (optionalValue.isPresent()) {
            var activityName = optionalValue.get();

            try {
                activityService.create(activityName);
            } catch (ActivityServiceException e) {
                showErrorMessage(e);
            }
        }
    }

    @TransactionalEventListener(
            value = {
                    ActivityDataUpdatedEvent.class,
                    ActivityDataRefreshRequestEvent.class
            },
            fallbackExecution = true
    )
    public void refreshActivityNames() {
        var activityNames = activityService.findAll()
                .stream()
                .map(Activity::getName)
                .toList();

        activityNamesListView.setItems(observableArrayList(activityNames));
        activityNamesListView.refresh();
    }

    @TransactionalEventListener(
            value = {
                    HourSessionDataUpdatedEvent.class,
                    HourSessionDataRefreshRequestEvent.class
            },
            fallbackExecution = true
    )
    public void refreshHourSessions() {
        var hourSessions = hourSessionService.findByDate(datePicker.getValue());
        hourSessionsListView.setItems(observableArrayList(hourSessions));
        refreshHourSessionsProgressIndicator();
        hourSessionsListView.refresh();
    }

    private void setSelectedHourSessionCompleted() {
        var selectedHourSession = hourSessionsListView.getSelectionModel().getSelectedItem();
        hourSessionService.reverseCompleted(selectedHourSession);
    }

    private void createHourSession() {
        var activityName = activityNamesListView.getSelectionModel().getSelectedItem();
        var date = datePicker.getValue();

        try {
            hourSessionService.create(activityName, date);
        } catch (HourSessionServiceException e) {
            showErrorMessage(e);
        }
    }

    private void deleteHourSession() {
        var selectedHourSession = hourSessionsListView.getSelectionModel().getSelectedItem();
        if (selectedHourSession != null) {
            hourSessionService.delete(selectedHourSession.getId());
        }
    }

    private void deleteActivity() {
        var activityName = activityNamesListView.getSelectionModel().getSelectedItem();
        activityService.delete(activityName);
    }

    private void refreshHourSessionsProgressIndicator() {
        long totalHourSessionsCount = hourSessionsListView.getItems().size();
        long completedHourSessionsCount = hourSessionsListView.getItems()
                .stream()
                .filter(HourSession::isCompleted)
                .count();

        double ratio = completedHourSessionsCount != 0 ?
                (double) completedHourSessionsCount / totalHourSessionsCount
                : 0.0;
        hourSessionsProgressIndicator.setProgress(ratio);
    }

    private void showErrorMessage(Exception e) {
        new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
    }

    private class HourSessionsListViewKeyListener implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            switch (event.getCode()) {
                case ENTER -> setSelectedHourSessionCompleted();
                case DELETE -> deleteHourSession();
            }
        }
    }

    private class ActivityNamesListViewKeyListener implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            switch (event.getCode()) {
                case ENTER -> createHourSession();
                case DELETE -> deleteActivity();
            }
        }
    }

    private class DatePickerChangeListener implements ChangeListener<LocalDate> {
        @Override
        public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
            eventPublisher.publishEvent(new HourSessionDataRefreshRequestEvent());
        }
    }

    private class HourSessionsListViewCellFactory implements Callback<ListView<HourSession>, ListCell<HourSession>> {
        @Override
        public ListCell<HourSession> call(ListView<HourSession> param) {
            return new ListCell<>() {
                @Override
                protected void updateItem(HourSession hourSession, boolean empty) {
                    super.updateItem(hourSession, empty);

                    if (empty || hourSession == null) {
                        setText(null);
                        setGraphic(null);
                        setStyle(null);
                    } else {
                        var selectedItem = hourSessionsListView.getSelectionModel().getSelectedItem();
                        boolean selected = selectedItem != null && selectedItem.equals(hourSession);

                        if (hourSession.isCompleted() && !selected) {
                            setStyle("-fx-background-color: -color-success-muted;");
                        } else {
                            setStyle(null);
                        }

                        setText(hourSession.getActivityName());
                    }
                }
            };
        }
    }
}