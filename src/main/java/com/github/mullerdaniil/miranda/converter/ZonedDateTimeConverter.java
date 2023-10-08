package com.github.mullerdaniil.miranda.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

@Converter(autoApply = true)
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, LocalDateTime> {
    @Override
    public LocalDateTime convertToDatabaseColumn(ZonedDateTime zonedDateTime) {
        return Optional.ofNullable(zonedDateTime)
                .map(zonedDT -> zonedDT.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                .orElse(null);
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime)
                .map(localDT -> localDT.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()))
                .orElse(null);
    }
}