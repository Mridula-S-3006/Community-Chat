package com.communitychat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.communitychat.model.entity.Event;
import com.communitychat.model.entity.User;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCreator(User creator);
}
