package ru.practicum.hit;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.hit.entity.EndpointHit;

public interface HitStorage extends JpaRepository<EndpointHit, Long>, HitRepository {

}
