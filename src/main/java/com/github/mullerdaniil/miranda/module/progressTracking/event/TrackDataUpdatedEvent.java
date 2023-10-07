package com.github.mullerdaniil.miranda.module.progressTracking.event;

public record TrackDataUpdatedEvent(Integer trackId,
                                    boolean updateAll) {
}