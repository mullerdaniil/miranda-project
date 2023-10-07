package com.github.mullerdaniil.miranda.module.progressTracking.repository;

import com.github.mullerdaniil.miranda.module.progressTracking.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Integer> {
    boolean existsByName(String name);
    List<Track> findAllByOrderByLastUpdateTimeDesc();
}