package com.github.mullerdaniil.miranda.module.activityHours.service;

import com.github.mullerdaniil.miranda.module.activityHours.repository.HourSessionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
class HourSessionServiceTest {
    @Autowired
    private HourSessionService hourSessionService;


    void findTodayHourSessions() {

    }
}