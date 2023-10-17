package com.github.mullerdaniil.miranda.ui.questionTraining.converter;

import org.springframework.stereotype.Component;

@Component
public class ColorConverter implements BidirectionalConverter<java.awt.Color, javafx.scene.paint.Color> {
    @Override
    public javafx.scene.paint.Color convertTo(java.awt.Color obj) {
        return new javafx.scene.paint.Color(
                (double) obj.getRed() / 255.0,
                (double) obj.getGreen() / 255.0,
                (double) obj.getBlue() / 255.0,
                1.0
        );
    }

    @Override
    public java.awt.Color convertFrom(javafx.scene.paint.Color obj) {
        return new java.awt.Color(
                (float) obj.getRed(),
                (float) obj.getGreen(),
                (float) obj.getBlue()
        );
    }
}