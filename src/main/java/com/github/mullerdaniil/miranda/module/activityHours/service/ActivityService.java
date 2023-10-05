package com.github.mullerdaniil.miranda.module.activityHours.service;

import com.github.mullerdaniil.miranda.module.activityHours.entity.Activity;
import com.github.mullerdaniil.miranda.module.activityHours.exception.ActivityServiceException;
import com.github.mullerdaniil.miranda.module.activityHours.repository.ActivityRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Transactional
@Service
public class ActivityService {
    private final ActivityRepository activityRepository;

    public Integer create(String name) {
        if (activityRepository.existsByName(name)) {
            throw new ActivityServiceException("Unable to create the activity: name already in use.");
        }

        var activity = Activity.builder()
                .name(name)
                .build();

        activityRepository.saveAndFlush(activity);

        return activity.getId();
    }

    public void delete(String name) {
        activityRepository.deleteActivityByName(name);
    }

    public List<Activity> findAll() {
        return activityRepository.findAll();
    }
}