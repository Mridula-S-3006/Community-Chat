package com.communitychat.service;

import com.communitychat.model.entity.Event;
import com.communitychat.model.entity.User;
import com.communitychat.repository.EventRepository;
import com.communitychat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Autowired
    public CalendarService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<Event> getUpcomingEvents(Long userId) throws Exception {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new Exception("User not found");
        }

        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findByCreator(user.get()).stream()
                .filter(event -> event.getStartTime().isAfter(now))
                .collect(Collectors.toList());
    }

    public List<Event> getEventsInRange(Long userId, LocalDateTime from, LocalDateTime to) throws Exception {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new Exception("User not found");
        }

        return eventRepository.findByCreator(user.get()).stream()
                .filter(event -> !event.getStartTime().isBefore(from) && !event.getEndTime().isAfter(to))
                .collect(Collectors.toList());
    }
}
