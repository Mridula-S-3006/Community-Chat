package com.communitychat.service;

import com.communitychat.model.entity.Event;
import com.communitychat.model.entity.EventResponse;
import com.communitychat.model.entity.Group;
import com.communitychat.model.entity.GroupMember;
import com.communitychat.model.entity.User;
import com.communitychat.repository.EventRepository;
import com.communitychat.repository.EventResponseRepository;
import com.communitychat.repository.GroupRepository;
import com.communitychat.repository.GroupMemberRepository;
import com.communitychat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventResponseRepository eventResponseRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Autowired
    public EventService(EventRepository eventRepository,
                        EventResponseRepository eventResponseRepository,
                        GroupRepository groupRepository,
                        UserRepository userRepository,
                        GroupMemberRepository groupMemberRepository) {
        this.eventRepository = eventRepository;
        this.eventResponseRepository = eventResponseRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    public Event createEvent(Long organizerId, Long groupId, String title,
                             String description, LocalDateTime dateTime, String location) throws Exception {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new Exception("Organizer not found"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new Exception("Group not found"));

        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setOrganizer(organizer);
        event.setGroup(group);
        event.setDateTime(dateTime);
        event.setLocation(location);
        event.setCreatedAt(LocalDateTime.now());

        return eventRepository.save(event);
    }

    public List<Event> getUserEvents(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        
        // Get all groups the user is a member of
        List<GroupMember> memberships = groupMemberRepository.findByUser(user);
        
        List<Event> events = new ArrayList<>();
        for (GroupMember gm : memberships) {
            events.addAll(eventRepository.findByGroup(gm.getGroup()));
        }
        
        return events;
    }

    public EventResponse respondToEvent(Long eventId, Long userId, String response) throws Exception {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new Exception("Event not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        EventResponse er = eventResponseRepository
                .findByEventAndUser(event, user)
                .orElse(new EventResponse());

        er.setEvent(event);
        er.setUser(user);
        er.setResponse(response);

        return eventResponseRepository.save(er);
    }

    public List<EventResponse> getEventResponses(Long eventId) throws Exception {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new Exception("Event not found"));
        return eventResponseRepository.findByEvent(event);
    }
}