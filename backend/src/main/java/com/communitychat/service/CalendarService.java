package com.communitychat.service;

import com.communitychat.model.entity.Event;
import com.communitychat.model.entity.GroupMember;
import com.communitychat.model.entity.User;
import com.communitychat.repository.EventRepository;
import com.communitychat.repository.GroupMemberRepository;
import com.communitychat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Autowired
    public CalendarService(EventRepository eventRepository, 
                          UserRepository userRepository,
                          GroupMemberRepository groupMemberRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    public List<Event> getUpcomingEvents(Long userId) throws Exception {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new Exception("User not found");
        }

        LocalDateTime now = LocalDateTime.now();
        
        // Get all events from groups the user is in
        List<GroupMember> memberships = groupMemberRepository.findByUser(user.get());
        List<Event> allEvents = new ArrayList<>();
        
        for (GroupMember gm : memberships) {
            allEvents.addAll(eventRepository.findByGroup(gm.getGroup()));
        }
        
        // Filter upcoming events only
        return allEvents.stream()
                .filter(event -> event.getDateTime().isAfter(now))
                .sorted((e1, e2) -> e1.getDateTime().compareTo(e2.getDateTime()))
                .collect(Collectors.toList());
    }

    public List<Event> getEventsInRange(Long userId, LocalDateTime from, LocalDateTime to) throws Exception {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new Exception("User not found");
        }

        // Get all events from groups the user is in
        List<GroupMember> memberships = groupMemberRepository.findByUser(user.get());
        List<Event> allEvents = new ArrayList<>();
        
        for (GroupMember gm : memberships) {
            allEvents.addAll(eventRepository.findByGroup(gm.getGroup()));
        }
        
        // Filter events in date range
        return allEvents.stream()
                .filter(event -> !event.getDateTime().isBefore(from) && !event.getDateTime().isAfter(to))
                .sorted((e1, e2) -> e1.getDateTime().compareTo(e2.getDateTime()))
                .collect(Collectors.toList());
    }
}