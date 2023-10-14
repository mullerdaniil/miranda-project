package com.github.mullerdaniil.miranda.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.awt.*;
import java.util.Optional;

@Converter(autoApply = true)
public class ColorConverter implements AttributeConverter<Color, String> {
    @Override
    public String convertToDatabaseColumn(Color color) {
        return Optional.ofNullable(color)
                .map(rgbColor -> "#%02X%02X%02X".formatted(
                        rgbColor.getRed(),
                        rgbColor.getGreen(),
                        rgbColor.getBlue()
                ))
                .orElse(null);
    }

    @Override
    public Color convertToEntityAttribute(String colorHexString) {
        return Optional.ofNullable(colorHexString)
                .map(hexString -> {
                    var red = Integer.parseInt(hexString.substring(1, 3), 16);
                    var green = Integer.parseInt(hexString.substring(3, 5), 16);
                    var blue = Integer.parseInt(hexString.substring(5, 7), 16);

                    return new Color(red, green, blue);
                })
                .orElse(null);
    }
}