package com.github.mullerdaniil.miranda.ui.event;

import org.springframework.context.ApplicationEvent;

// TODO: 07.10.2023 move from ui layer
public class HourSessionDataUpdatedEvent extends ApplicationEvent {
    public HourSessionDataUpdatedEvent(Object source) {
        super(source);
    }
}
