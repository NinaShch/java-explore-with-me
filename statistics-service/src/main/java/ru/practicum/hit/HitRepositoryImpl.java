package ru.practicum.hit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.hit.entity.EndpointHit;
import ru.practicum.stats.dto.ViewStatsDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HitRepositoryImpl implements HitRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, boolean unique) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ViewStatsDto> cr = cb.createQuery(ViewStatsDto.class);
        Root<EndpointHit> root = cr.from(EndpointHit.class);
        if (unique) {
            cr.multiselect(root.get("app"), root.get("uri"), cb.countDistinct(root.get("ip")));
        } else {
            cr.multiselect(root.get("app"), root.get("uri"), cb.count(root));
        }
        cr.groupBy(root.get("app"), root.get("uri"));
        cr.where(cb.and(
                cb.greaterThanOrEqualTo(root.get("timestamp"), start),
                cb.lessThanOrEqualTo(root.get("timestamp"), end)
        ));
        return entityManager.createQuery(cr).getResultList();
    }

    @Override
    public List<ViewStatsDto> getStatsByUri(LocalDateTime start, LocalDateTime end, String uri, boolean unique) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ViewStatsDto> cr = cb.createQuery(ViewStatsDto.class);
        Root<EndpointHit> root = cr.from(EndpointHit.class);
        if (unique) {
            cr.multiselect(root.get("app"), root.get("uri"), cb.countDistinct(root.get("ip")));
        } else {
            cr.multiselect(root.get("app"), root.get("uri"), cb.count(root));
        }
        cr.groupBy(root.get("app"), root.get("uri"));
        cr.where(cb.and(
                cb.greaterThanOrEqualTo(root.get("timestamp"), start),
                cb.lessThanOrEqualTo(root.get("timestamp"), end),
                cb.equal(root.get("uri"), uri)
        ));
        return entityManager.createQuery(cr).getResultList();
    }

}
