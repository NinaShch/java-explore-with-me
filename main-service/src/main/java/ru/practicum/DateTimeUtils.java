package ru.practicum;

import ru.practicum.exception.BadRequestException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtils {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static LocalDateTime parseDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Wrong datetime " + dateString, "Wrong dateTime format", ex);
        }
    }

    public static String getDateTimeNow() {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).format(LocalDateTime.now());
    }

    public static String getDateTimeSixMonthsAgo() {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).format(LocalDateTime.now().minusMonths(6));
    }
}
