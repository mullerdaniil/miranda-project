package com.github.mullerdaniil.miranda.module.progressTracking.repository;

import com.github.mullerdaniil.miranda.module.progressTracking.entity.ProgressPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgressPointRepository extends JpaRepository<ProgressPoint, Integer> {
    List<ProgressPoint> findProgressPointsByTrackIdOrderByCreationTimeDesc(Integer trackId);
    boolean existsByTrackId(Integer trackId);
}