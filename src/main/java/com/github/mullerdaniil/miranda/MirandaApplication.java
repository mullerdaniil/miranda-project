package com.github.mullerdaniil.miranda;

import com.github.mullerdaniil.miranda.ui.MirandaJavaFxApplication;
import javafx.application.Application;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.spring.SpringFxWeaver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MirandaApplication {
    public static void main(String[] args) {
//        SpringApplication.run(MirandaApplication.class, args);
        Application.launch(MirandaJavaFxApplication.class, args);
    }

    @Bean
    public FxWeaver fxWeaver(ConfigurableApplicationContext applicationContext) {
        return new SpringFxWeaver(applicationContext);
    }
}