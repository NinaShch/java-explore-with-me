package ru.practicum.hit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HitStorage extends JpaRepository<EndpointHit, Long>, HitRepository {

}
