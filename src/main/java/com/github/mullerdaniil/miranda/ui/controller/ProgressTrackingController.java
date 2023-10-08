package com.github.mullerdaniil.miranda.ui.controller;

import com.github.mullerdaniil.miranda.module.progressTracking.entity.ProgressPoint;
import com.github.mullerdaniil.miranda.module.progressTracking.entity.Track;
import com.github.mullerdaniil.miranda.module.progressTracking.event.TrackDataUpdatedEvent;
import com.github.mullerdaniil.miranda.module.progressTracking.exception.ProgressPointServiceException;
import com.github.mullerdaniil.miranda.module.progressTracking.exception.TrackServiceException;
import com.github.mullerdaniil.miranda.module.progressTracking.service.ProgressPointService;
import com.github.mullerdaniil.miranda.module.progressTracking.service.TrackService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.github.mullerdaniil.miranda.ui.util.DialogUtil.*;
import static java.util.Objects.requireNonNull;
import static javafx.collections.FXCollections.observableArrayList;

@RequiredArgsConstructor
@Component
public class ProgressTrackingController {
    private final TrackService trackService;
    private final ProgressPointService progressPointService;

    @FXML
    private ListView<ProgressPoint> progressPointsListView;

    @FXML
    private ListView<Track> tracksListView;

    @FXML
    private TextField progressPointProgressTextField;

    @FXML
    private ProgressBar trackProgressBar;

    public void initialize() {
        progressPointsListView.setOnKeyPressed(new ProgressPointsListViewKeyListener());
        tracksListView.setOnKeyPressed(new TracksListViewKeyListener());
        progressPointProgressTextField.setOnKeyPressed(new ProgressPointProgressTextFieldKeyListener());
        tracksListView.getSelectionModel().selectedItemProperty()
                .addListener(new TracksListViewOnSelectionChangeListener());

        progressPointsListView.setCellFactory(new ProgressPointsListViewCellFactory());
        tracksListView.setCellFactory(new TracksListViewCellFactory());

        refreshProgressPoints();
        refreshTracks(null);
    }

    public void onNewTrackButton() {
        var taskCreationDialog = buildTaskCreationDialog();
        taskCreationDialog.showAndWait();
    }

    public void onNewProgressPointButton() {
        var selectedTrack = tracksListView.getSelectionModel().getSelectedItem();

        if (selectedTrack == null) {
            showErrorDialog("Unable to create the progress point: track is not selected.");
            return;
        }

        try {
            var text = progressPointProgressTextField.getText();
            int progressValue = Integer.parseInt(text);

            progressPointService.create(progressValue, selectedTrack.getId());
        } catch (NumberFormatException e) {
            showErrorDialog("Unable to create the progress point: invalid progress value number format.");
        } catch (ProgressPointServiceException e) {
            showExceptionDialog(e);
        }
    }

    private void deleteSelectedProgressPoint() {
        var selectedProgressPoint = progressPointsListView.getSelectionModel().getSelectedItem();
        if (selectedProgressPoint != null) {
            progressPointService.deleteById(selectedProgressPoint.getId());
        }
    }

    private void deleteSelectedTrack() {
        var selectedTrack = tracksListView.getSelectionModel().getSelectedItem();
        if (selectedTrack != null) {
            try {
                trackService.deleteById(selectedTrack.getId());
            } catch (TrackServiceException e) {
                showExceptionDialog(e);
            }
        }
    }

    private Dialog<TaskCreationDialogResult> buildTaskCreationDialog() {
        Dialog<TaskCreationDialogResult> taskCreationDialog = new Dialog<>();
        taskCreationDialog.setTitle("New track");
        taskCreationDialog.setContentText(null);
        taskCreationDialog.setHeaderText(null);
        taskCreationDialog.setGraphic(null);

        var nameLabel = new Label("Name");
        var totalProgressLabel = new Label("Total Progress");
        var unitLabel = new Label("Unit");

        var nameTextField = new TextField();
        var totalProgressTextField = new TextField();
        var unitTextField = new TextField();

        var gridPane = new GridPane();
        gridPane.getColumnConstraints().addAll(
                new ColumnConstraints(),
                new ColumnConstraints()
        );
        gridPane.getRowConstraints().addAll(
                new RowConstraints(),
                new RowConstraints(),
                new RowConstraints()
        );

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.getColumnConstraints().get(0).setHalignment(HPos.RIGHT);

        gridPane.add(nameLabel, 0, 0);
        gridPane.add(totalProgressLabel, 0, 1);
        gridPane.add(unitLabel, 0, 2);
        gridPane.add(nameTextField, 1, 0);
        gridPane.add(totalProgressTextField, 1, 1);
        gridPane.add(unitTextField, 1, 2);

        var dialogPane = taskCreationDialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setContent(gridPane);

        final Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.addEventFilter(
                ActionEvent.ACTION,
                event -> {
                    try {
                        String name = nameTextField.getText();
                        Integer totalProgressValue = Integer.parseInt(totalProgressTextField.getText());
                        String unit = unitTextField.getText();

                        if (name.isBlank()) {
                            showErrorDialog("Unable to create the task: name must not be blank.");
                            return;
                        }
                        if (unit.isBlank()) {
                            showErrorDialog("Unable to create the task: unit must not be blank.");
                            return;
                        }

                        trackService.create(name, totalProgressValue, unit);
                    } catch (NumberFormatException e) {
                        showErrorDialog("Unable to create the task: invalid total progress value.");
                        event.consume();
                    } catch (TrackServiceException e) {
                        showExceptionDialog(e);
                        event.consume();
                    }
                }
        );

        return taskCreationDialog;
    }

    private record TaskCreationDialogResult(String name,
                                            String totalProgress,
                                            String unit) {
    }

    @TransactionalEventListener(TrackDataUpdatedEvent.class)
    public void reactToTracksDataUpdating(TrackDataUpdatedEvent event) {
        var selectedTrack = tracksListView.getSelectionModel().getSelectedItem();

        if (event.updateAll()) {
            refreshTracks(tracksListView.getSelectionModel().getSelectedItem());
        } else if (selectedTrack != null && selectedTrack.getId().equals(event.trackId())) {
            refreshProgressPoints();
        }
    }

    public void refreshProgressPoints() {
        var selectedTrack = tracksListView.getSelectionModel().getSelectedItem();

        List<ProgressPoint> progressPoints;
        if (selectedTrack != null) {
            progressPoints = progressPointService.findAllByTrackId(selectedTrack.getId());
        } else {
            progressPoints = new ArrayList<>();
        }
        progressPointsListView.setItems(observableArrayList(progressPoints));
        refreshTrackProgressBar();
    }

    private void refreshTrackProgressBar() {
        var selectedTrack = tracksListView.getSelectionModel().getSelectedItem();

        double progressValue = 0;
        if (selectedTrack != null) {
            var progressPoints = progressPointsListView.getItems();

            if (!progressPoints.isEmpty()) {
                progressValue = (double) progressPoints.get(0).getProgress() / selectedTrack.getTotalProgress();
            }
        } else {
            progressValue = ProgressBar.INDETERMINATE_PROGRESS;
        }

        trackProgressBar.setProgress(progressValue);
    }

    public void refreshTracks(Track previouslySelectedTrack) {
        tracksListView.setItems(observableArrayList(trackService.findAll()));

        if (previouslySelectedTrack != null) {
            var maybeTrackToSelect = tracksListView.getItems()
                    .stream()
                    .filter(track -> track.getId().equals(previouslySelectedTrack.getId()))
                    .findFirst();

            if (maybeTrackToSelect.isPresent()) {
                var trackToSelect = maybeTrackToSelect.get();
                tracksListView.getSelectionModel().select(trackToSelect);
            }
        }
    }

    private class ProgressPointsListViewKeyListener implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            if (requireNonNull(event.getCode()) == KeyCode.DELETE) {
                deleteSelectedProgressPoint();
            }
        }
    }

    private class TracksListViewKeyListener implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            if (requireNonNull(event.getCode()) == KeyCode.DELETE) {
                deleteSelectedTrack();
            }
        }
    }

    private class ProgressPointProgressTextFieldKeyListener implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
                onNewProgressPointButton();
            }
        }
    }

    private class TracksListViewOnSelectionChangeListener implements ChangeListener<Track> {
        @Override
        public void changed(ObservableValue<? extends Track> observable, Track oldValue, Track newValue) {
            refreshProgressPoints();
        }
    }

    private static class ProgressPointsListViewCellFactory implements Callback<ListView<ProgressPoint>, ListCell<ProgressPoint>> {
        @Override
        public ListCell<ProgressPoint> call(ListView<ProgressPoint> param) {
            return new ListCell<>() {
                @Override
                protected void updateItem(ProgressPoint progressPoint, boolean empty) {
                    super.updateItem(progressPoint, empty);

                    if (empty || progressPoint == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText("%s\t{%s}".formatted(
                                progressPoint.getProgress(),
                                progressPoint.getCreationTime().format(DateTimeFormatter.ofPattern("dd.MM.yy-HH:mm")))
                        );
                    }
                }
            };
        }
    }

    private static class TracksListViewCellFactory implements Callback<ListView<Track>, ListCell<Track>> {
        @Override
        public ListCell<Track> call(ListView<Track> param) {
            return new ListCell<>() {
                @Override
                protected void updateItem(Track track, boolean empty) {
                    super.updateItem(track, empty);

                    if (empty || track == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText("%s [[%d/%s]]".formatted(
                                track.getName(),
                                track.getTotalProgress(),
                                track.getUnit())
                        );
                    }
                }
            };
        }
    }
}