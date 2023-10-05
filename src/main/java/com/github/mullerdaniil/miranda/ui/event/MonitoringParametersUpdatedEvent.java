package com.github.mullerdaniil.miranda.ui.event;

import org.springframework.context.ApplicationEvent;

public class MonitoringParametersUpdatedEvent extends ApplicationEvent {
    public MonitoringParametersUpdatedEvent(Object source) {
        super(source);
    }
}