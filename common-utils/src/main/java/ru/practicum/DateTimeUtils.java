package ru.practicum;

import ru.practicum.exception.BadRequestException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// Делать это бином вместо utility-class на мой взгляд лишнее, потому что в этом классе нет
// внутреннего состояния или сложной логики, по сути это обвязка над системными статическими методами
// из LocalDateTime и DateTimeFormatter.
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
            // так как в этом методе используется BadRequestException, все эксепшены перенесены в
            // общий модуль, иначе снова получается циклическая зависимость модулей,
            // а переносить только один эксепшен - нарушение структуры проекта )
        }
    }

    public static String getDateTime(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).format(localDateTime);
    }
}
