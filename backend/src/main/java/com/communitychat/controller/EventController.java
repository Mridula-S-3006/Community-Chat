package com.communitychat.controller;

import com.communitychat.model.entity.Event;
import com.communitychat.model.entity.EventResponse;
import com.communitychat.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestParam Long creatorId,
                                         @RequestParam String title,
                                         @RequestParam String description,
                                         @RequestParam String startTime,
                                         @RequestParam String endTime) {
        try {
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);
            Event event = eventService.createEvent(creatorId, title, description, start, end);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserEvents(@PathVariable Long userId) {
        try {
            List<Event> events = eventService.getUserEvents(userId);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{eventId}/respond")
    public ResponseEntity<?> respondToEvent(@PathVariable Long eventId,
                                            @RequestParam Long userId,
                                            @RequestParam String response) {
        try {
            EventResponse er = eventService.respondToEvent(eventId, userId, response);
            return ResponseEntity.ok(er);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{eventId}/responses")
    public ResponseEntity<?> getEventResponses(@PathVariable Long eventId) {
        try {
            List<EventResponse> responses = eventService.getEventResponses(eventId);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
