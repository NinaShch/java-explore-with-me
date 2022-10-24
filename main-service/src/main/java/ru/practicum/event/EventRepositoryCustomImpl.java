package ru.practicum.event;

import org.springframework.stereotype.Repository;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.BadRequestException;
import ru.practicum.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventRepositoryCustomImpl implements EventRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Event> findByParamsCommon(String text, List<Category> categories, Boolean paid, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd, boolean onlyAvailable, String sort,
                                          int from, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> q = cb.createQuery(Event.class);
        Root<Event> eventRoot = q.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();
        if (text != null)
            predicates.add(cb.or(
                    cb.like(eventRoot.get("annotation"), "%" + text + "%"),
                    cb.like(eventRoot.get("description"), "%" + text + "%")
            ));
        if (categories != null)
            predicates.add(eventRoot.get("category").in(categories));
        if (paid != null)
            predicates.add(cb.equal(eventRoot.get("paid"), paid));
        if (rangeStart != null)
            predicates.add(cb.greaterThanOrEqualTo(eventRoot.get("eventDate"), rangeStart));
        if (rangeEnd != null)
            predicates.add(cb.lessThanOrEqualTo(eventRoot.get("eventDate"), rangeEnd));
        if (onlyAvailable)
            predicates.add(cb.or(cb.equal(eventRoot.get("participant_limit"), 0),
                    cb.greaterThan(eventRoot.get("participant_limit"), eventRoot.get("confirmed_requests"))));
        q.select(eventRoot).where(cb.and(predicates.toArray(new Predicate[0])));
        if ("EVENT_DATE".equals(sort))
            q.orderBy(cb.asc(eventRoot.get("eventDate")));
        else if ("VIEWS".equals(sort))
            q.orderBy(cb.asc(eventRoot.get("views")));
        else if (sort != null) throw new BadRequestException("Wrong sort");
        return entityManager.createQuery(q).setFirstResult(from).setMaxResults(size).getResultList();
    }

    @Override
    public List<Event> findByParamsForAdmin(List<User> users, List<State> states, List<Category> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> q = cb.createQuery(Event.class);
        Root<Event> eventRoot = q.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();
        if (users != null)
            predicates.add(eventRoot.get("initiator").in(users));
        if (states != null)
            predicates.add(eventRoot.get("state").in(states));
        if (categories != null)
            predicates.add(eventRoot.get("category").in(categories));
        if (rangeStart != null)
            predicates.add(cb.greaterThanOrEqualTo(eventRoot.get("eventDate"), rangeStart));
        if (rangeEnd != null)
            predicates.add(cb.lessThanOrEqualTo(eventRoot.get("eventDate"), rangeEnd));
        q.select(eventRoot).where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(q).setFirstResult(from).setMaxResults(size).getResultList();
    }
}
