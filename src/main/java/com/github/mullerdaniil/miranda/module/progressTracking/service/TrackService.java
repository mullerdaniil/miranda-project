package com.github.mullerdaniil.miranda.module.progressTracking.service;

import com.github.mullerdaniil.miranda.module.progressTracking.entity.Track;
import com.github.mullerdaniil.miranda.module.progressTracking.event.TrackDataUpdatedEvent;
import com.github.mullerdaniil.miranda.module.progressTracking.exception.TrackServiceException;
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
public class TrackService {
    private final TrackRepository trackRepository;
    private final ProgressPointRepository progressPointRepository;
    private final ApplicationEventPublisher eventPublisher;

    public List<Track> findAll() {
        return trackRepository.findAllByOrderByLastUpdateTimeDesc();
    }

    public void create(String name, Integer totalProgress, String unit) {
        if (trackRepository.existsByName(name)) {
            throw new TrackServiceException("Unable to create the track: name already in use.");
        }

        var track = Track.builder()
                .name(name)
                .totalProgress(totalProgress)
                .unit(unit)
                .lastUpdateTime(ZonedDateTime.now())
                .build();

        trackRepository.saveAndFlush(track);
        eventPublisher.publishEvent(new TrackDataUpdatedEvent(track.getId(), true));
    }

    public void deleteById(Integer id) {
        if (progressPointRepository.existsByTrackId(id)) {
            throw new TrackServiceException("Unable to delete: progress points associated with the track exist.");
        }

        trackRepository.deleteById(id);
        eventPublisher.publishEvent(new TrackDataUpdatedEvent(id, true));
    }
}