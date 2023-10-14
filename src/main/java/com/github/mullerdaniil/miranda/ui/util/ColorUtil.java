package com.github.mullerdaniil.miranda.ui.util;

import java.awt.*;

public class ColorUtil {
    public static String toHexString(Color color) {
        return "#%02X%02X%02X".formatted(
                color.getRed(),
                color.getGreen(),
                color.getBlue()
        );
    }
}