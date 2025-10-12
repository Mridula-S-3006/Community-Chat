package com.communitychat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.communitychat.model.entity.Event;
import com.communitychat.model.entity.EventResponse;
import com.communitychat.model.entity.User;

@Repository
public interface EventResponseRepository extends JpaRepository<EventResponse, Long> {
    List<EventResponse> findByEvent(Event event);
    List<EventResponse> findByUser(User user);
    Optional<EventResponse> findByEventAndUser(Event event, User user);
}
