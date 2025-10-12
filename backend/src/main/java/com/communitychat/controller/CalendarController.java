package com.communitychat.controller;

import com.communitychat.model.entity.Event;
import com.communitychat.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    @Autowired
    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping("/upcoming/{userId}")
    public ResponseEntity<?> getUpcomingEvents(@PathVariable Long userId) {
        try {
            List<Event> events = calendarService.getUpcomingEvents(userId);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/range/{userId}")
    public ResponseEntity<?> getEventsInRange(@PathVariable Long userId,
                                              @RequestParam String from,
                                              @RequestParam String to) {
        try {
            LocalDateTime start = LocalDateTime.parse(from);
            LocalDateTime end = LocalDateTime.parse(to);
            List<Event> events = calendarService.getEventsInRange(userId, start, end);
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
