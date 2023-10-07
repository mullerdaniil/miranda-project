package com.github.mullerdaniil.miranda.ui.controller;

import com.github.mullerdaniil.miranda.module.activityHours.service.HourSessionService;
import com.github.mullerdaniil.miranda.module.activityHours.event.HourSessionDataUpdatedEvent;
import com.github.mullerdaniil.miranda.ui.event.HourSessionDataRefreshRequestEvent;
import com.github.mullerdaniil.miranda.ui.event.MonitoringParametersUpdatedEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.DatePicker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static javafx.collections.FXCollections.observableArrayList;

@RequiredArgsConstructor
@Component
public class ActivityHoursMonitoringController {
    private final HourSessionService hourSessionService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @FXML
    private PieChart hoursByActivityChart;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    public void initialize() {
        var datePickerChangeListener = new DatePickerChangeListener();
        fromDatePicker.valueProperty().addListener(datePickerChangeListener);
        toDatePicker.valueProperty().addListener(datePickerChangeListener);
        fromDatePicker.setValue(LocalDate.now());
        toDatePicker.setValue(LocalDate.now());
        drawChart();
    }

    @EventListener({
            HourSessionDataUpdatedEvent.class,
            HourSessionDataRefreshRequestEvent.class,
            MonitoringParametersUpdatedEvent.class
    })
    public void drawChart() {
        hoursByActivityChart.setData(buildPieChartData());
    }

    private ObservableList<PieChart.Data> buildPieChartData() {
        ObservableList<PieChart.Data> dataObservableList = observableArrayList();

        var hourSessions = hourSessionService.findCompetedByDateSpan(fromDatePicker.getValue(), toDatePicker.getValue());

        Map<String, Integer> hoursByActivity = new HashMap<>();
        for (var hourSession : hourSessions) {
            hoursByActivity.merge(hourSession.getActivityName(), 1, Integer::sum);
        }

        for (var activityName : hoursByActivity.keySet()) {
            var hours = hoursByActivity.get(activityName);
            dataObservableList.add(new PieChart.Data(activityName, hours));
        }

        return dataObservableList;
    }

    private class DatePickerChangeListener implements ChangeListener<LocalDate> {
        @Override
        public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
            applicationEventPublisher.publishEvent(new MonitoringParametersUpdatedEvent());
        }
    }
}