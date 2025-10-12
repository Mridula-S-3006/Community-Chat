package com.communitychat.service;

import com.communitychat.model.entity.Event;
import com.communitychat.model.entity.EventResponse;
import com.communitychat.model.entity.User;
import com.communitychat.repository.EventRepository;
import com.communitychat.repository.EventResponseRepository;
import com.communitychat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventResponseRepository eventResponseRepository;
    private final UserRepository userRepository;

    @Autowired
    public EventService(EventRepository eventRepository, EventResponseRepository eventResponseRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.eventResponseRepository = eventResponseRepository;
        this.userRepository = userRepository;
    }

    public Event createEvent(Long creatorId, String title, String description, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) throws Exception {
        Optional<User> creator = userRepository.findById(creatorId);
        if (!creator.isPresent()) {
            throw new Exception("Creator not found");
        }

        Event event = new Event();
        event.setCreator(creator.get());
        event.setTitle(title);
        event.setDescription(description);
        event.setStartTime(startTime);
        event.setEndTime(endTime);

        return eventRepository.save(event);
    }

    public List<Event> getUserEvents(Long userId) throws Exception {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new Exception("User not found");
        }

        return eventRepository.findByCreator(user.get());
    }

    public EventResponse respondToEvent(Long eventId, Long userId, String response) throws Exception {
        Optional<Event> event = eventRepository.findById(eventId);
        Optional<User> user = userRepository.findById(userId);
        if (!event.isPresent() || !user.isPresent()) {
            throw new Exception("Event or user not found");
        }

        Optional<EventResponse> existing = eventResponseRepository.findByEventAndUser(event.get(), user.get());
        EventResponse er = existing.orElse(new EventResponse());
        er.setEvent(event.get());
        er.setUser(user.get());
        er.setResponse(response);

        return eventResponseRepository.save(er);
    }

    public List<EventResponse> getEventResponses(Long eventId) throws Exception {
        Optional<Event> event = eventRepository.findById(eventId);
        if (!event.isPresent()) {
            throw new Exception("Event not found");
        }
        return eventResponseRepository.findByEvent(event.get());
    }
}
