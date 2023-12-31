package com.github.mullerdaniil.miranda.module.activityHours.service;

import com.github.mullerdaniil.miranda.module.activityHours.entity.HourSession;
import com.github.mullerdaniil.miranda.module.activityHours.event.HourSessionDataUpdatedEvent;
import com.github.mullerdaniil.miranda.module.activityHours.exception.HourSessionServiceException;
import com.github.mullerdaniil.miranda.module.activityHours.repository.ActivityRepository;
import com.github.mullerdaniil.miranda.module.activityHours.repository.HourSessionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Transactional
@Service
public class HourSessionService {
    private final HourSessionRepository hourSessionRepository;
    private final ActivityRepository activityRepository;
    private final ApplicationEventPublisher eventPublisher;

    public List<HourSession> findByDate(LocalDate date) {
        return hourSessionRepository.findHourSessionsByDateOrderById(date);
    }

    public List<HourSession> findCompetedByDateSpan(LocalDate start, LocalDate end) {
        return hourSessionRepository.findHourSessionsByDateBetweenAndCompletedTrueOrderById(start, end);
    }

    public void create(String activityName, LocalDate date) {
        var maybeActivity = activityRepository.findByName(activityName);
        if (maybeActivity.isEmpty()) {
            throw new HourSessionServiceException("Unable to create the HourSession: activity not found.");
        }

        var hourSession = HourSession.builder()
                .activityName(activityName)
                .date(date)
                .build();

        hourSessionRepository.saveAndFlush(hourSession);
        eventPublisher.publishEvent(new HourSessionDataUpdatedEvent());
    }

    public void reverseCompleted(HourSession hourSession) {
        hourSession.reverseCompleted();
        hourSessionRepository.saveAndFlush(hourSession);
        eventPublisher.publishEvent(new HourSessionDataUpdatedEvent());
    }

    public void delete(Integer id) {
        hourSessionRepository.deleteById(id);
        eventPublisher.publishEvent(new HourSessionDataUpdatedEvent());
    }
}