package com.github.mullerdaniil.miranda.module.activityHours.repository;

import com.github.mullerdaniil.miranda.module.activityHours.entity.HourSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HourSessionRepository extends JpaRepository<HourSession, Integer> {
    List<HourSession> findHourSessionsByDateOrderById(LocalDate date);
    List<HourSession> findHourSessionsByDateAndCompletedTrueOrderById(LocalDate date);
    List<HourSession> findHourSessionsByDateBetweenAndCompletedTrueOrderById(LocalDate start, LocalDate end);
}