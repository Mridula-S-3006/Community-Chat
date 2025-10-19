package com.communitychat.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.communitychat.model.entity.Event;
import com.communitychat.model.entity.EventResponse;
import com.communitychat.model.entity.GroupMember;
import com.communitychat.repository.EventRepository;
import com.communitychat.repository.EventResponseRepository;
import com.communitychat.repository.GroupMemberRepository;
import com.communitychat.repository.GroupRepository;
import com.communitychat.repository.UserRepository;
import com.communitychat.service.EventService;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventService eventService;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private EventResponseRepository eventResponseRepository;

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Map<String, Object> payload) {
        try {
            // Get current user from session (for now using first user as demo)
            Long organizerId = userRepository.findAll().get(0).getId();
            
            Long groupId = Long.valueOf(payload.get("groupId").toString());
            String title = payload.get("title").toString();
            String description = payload.get("description").toString();
            LocalDateTime dateTime = LocalDateTime.parse(payload.get("dateTime").toString());
            String location = payload.getOrDefault("location", "").toString();
            Integer reminderMinutes = payload.containsKey("reminderMinutes") && !payload.get("reminderMinutes").toString().isEmpty() ? 
                Integer.valueOf(payload.get("reminderMinutes").toString()) : null;

            Event event = eventService.createEvent(organizerId, groupId, title, description, dateTime, location);
            event.setReminderMinutes(reminderMinutes);
            event = eventRepository.save(event);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", event.getId());
            response.put("title", event.getTitle());
            response.put("description", event.getDescription());
            response.put("dateTime", event.getDateTime().toString());
            response.put("location", event.getLocation());
            response.put("groupId", event.getGroup().getId());
            response.put("groupName", event.getGroup().getName());
            response.put("reminderMinutes", event.getReminderMinutes());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserEvents() {
        try {
            // Get current user from session (demo: use first user)
            Long userId = userRepository.findAll().get(0).getId();
            
            // Get all groups user is member of
            List<GroupMember> memberships = groupMemberRepository.findByUser(
                userRepository.findById(userId).get()
            );
            
            // Get all events from those groups
            List<Event> allEvents = memberships.stream()
                .flatMap(gm -> eventRepository.findByGroup(gm.getGroup()).stream())
                .distinct()
                .collect(Collectors.toList());
            
            // Convert to simple response format
            List<Map<String, Object>> events = allEvents.stream().map(e -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", e.getId());
                map.put("title", e.getTitle());
                map.put("description", e.getDescription());
                map.put("dateTime", e.getDateTime().toString());
                map.put("location", e.getLocation());
                map.put("groupId", e.getGroup().getId());
                map.put("groupName", e.getGroup().getName());
                map.put("organizerId", e.getOrganizer().getId());
                map.put("reminderMinutes", e.getReminderMinutes());
                return map;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Event event = eventRepository.findById(id)
                .orElseThrow(() -> new Exception("Event not found"));
            
            event.setTitle(payload.get("title").toString());
            event.setDescription(payload.get("description").toString());
            event.setDateTime(LocalDateTime.parse(payload.get("dateTime").toString()));
            event.setLocation(payload.getOrDefault("location", "").toString());
            
            if (payload.containsKey("reminderMinutes") && !payload.get("reminderMinutes").toString().isEmpty()) {
                event.setReminderMinutes(Integer.valueOf(payload.get("reminderMinutes").toString()));
            } else {
                event.setReminderMinutes(null);
            }
            
            if (payload.containsKey("groupId")) {
                Long groupId = Long.valueOf(payload.get("groupId").toString());
                event.setGroup(groupRepository.findById(groupId)
                    .orElseThrow(() -> new Exception("Group not found")));
            }
            
            Event updated = eventRepository.save(event);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", updated.getId());
            response.put("title", updated.getTitle());
            response.put("description", updated.getDescription());
            response.put("dateTime", updated.getDateTime().toString());
            response.put("location", updated.getLocation());
            response.put("groupId", updated.getGroup().getId());
            response.put("groupName", updated.getGroup().getName());
            response.put("reminderMinutes", updated.getReminderMinutes());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        try {
            eventRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{eventId}/rsvp")
    public ResponseEntity<?> rsvpEvent(@PathVariable Long eventId, @RequestBody Map<String, String> payload) {
        try {
            Long userId = userRepository.findAll().get(0).getId();
            String status = payload.get("status");
            
            EventResponse er = eventService.respondToEvent(eventId, userId, status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", er.getId());
            response.put("status", er.getResponse());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{eventId}/rsvps")
    public ResponseEntity<?> getEventRSVPs(@PathVariable Long eventId) {
        try {
            List<EventResponse> responses = eventService.getEventResponses(eventId);
            
            List<Map<String, Object>> rsvps = responses.stream().map(r -> {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", r.getUser().getId());
                map.put("username", r.getUser().getUsername());
                map.put("status", r.getResponse());
                return map;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(rsvps);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}