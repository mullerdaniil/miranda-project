package com.github.mullerdaniil.miranda.module.progressTracking.service;

import com.github.mullerdaniil.miranda.module.progressTracking.entity.ProgressPoint;
import com.github.mullerdaniil.miranda.module.progressTracking.event.TrackDataUpdatedEvent;
import com.github.mullerdaniil.miranda.module.progressTracking.exception.ProgressPointServiceException;
import com.github.mullerdaniil.miranda.module.progressTracking.repository.ProgressPointRepository;
import com.github.mullerdaniil.miranda.module.progressTracking.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ProgressPointService {
    private final ProgressPointRepository progressPointRepository;
    private final TrackRepository trackRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public List<ProgressPoint> findAllByTrackId(Integer trackId) {
        return progressPointRepository.findProgressPointsByTrackIdOrderByCreationTimeDesc(trackId);
    }

    public void create(Integer progress, Integer trackId) {
        var maybeTrack = trackRepository.findById(trackId);

        if (maybeTrack.isEmpty()) {
            throw new ProgressPointServiceException("Unable to create the progress point: track is not found.");
        }

        var track = maybeTrack.get();
        track.setLastUpdateTime(ZonedDateTime.now());

        var progressPoint = ProgressPoint.builder()
                .progress(progress)
                .track(track)
                .build();

        progressPointRepository.saveAndFlush(progressPoint);
        applicationEventPublisher.publishEvent(new TrackDataUpdatedEvent(track.getId(), true));
    }

    public void deleteById(Integer id) {
        var maybeProgressPoint = progressPointRepository.findById(id);

        if (maybeProgressPoint.isEmpty()) {
            throw new ProgressPointServiceException("Unable to delete the progress point: invalid id.");
        }

        var progressPoint = maybeProgressPoint.get();

        progressPointRepository.deleteById(id);
        applicationEventPublisher.publishEvent(new TrackDataUpdatedEvent(progressPoint.getTrack().getId(), false));
    }
}