package com.github.mullerdaniil.miranda.ui.event;

import org.springframework.context.ApplicationEvent;

public class HourSessionDataUpdatedEvent extends ApplicationEvent {
    public HourSessionDataUpdatedEvent(Object source) {
        super(source);
    }
}
