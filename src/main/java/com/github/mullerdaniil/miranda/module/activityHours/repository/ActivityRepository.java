package com.github.mullerdaniil.miranda.module.activityHours.repository;

import com.github.mullerdaniil.miranda.module.activityHours.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    boolean existsByName(String name);
    void deleteActivityByName(String name);
    Optional<Activity> findByName(String name);
}
