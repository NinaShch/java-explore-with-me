package ru.practicum.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
public class Location {
    Double lat;
    Double lon;
}
