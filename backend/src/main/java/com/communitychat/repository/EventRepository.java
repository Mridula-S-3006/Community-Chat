package com.communitychat.repository;

import com.communitychat.model.entity.Event;
import com.communitychat.model.entity.Group;
import com.communitychat.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByGroup(Group group);
    List<Event> findByOrganizer(User organizer);
}