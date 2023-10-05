package com.github.mullerdaniil.miranda.ui.event;

import org.springframework.context.ApplicationEvent;

public class ActivityDataUpdatedEvent extends ApplicationEvent {
    public ActivityDataUpdatedEvent(Object source) {
        super(source);
    }
}
